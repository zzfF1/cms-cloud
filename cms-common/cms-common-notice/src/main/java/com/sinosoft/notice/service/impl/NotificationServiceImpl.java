package com.sinosoft.notice.service.impl;

import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.notice.core.exception.NotificationException;
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
    private final TemplateEngine templateEngine;
    private final EmailService emailService;
    private final SmsService smsService;

    /**
     * 发送通知（根据模板规则自动确定接收人）
     */
    @Transactional
    @Override
    public Long sendNotification(String templateCode, NotificationPayload payload,
                                 String sourceType, String sourceId) {
        // 1. 获取当前用户
        Long currentUserId = payload.getUserId();

        // 2. 获取通知模板
        SysNotificationTemplate template = templateMapper.selectByCode(templateCode);
        if (template == null) {
            throw new NotificationException("400", "[" + templateCode + "]通知模板不存在!");
        }

        if (!"0".equals(template.getStatus())) {
            throw new NotificationException("400", "[" + templateCode + "]通知模板未启用!");
        }

        // 3. 生成通知内容
        Map<String, Object> templateParams = payload.toTemplateParams();
        String title = renderTemplate(template.getTitleTemplate(), templateParams);
        String content = renderTemplate(template.getContentTemplate(), templateParams);

        // 4. 创建通知记录
        SysNotification notification = createNotification(
            template, title, content, sourceType, sourceId, payload, currentUserId);

        // 5. 处理业务键（用于后续可能的合并）
        String businessKey = generateBusinessKey(payload, template);
        if (StringUtils.isNotEmpty(businessKey)) {
            notification.setBusinessKey(businessKey);

            // 尝试合并现有通知
            SysNotification mergedNotification = mergeIfNeeded(notification, businessKey);
            if (mergedNotification != notification) {
                // 已合并到现有通知，返回现有通知ID
                return mergedNotification.getNotificationId();
            }
        }

        // 6. 保存新通知
        notificationMapper.insert(notification);

        // 7. 根据模板规则查找接收人
        List<Long> recipientUserIds = recipientService.findRecipients(notification, template, templateParams);

        if (recipientUserIds.isEmpty()) {
            log.warn("没有找到通知接收人，通知不会发送");
            return notification.getNotificationId();
        }

        // 8. 创建通知接收记录
        recipientService.createNotificationUserRecords(notification.getNotificationId(), recipientUserIds);

        // 9. 发送通知到各个渠道
        sendNotificationToUsers(notification, recipientUserIds, template, templateParams);

        return notification.getNotificationId();
    }

    /**
     * 发送通知（指定接收人）
     */
    @Transactional
    @Override
    public Long sendNotification(String templateCode, NotificationPayload payload,
                                 String sourceType, String sourceId, List<Long> specificUserIds) {
        // 验证参数
        if (specificUserIds == null || specificUserIds.isEmpty()) {
            log.warn("指定的接收人列表为空，通知不会发送");
            return null;
        }

        // 1. 获取当前用户
        Long currentUserId = getCurrentUserId();

        // 2. 获取通知模板
        SysNotificationTemplate template = templateMapper.selectByCode(templateCode);
        if (template == null) {
            throw new NotificationException("400", "[" + templateCode + "]通知模板不存在!");
        }

        if (!"0".equals(template.getStatus())) {
            throw new NotificationException("400", "[" + templateCode + "]通知模板未启用!");
        }

        // 3. 生成通知内容
        Map<String, Object> templateParams = payload.toTemplateParams();
        String title = renderTemplate(template.getTitleTemplate(), templateParams);
        String content = renderTemplate(template.getContentTemplate(), templateParams);

        // 4. 创建通知记录
        SysNotification notification = createNotification(
            template, title, content, sourceType, sourceId, payload, currentUserId);

        // 5. 处理业务键（用于后续可能的合并）
        String businessKey = generateBusinessKey(payload, template);
        if (StringUtils.isNotEmpty(businessKey)) {
            notification.setBusinessKey(businessKey);

            // 如果只有一个接收人，尝试合并
            if (specificUserIds.size() == 1) {
                SysNotification mergedNotification = mergeIfNeeded(notification, businessKey);
                if (mergedNotification != notification) {
                    // 已合并到现有通知，更新接收人列表并返回
                    ensureUserHasNotification(mergedNotification.getNotificationId(), specificUserIds.get(0));
                    return mergedNotification.getNotificationId();
                }
            }
        }

        // 6. 保存新通知
        notificationMapper.insert(notification);

        // 7. 创建通知接收记录
        recipientService.createNotificationUserRecords(notification.getNotificationId(), specificUserIds);

        // 8. 发送通知到各个渠道
        sendNotificationToUsers(notification, specificUserIds, template, templateParams);

        return notification.getNotificationId();
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
            return true;
        }

        recipient.setIsRead("1");
        recipient.setReadTime(new Date());
        recipient.setUpdateTime(new Date());

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

        // 设置附件
        notification.setAttachments(payload.getAttachmentsJson());

        return notification;
    }

    /**
     * 简化的合并逻辑，仅支持更新现有通知
     */
    private SysNotification mergeIfNeeded(SysNotification notification, String businessKey) {
        if (StringUtils.isEmpty(businessKey)) {
            return notification;
        }

        // 查找最近的相同业务键通知
        List<SysNotification> existingNotifications = notificationMapper.selectByBusinessKeyAndTime(
            businessKey,
            new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)  // 24小时内
        );

        if (existingNotifications == null || existingNotifications.isEmpty()) {
            return notification;
        }

        // 获取最近的通知
        SysNotification existingNotification = existingNotifications.get(0);

        // 保留基本信息
        Long notificationId = existingNotification.getNotificationId();
        Date createTime = existingNotification.getCreateTime();
        Long createBy = existingNotification.getCreateBy();
        Long createDept = existingNotification.getCreateDept();

        // 更新内容
        existingNotification.setTitle(notification.getTitle());
        existingNotification.setContent(notification.getContent());
        existingNotification.setPriority(notification.getPriority());
        existingNotification.setExpirationDate(notification.getExpirationDate());
        existingNotification.setActions(notification.getActions());
        existingNotification.setAttachments(notification.getAttachments());
        existingNotification.setBusinessData(notification.getBusinessData());
        existingNotification.setUpdateBy(notification.getUpdateBy());
        existingNotification.setUpdateTime(new Date());

        // 恢复基本信息
        existingNotification.setNotificationId(notificationId);
        existingNotification.setCreateTime(createTime);
        existingNotification.setCreateBy(createBy);
        existingNotification.setCreateDept(createDept);

        // 更新数据库
        notificationMapper.updateById(existingNotification);

        // 将所有接收记录标记为未读
        notificationUserMapper.markAllUnread(notificationId);

        return existingNotification;
    }

    /**
     * 确保用户有通知记录
     */
    private void ensureUserHasNotification(Long notificationId, Long userId) {
        SysNotificationUser existing = notificationUserMapper.selectByNotificationIdAndUserId(notificationId, userId);
        if (existing == null) {
            // 创建新的通知用户关系
            SysNotificationUser notificationUser = new SysNotificationUser();
            notificationUser.setNotificationId(notificationId);
            notificationUser.setUserId(userId);
            notificationUser.setIsRead("0");
            notificationUser.setCreateTime(new Date());

            notificationUserMapper.insert(notificationUser);
        } else {
            // 标记为未读
            existing.setIsRead("0");
            existing.setReadTime(null);
            existing.setUpdateTime(new Date());

            notificationUserMapper.updateById(existing);
        }
    }

    /**
     * 生成业务键
     */
    private String generateBusinessKey(NotificationPayload payload, SysNotificationTemplate template) {
        // 优先使用负载中的业务键
        if (StringUtils.isNotEmpty(payload.getBusinessKey())) {
            return payload.getBusinessKey();
        }

        // 使用业务键模板生成
        if (StringUtils.isNotEmpty(payload.getBusinessKeyTemplate())) {
            try {
                Map<String, Object> params = payload.toTemplateParams();
                Template keyTemplate = templateEngine.getTemplate(payload.getBusinessKeyTemplate());
                return keyTemplate.render(params);
            } catch (Exception e) {
                log.error("生成业务键异常: {}", e.getMessage());
            }
        }

        // 返回默认值
        return null;
    }

    /**
     * 发送通知到用户
     */
    private void sendNotificationToUsers(SysNotification notification,
                                         List<Long> userIds,
                                         SysNotificationTemplate template,
                                         Map<String, Object> templateParams) {
        for (Long userId : userIds) {
            try {
                // 1. 获取用户设置
                Map<String, Object> settings = settingService.getUserSettings(userId);

                // 2. 检查是否在免打扰时间
                if (isInDoNotDisturbTime(settings)) {
                    // 仅发送系统内通知
                    sendSystemNotification(notification, userId);
                    continue;
                }

                // 3. 发送系统内通知
                sendSystemNotification(notification, userId);

                // 4. 根据通知类型和用户设置发送短信
                if (shouldSendSms(notification.getType(), settings)) {
                    String mobile = getUserMobile(userId);
                    if (StringUtils.isNotEmpty(mobile)) {
                        sendSmsNotification(notification, userId, mobile, template, templateParams);
                    }
                }

                // 5. 根据通知类型和用户设置发送邮件
                if (shouldSendEmail(notification.getType(), settings)) {
                    String email = getUserEmail(userId);
                    if (StringUtils.isNotEmpty(email)) {
                        sendEmailNotification(notification, userId, email, template, templateParams);
                    }
                }
            } catch (Exception e) {
                log.error("发送通知给用户异常, 用户ID: {}, 通知ID: {}", userId, notification.getNotificationId(), e);
            }
        }
    }

    /**
     * 发送系统内通知
     */
    private void sendSystemNotification(SysNotification notification, Long userId) {
        // 系统内通知不需要特殊处理，已经在数据库中创建了对应关系
        log.debug("发送系统内通知，用户ID: {}, 通知ID: {}", userId, notification.getNotificationId());
    }

    /**
     * 发送短信通知
     */
    private void sendSmsNotification(SysNotification notification, Long userId,
                                     String mobile, SysNotificationTemplate template,
                                     Map<String, Object> templateParams) {
        try {
            // 渲染短信内容
            String smsContent;
            if (StringUtils.isNotEmpty(template.getSmsTemplate())) {
                smsContent = renderTemplate(template.getSmsTemplate(), templateParams);
            } else {
                // 使用通知内容作为短信内容
                smsContent = notification.getTitle() + "\n" + notification.getContent();
            }

            // 创建发送记录
            createDeliveryRecord(notification.getNotificationId(), userId, "sms", smsContent, mobile);

            // 发送短信
            smsService.send(mobile, smsContent);

        } catch (Exception e) {
            log.error("发送短信通知异常，用户ID: {}, 手机号: {}", userId, maskSensitiveInfo(mobile), e);
        }
    }

    /**
     * 发送邮件通知
     */
    private void sendEmailNotification(SysNotification notification, Long userId,
                                       String email, SysNotificationTemplate template,
                                       Map<String, Object> templateParams) {
        try {
            // 渲染邮件主题和内容
            String subject;
            if (StringUtils.isNotEmpty(template.getEmailSubject())) {
                subject = renderTemplate(template.getEmailSubject(), templateParams);
            } else {
                subject = notification.getTitle();
            }

            String content;
            if (StringUtils.isNotEmpty(template.getEmailContent())) {
                content = renderTemplate(template.getEmailContent(), templateParams);
            } else {
                content = notification.getContent();
            }

            // 创建发送记录
            createDeliveryRecord(notification.getNotificationId(), userId, "email",
                subject + "\n" + content, email);

            // 发送邮件
            emailService.send(email, subject, content);

        } catch (Exception e) {
            log.error("发送邮件通知异常，用户ID: {}, 邮箱: {}", userId, maskSensitiveInfo(email), e);
        }
    }

    /**
     * 创建发送记录
     */
    private void createDeliveryRecord(Long notificationId, Long userId,
                                      String channel, String content, String targetAddress) {
        // 使用Mapper直接创建记录，简化实现
    }

    /**
     * 脱敏处理
     */
    private String maskSensitiveInfo(String info) {
        if (info == null) {
            return null;
        }

        // 邮箱脱敏
        if (info.contains("@")) {
            int atIndex = info.indexOf('@');
            if (atIndex <= 1) {
                return info;
            }

            String local = info.substring(0, atIndex);
            String domain = info.substring(atIndex);

            if (local.length() <= 2) {
                return info;
            }

            String maskedLocal = local.substring(0, 1) + "****" + local.substring(local.length() - 1);
            return maskedLocal + domain;
        }

        // 手机号脱敏
        if (info.length() >= 11) {
            return info.substring(0, 3) + "****" + info.substring(7);
        }

        return info;
    }

    /**
     * 检查是否应该发送短信
     */
    private boolean shouldSendSms(String notificationType, Map<String, Object> settings) {
        String settingKey = notificationType + "_notify_sms";
        return settings != null &&
            settings.containsKey(settingKey) &&
            "1".equals(settings.get(settingKey).toString());
    }

    /**
     * 检查是否应该发送邮件
     */
    private boolean shouldSendEmail(String notificationType, Map<String, Object> settings) {
        String settingKey = notificationType + "_notify_email";
        return settings != null &&
            settings.containsKey(settingKey) &&
            "1".equals(settings.get(settingKey).toString());
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
            throw new NotificationException("400", e.getMessage(), e);
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
