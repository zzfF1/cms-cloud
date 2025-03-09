package com.sinosoft.notice.service.impl;

import com.sinosoft.notice.core.event.DomainEventPublisher;
import com.sinosoft.notice.core.event.NotificationCreatedEvent;
import com.sinosoft.notice.core.exception.NotificationErrorCode;
import com.sinosoft.notice.core.exception.TemplateDisabledException;
import com.sinosoft.notice.core.exception.TemplateNotFoundException;
import com.sinosoft.notice.core.exception.TemplateRenderException;
import com.sinosoft.notice.core.factory.NotificationChannelFactory;
import com.sinosoft.notice.core.factory.NotificationMergeStrategyFactory;
import com.sinosoft.notice.core.strategy.NotificationChannelStrategy;
import com.sinosoft.notice.core.strategy.NotificationMergeStrategy;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import com.sinosoft.notice.domain.SysNotificationUser;
import com.sinosoft.notice.mapper.SysNotificationMapper;
import com.sinosoft.notice.mapper.SysNotificationTemplateMapper;
import com.sinosoft.notice.mapper.SysNotificationUserMapper;
import com.sinosoft.notice.model.NotificationPayload;
import com.sinosoft.notice.service.INotificationService;
import com.sinosoft.notice.service.INotificationSettingService;
import com.sinosoft.notice.service.IRecipientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateEngine;
import com.alibaba.fastjson2.JSON;

/**
 * 通知服务实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements INotificationService {

    private final SysNotificationMapper notificationMapper;
    private final SysNotificationTemplateMapper templateMapper;
    private final SysNotificationUserMapper notificationUserMapper;
    private final IRecipientService recipientService;
    private final INotificationSettingService settingService;
    private final NotificationChannelFactory channelFactory;
    private final NotificationMergeStrategyFactory mergeStrategyFactory;
    private final TemplateEngine templateEngine;
    private final DomainEventPublisher eventPublisher;
    private final EmailService emailService;
    private final SmsService smsService;

    /**
     * 发送通知
     */
    @Transactional
    @Override
    public Long sendNotification(String templateCode, NotificationPayload payload,
                                 String sourceType, String sourceId, List<Long> specificUserIds) {
        // 1. 获取当前用户
        Long currentUserId = getCurrentUserId();

        // 2. 获取通知模板
        SysNotificationTemplate template = templateMapper.selectByCode(templateCode);
        if (template == null) {
            throw new TemplateNotFoundException(templateCode);
        }

        if (!"0".equals(template.getStatus())) {
            throw new TemplateDisabledException(templateCode);
        }

        // 3. 设置合并相关信息
        preparePayloadFromTemplate(payload, template);

        // 4. 生成通知内容
        Map<String, Object> templateParams = payload.toTemplateParams();
        String title = renderTemplate(template.getTitleTemplate(), templateParams);
        String content = renderTemplate(template.getContentTemplate(), templateParams);

        // 5. 创建通知记录
        SysNotification notification = createNotification(
            template, title, content, sourceType, sourceId, payload, currentUserId);

        // 6. 处理通知合并逻辑
        if (specificUserIds != null && specificUserIds.size() == 1) {
            // 单一用户的情况，直接进行合并处理
            return processSingleUserNotification(template, notification, payload, specificUserIds.get(0));
        } else {
            // 多用户或按权限规则筛选的情况
            return processMultiUserNotification(template, notification, payload, specificUserIds);
        }
    }

    /**
     * 获取用户通知列表
     */
    @Override
    public List<SysNotification> getUserNotifications(Long userId, String type, Boolean isRead, int pageNum, int pageSize) {
        // 构建查询条件
        SysNotificationUser query = new SysNotificationUser();
        query.setUserId(userId);

        if (isRead != null) {
            query.setIsRead(isRead ? "1" : "0");
        }

        // 分页参数
        int offset = (pageNum - 1) * pageSize;

        // 查询用户通知
        List<SysNotificationUser> notificationUsers;
        if (type != null) {
            notificationUsers = notificationUserMapper.selectUserNotificationsByType(query, type, offset, pageSize);
        } else {
            notificationUsers = notificationUserMapper.selectUserNotifications(query, offset, pageSize);
        }

        if (notificationUsers == null || notificationUsers.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取通知ID列表
        List<Long> notificationIds = notificationUsers.stream()
            .map(SysNotificationUser::getNotificationId)
            .collect(Collectors.toList());

        // 查询通知详情
        return notificationMapper.selectBatchIds(notificationIds);
    }

    /**
     * 标记通知为已读
     */
    @Transactional
    @Override
    public boolean markAsRead(Long userId, Long notificationId) {
        SysNotificationUser recipient = notificationUserMapper.selectByNotificationIdAndUserId(notificationId, userId);

        if (recipient == null) {
            log.warn("通知接收记录不存在, 用户ID: {}, 通知ID: {}", userId, notificationId);
            return false;
        }

        if ("1".equals(recipient.getIsRead())) {
            // 已经是已读状态
            return false;
        }

        recipient.setIsRead("1");
        recipient.setReadTime(new Date());

        notificationUserMapper.updateById(recipient);
        log.info("标记通知为已读，用户ID: {}, 通知ID: {}", userId, notificationId);

        return true;
    }

    /**
     * 标记所有通知为已读
     */
    @Transactional
    @Override
    public int markAllAsRead(Long userId) {
        int count = notificationUserMapper.markAllAsRead(userId, new Date());
        log.info("标记所有通知为已读，用户ID: {}, 更新数量: {}", userId, count);

        return count;
    }

    /**
     * 获取未读通知数量
     */
    @Override
    public Map<String, Integer> getUnreadCount(Long userId) {
        // 查询各类型未读数量
        int todoCount = notificationUserMapper.countUnreadByType(userId, "todo");
        int alertCount = notificationUserMapper.countUnreadByType(userId, "alert");
        int announcementCount = notificationUserMapper.countUnreadByType(userId, "announcement");
        int totalCount = todoCount + alertCount + announcementCount;

        Map<String, Integer> result = new HashMap<>();
        result.put("total", totalCount);
        result.put("todo", todoCount);
        result.put("alert", alertCount);
        result.put("announcement", announcementCount);

        return result;
    }

    /**
     * 创建基本通知对象
     */
    private SysNotification createNotification(
        SysNotificationTemplate template, String title, String content,
        String sourceType, String sourceId, NotificationPayload payload, Long currentUserId) {

        SysNotification notification = new SysNotification();
        notification.setNotificationId(generateNotificationId());
        notification.setTemplateId(template.getTemplateId());
        notification.setType(template.getType());
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSourceType(sourceType);
        notification.setSourceId(sourceId);
        notification.setPriority(Boolean.TRUE.equals(payload.getUrgent()) ?
            "high" : template.getPriority());
        notification.setBusinessData(toJsonString(payload.toTemplateParams()));
        notification.setStatus("0"); // 正常状态

        // 设置业务键（用于合并判断）
        String businessKey = payload.generateBusinessKey();
        if (businessKey != null && !businessKey.isEmpty()) {
            notification.setBusinessKey(businessKey);
        }

        // 设置过期时间
        if (template.getValidDays() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, template.getValidDays());
            notification.setExpirationDate(calendar.getTime());
        }

        // 设置创建者信息
        notification.setCreateBy(currentUserId);
        notification.setCreateTime(new Date());

        // 复制模板中的权限信息
        copyPermissionInfo(notification, template);

        // 设置操作
        notification.setActions(payload.getActionsJson());

        return notification;
    }

    /**
     * 处理单一用户通知
     */
    private Long processSingleUserNotification(
        SysNotificationTemplate template, SysNotification notification,
        NotificationPayload payload, Long userId) {

        // 1. 尝试合并通知
        Optional<NotificationMergeStrategy> mergeStrategy =
            getMergeStrategy(template, payload);

        if (mergeStrategy.isPresent()) {
            // 获取合并窗口
            Date mergeWindowStart = getMergeWindowStart();

            // 查找可合并的通知
            List<SysNotification> existingNotifications =
                mergeStrategy.get().findMergeableNotifications(
                    notification.getBusinessKey(), userId, mergeWindowStart);

            // 如果找到可合并的通知，进行合并
            if (existingNotifications != null && !existingNotifications.isEmpty()) {
                SysNotification existingNotification = existingNotifications.get(0);
                SysNotification mergedNotification = mergeStrategy.get().merge(
                    existingNotification, notification, template, payload);

                // 如果返回的是原通知（合并成功）
                if (mergedNotification.getNotificationId().equals(existingNotification.getNotificationId())) {
                    // 发送通知
                    sendNotificationToUser(mergedNotification, userId, template, payload.toTemplateParams());
                    return mergedNotification.getNotificationId();
                }
            }
        }

        // 2. 如果没有合并或合并失败，创建新通知
        notificationMapper.insert(notification);

        // 3. 创建通知接收记录
        recipientService.createNotificationUserRecords(notification.getNotificationId(),
            Collections.singletonList(userId));

        // 4. 发送通知
        sendNotificationToUser(notification, userId, template, payload.toTemplateParams());

        // 5. 发布通知创建事件
        publishNotificationCreatedEvent(notification);

        return notification.getNotificationId();
    }

    /**
     * 处理多用户通知
     */
    private Long processMultiUserNotification(
        SysNotificationTemplate template, SysNotification notification,
        NotificationPayload payload, List<Long> specificUserIds) {

        // 1. 确定通知接收人
        List<Long> recipientUserIds;
        if (specificUserIds != null && !specificUserIds.isEmpty()) {
            // 使用指定的用户列表
            recipientUserIds = specificUserIds;
        } else {
            // 根据权限规则匹配用户
            recipientUserIds = recipientService.findRecipients(notification, template, null);
        }

        if (recipientUserIds.isEmpty()) {
            log.warn("没有找到通知接收人，通知不会发送");
            return null;
        }

        // 2. 保存通知记录
        notificationMapper.insert(notification);

        // 3. 创建通知接收记录
        recipientService.createNotificationUserRecords(notification.getNotificationId(), recipientUserIds);

        // 4. 发送通知到各个用户
        for (Long userId : recipientUserIds) {
            sendNotificationToUser(notification, userId, template, payload.toTemplateParams());
        }

        // 5. 发布通知创建事件
        publishNotificationCreatedEvent(notification);

        return notification.getNotificationId();
    }

    /**
     * 发布通知创建事件
     */
    private void publishNotificationCreatedEvent(SysNotification notification) {
        eventPublisher.publish(new NotificationCreatedEvent(
            notification.getNotificationId(),
            notification.getType(),
            notification.getTitle(),
            notification.getTemplateId(),
            notification.getBusinessKey()
        ));
    }

    /**
     * 获取合并策略
     */
    private Optional<NotificationMergeStrategy> getMergeStrategy(
        SysNotificationTemplate template, NotificationPayload payload) {

        // 优先使用负载对象中的策略
        String mergeStrategyCode = null;
        if (payload.getMergeStrategy() != null && !payload.getMergeStrategy().isEmpty()) {
            mergeStrategyCode = payload.getMergeStrategy();
        } else if (template != null && template.getMergeStrategy() != null &&
            !template.getMergeStrategy().isEmpty()) {
            mergeStrategyCode = template.getMergeStrategy();
        }

        return mergeStrategyFactory.getStrategy(mergeStrategyCode);
    }

    /**
     * 获取合并窗口起始时间（默认24小时内）
     */
    private Date getMergeWindowStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -24);
        return calendar.getTime();
    }

    /**
     * 从模板准备负载对象信息
     */
    private void preparePayloadFromTemplate(NotificationPayload payload, SysNotificationTemplate template) {
        if (payload.getMergeStrategy() == null || payload.getMergeStrategy().isEmpty()) {
            payload.setMergeStrategy(template.getMergeStrategy());
        }

        if (payload.getBusinessKeyTemplate() == null || payload.getBusinessKeyTemplate().isEmpty()) {
            payload.setBusinessKeyTemplate(template.getBusinessKeyTpl());
        }

        if (payload.getMergeTitleTemplate() == null || payload.getMergeTitleTemplate().isEmpty()) {
            payload.setMergeTitleTemplate(template.getMergeTitleTpl());
        }

        if (payload.getMergeContentTemplate() == null || payload.getMergeContentTemplate().isEmpty()) {
            payload.setMergeContentTemplate(template.getMergeContentTpl());
        }
    }

    /**
     * 渲染模板内容
     */
    private String renderTemplate(String templateContent, Map<String, Object> data) {
        if (templateContent == null || templateContent.isEmpty()) {
            return "";
        }

        try {
            // 使用Hutool的TemplateEngine
            Template template = templateEngine.getTemplate(templateContent);
            return template.render(data);
        } catch (Exception e) {
            log.error("渲染模板异常: {}", e.getMessage());
            throw new TemplateRenderException(e.getMessage(), e);
        }
    }

    /**
     * 转换为JSON字符串
     */
    private String toJsonString(Object obj) {
        try {
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            log.error("转换JSON异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 生成通知ID
     */
    private Long generateNotificationId() {
        // 实际项目中应该使用分布式ID生成器或数据库序列
        return System.currentTimeMillis() * 1000 + new Random().nextInt(1000);
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        // 实际应从上下文中获取
        return 1L;
    }

    /**
     * 复制权限信息
     */
    private void copyPermissionInfo(SysNotification notification, SysNotificationTemplate template) {
        notification.setRoleIds(template.getRoleIds());
        notification.setMenuPerms(template.getMenuPerms());
        // 数据范围SQL可能需要特殊处理
    }

    /**
     * 发送通知到指定用户
     */
    private void sendNotificationToUser(SysNotification notification, Long userId,
                                        SysNotificationTemplate template, Map<String, Object> templateParams) {
        // 1. 获取用户通知设置
        Map<String, Object> settings = settingService.getUserSettings(userId);

        // 2. 检查是否在免打扰时间
        if (isInDoNotDisturbTime(settings)) {
            log.debug("用户 {} 处于免打扰时间，仅发送系统内通知", userId);
            // 创建系统内通知
            createSystemNotification(notification, userId, templateParams);
            return;
        }

        // 3. 获取用户启用的通知渠道
        List<NotificationChannelStrategy> enabledChannels =
            channelFactory.getEnabledChannelsForUser(userId, settings, notification.getType());

        // 4. 通过各个渠道发送通知
        for (NotificationChannelStrategy channelStrategy : enabledChannels) {
            String channelCode = channelStrategy.getChannelCode();

            try {
                switch (channelCode) {
                    case "system":
                        createSystemNotification(notification, userId, templateParams);
                        break;
                    case "sms":
                        createSmsNotification(notification, userId, template, templateParams);
                        break;
                    case "email":
                        createEmailNotification(notification, userId, template, templateParams);
                        break;
                    default:
                        log.warn("未知的通知渠道: {}", channelCode);
                }
            } catch (Exception e) {
                log.error("通过渠道 {} 发送通知异常: {}", channelCode, e.getMessage(), e);
            }
        }
    }

    /**
     * 检查是否在免打扰时间
     */
    private boolean isInDoNotDisturbTime(Map<String, Object> settings) {
        Object startTimeObj = settings.get("do_not_disturb_start");
        Object endTimeObj = settings.get("do_not_disturb_end");

        if (startTimeObj == null || endTimeObj == null) {
            return false;
        }

        try {
            // 当前时间
            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            // 解析设置中的时间字符串
            String startTimeStr = startTimeObj.toString();
            String endTimeStr = endTimeObj.toString();

            // 免打扰开始时间
            String[] startParts = startTimeStr.split(":");
            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);

            // 免打扰结束时间
            String[] endParts = endTimeStr.split(":");
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            // 转换为分钟数进行比较
            int nowMinutes = hour * 60 + minute;
            int startMinutes = startHour * 60 + startMinute;
            int endMinutes = endHour * 60 + endMinute;

            if (startMinutes < endMinutes) {
                // 同一天内的时间段，如 22:00 - 23:00
                return nowMinutes >= startMinutes && nowMinutes <= endMinutes;
            } else {
                // 跨天的时间段，如 22:00 - 06:00
                return nowMinutes >= startMinutes || nowMinutes <= endMinutes;
            }
        } catch (Exception e) {
            log.error("解析免打扰时间异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 创建系统内通知
     */
    private void createSystemNotification(SysNotification notification, Long userId, Map<String, Object> templateParams) {
        NotificationChannelStrategy systemStrategy = channelFactory.getStrategy("system");
        systemStrategy.prepareDelivery(notification, userId, null, templateParams);
    }

    /**
     * 创建短信通知
     */
    private void createSmsNotification(SysNotification notification, Long userId,
                                       SysNotificationTemplate template, Map<String, Object> templateParams) {
        // 获取用户手机号
        String mobile = getUserMobile(userId);
        if (mobile == null || mobile.isEmpty()) {
            log.warn("用户 {} 没有设置手机号，无法发送短信通知", userId);
            return;
        }

        NotificationChannelStrategy smsStrategy = channelFactory.getStrategy("sms");
        smsStrategy.prepareDelivery(notification, userId, mobile, templateParams);
    }

    /**
     * 创建邮件通知
     */
    private void createEmailNotification(SysNotification notification, Long userId,
                                         SysNotificationTemplate template, Map<String, Object> templateParams) {
        // 获取用户邮箱
        String email = getUserEmail(userId);
        if (email == null || email.isEmpty()) {
            log.warn("用户 {} 没有设置邮箱，无法发送邮件通知", userId);
            return;
        }

        NotificationChannelStrategy emailStrategy = channelFactory.getStrategy("email");
        emailStrategy.prepareDelivery(notification, userId, email, templateParams);
    }

    /**
     * 获取用户手机号
     */
    private String getUserMobile(Long userId) {
        // 实际项目中应该从用户服务或用户表中获取
        return "13800138000";
    }

    /**
     * 获取用户邮箱
     */
    private String getUserEmail(Long userId) {
        // 实际项目中应该从用户服务或用户表中获取
        return "user@example.com";
    }
}
