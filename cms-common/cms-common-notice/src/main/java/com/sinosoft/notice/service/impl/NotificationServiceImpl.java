package com.sinosoft.notice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateEngine;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sinosoft.common.core.utils.DateUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.notice.constant.NotificationTypeConstants;
import com.sinosoft.notice.core.enums.NotificationStatus;
import com.sinosoft.notice.core.exception.NotificationException;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import com.sinosoft.notice.domain.SysNotificationUser;
import com.sinosoft.notice.domain.dto.NotificationDTO;
import com.sinosoft.notice.domain.vo.NotificationVO;
import com.sinosoft.notice.mapper.SysNotificationMapper;
import com.sinosoft.notice.mapper.SysNotificationTemplateMapper;
import com.sinosoft.notice.mapper.SysNotificationUserMapper;
import com.sinosoft.notice.model.NotificationPayload;
import com.sinosoft.notice.service.INotificationService;
import com.sinosoft.notice.service.IRecipientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 *
 * @author zzf
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final SysNotificationMapper notificationMapper;
    private final SysNotificationUserMapper notificationUserMapper;
    private final SysNotificationTemplateMapper templateMapper;
    private final IRecipientService recipientService;
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

        // 5. 处理业务键（用于后续可能的识别）
        String businessKey = generateBusinessKey(payload, template);
        if (StringUtils.isNotEmpty(businessKey)) {
            notification.setBusinessKey(businessKey);
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
        sendNotificationToChannels(notification, recipientUserIds, template, templateParams);

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

        // 5. 处理业务键（用于后续可能的识别）
        String businessKey = generateBusinessKey(payload, template);
        if (StringUtils.isNotEmpty(businessKey)) {
            notification.setBusinessKey(businessKey);
        }

        // 6. 保存新通知
        notificationMapper.insert(notification);

        // 7. 创建通知接收记录
        recipientService.createNotificationUserRecords(notification.getNotificationId(), specificUserIds);

        // 8. 发送通知到各个渠道
        sendNotificationToChannels(notification, specificUserIds, template, templateParams);

        return notification.getNotificationId();
    }

    /**
     * 查询用户通知列表（分页）
     */
    @Override
    public TableDataInfo<NotificationVO> getNotifications(NotificationDTO dto, PageQuery pageQuery) {
        if (dto.getUserId() == null) {
            throw new NotificationException("400", "用户ID不能为空");
        }

        Long userId = dto.getUserId();
        String type = dto.getType();
        Boolean isRead = dto.getIsRead();
        String keyword = dto.getKeyword();
        String startTime = dto.getStartTime();
        String endTime = dto.getEndTime();

        try {
            // 1. 查询用户通知ID列表
            LambdaQueryWrapper<SysNotificationUser> userQuery = new LambdaQueryWrapper<>();
            userQuery.eq(SysNotificationUser::getUserId, userId);

            Page<SysNotificationUser> userPage = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
            Page<SysNotificationUser> userResult = notificationUserMapper.selectPage(userPage, userQuery);

            List<Long> notificationIds = userResult.getRecords().stream()
                .map(SysNotificationUser::getNotificationId)
                .collect(Collectors.toList());

            if (notificationIds.isEmpty()) {
                return new TableDataInfo<>(new ArrayList<NotificationVO>(), 0);
            }

            // 2. 构建通知查询条件
            LambdaQueryWrapper<SysNotification> notificationQuery = new LambdaQueryWrapper<>();
            notificationQuery.in(SysNotification::getNotificationId, notificationIds);

            // 按类型筛选
            if (StrUtil.isNotBlank(type)) {
                notificationQuery.eq(SysNotification::getType, type);
            }

            // 按关键词筛选
            if (StrUtil.isNotBlank(keyword)) {
                notificationQuery.and(wrapper -> wrapper
                    .like(SysNotification::getTitle, keyword)
                    .or()
                    .like(SysNotification::getContent, keyword));
            }

            // 按日期筛选
            if (StrUtil.isNotBlank(startTime) && StrUtil.isNotBlank(endTime)) {
                Date startDate = DateUtils.parseDate(startTime);
                Date endDate = DateUtils.parseDate(endTime);
                notificationQuery.between(SysNotification::getCreateTime, startDate, endDate);
            }

            // 按状态筛选
            notificationQuery.eq(SysNotification::getStatus, NotificationStatus.NORMAL.getCode());

            // 设置排序：先按优先级，再按创建时间倒序
            notificationQuery.orderByAsc(SysNotification::getPriority);
            notificationQuery.orderByDesc(SysNotification::getCreateTime);

            // 3. 查询通知列表
            List<SysNotification> notifications = notificationMapper.selectList(notificationQuery);

            // 4. 查询用户通知关系，获取已读状态
            Map<Long, Boolean> readStatusMap = new HashMap<>();
            Map<Long, Date> readTimeMap = new HashMap<>();

            userResult.getRecords().forEach(userNotice -> {
                readStatusMap.put(userNotice.getNotificationId(), "1".equals(userNotice.getIsRead()));
                readTimeMap.put(userNotice.getNotificationId(), userNotice.getReadTime());
            });

            // 5. 转换为VO并设置已读状态
            List<NotificationVO> voList = notifications.stream()
                .map(notification -> {
                    NotificationVO vo = BeanUtil.copyProperties(notification, NotificationVO.class);
                    vo.setRead(readStatusMap.getOrDefault(notification.getNotificationId(), false));
                    vo.setReadTime(readTimeMap.get(notification.getNotificationId()));
                    return vo;
                })
                .collect(Collectors.toList());

            // 6. 如果需要按已读状态筛选，在内存中过滤
            if (isRead != null) {
                voList = voList.stream()
                    .filter(vo -> vo.getRead() == isRead)
                    .collect(Collectors.toList());
            }

            // 7. 计算总数
            long total = voList.size();

            return new TableDataInfo<>(voList, (int) total);
        } catch (Exception e) {
            log.error("查询用户通知列表异常", e);
            throw new NotificationException("500", "查询通知列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户未读通知统计
     */
    @Override
    public Map<String, Integer> getUnreadCount(Long userId) {
        Map<String, Integer> result = new HashMap<>();

        try {
            // 获取用户所有未读通知
            LambdaQueryWrapper<SysNotificationUser> userQuery = new LambdaQueryWrapper<>();
            userQuery.eq(SysNotificationUser::getUserId, userId);
            userQuery.eq(SysNotificationUser::getIsRead, "0");

            List<SysNotificationUser> userNotifications = notificationUserMapper.selectList(userQuery);

            if (userNotifications.isEmpty()) {
                result.put("total", 0);
                result.put("todo", 0);
                result.put("alert", 0);
                result.put("message", 0);
                result.put("announcement", 0);
                return result;
            }

            // 获取通知ID列表
            List<Long> notificationIds = userNotifications.stream()
                .map(SysNotificationUser::getNotificationId)
                .collect(Collectors.toList());

            // 查询通知详情
            LambdaQueryWrapper<SysNotification> notificationQuery = new LambdaQueryWrapper<>();
            notificationQuery.in(SysNotification::getNotificationId, notificationIds);
            notificationQuery.eq(SysNotification::getStatus, NotificationStatus.NORMAL.getCode());

            List<SysNotification> notifications = notificationMapper.selectList(notificationQuery);

            // 按类型统计
            int todoCount = 0;
            int messageCount = 0;
            int announcementCount = 0;

            for (SysNotification notification : notifications) {
                String type = notification.getType();
                if (NotificationTypeConstants.TYPE_TODO.equals(type)) {
                    todoCount++;
                } else if (NotificationTypeConstants.TYPE_MESSAGE.equals(type)) {
                    messageCount++;
                } else if (NotificationTypeConstants.TYPE_ANNOUNCEMENT.equals(type)) {
                    announcementCount++;
                }
            }

            // 汇总结果
            int totalCount = notifications.size();

            result.put("total", totalCount);
            result.put("todo", todoCount);
            result.put("message", messageCount);
            result.put("announcement", announcementCount);

            return result;
        } catch (Exception e) {
            log.error("获取未读通知统计异常: {}", e.getMessage(), e);
            // 返回空结果，避免前端错误
            result.put("total", 0);
            result.put("todo", 0);
            result.put("message", 0);
            result.put("announcement", 0);
            return result;
        }
    }

    /**
     * 标记通知为已读
     */
    @Override
    public boolean markAsRead(Long userId, Long notificationId) {
        // 查询用户通知关系
        LambdaQueryWrapper<SysNotificationUser> query = new LambdaQueryWrapper<>();
        query.eq(SysNotificationUser::getUserId, userId);
        query.eq(SysNotificationUser::getNotificationId, notificationId);

        SysNotificationUser userNotification = notificationUserMapper.selectOne(query);

        if (userNotification == null) {
            log.warn("通知接收记录不存在, 用户ID: {}, 通知ID: {}", userId, notificationId);
            return false;
        }

        if ("1".equals(userNotification.getIsRead())) {
            // 已经是已读状态
            return true;
        }

        // 更新为已读
        userNotification.setIsRead("1");
        userNotification.setReadTime(new Date());
        userNotification.setUpdateTime(new Date());

        return notificationUserMapper.updateById(userNotification) > 0;
    }

    /**
     * 批量标记通知为已读
     */
    @Override
    public int batchMarkAsRead(Long userId, List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Long notificationId : notificationIds) {
            if (markAsRead(userId, notificationId)) {
                count++;
            }
        }

        return count;
    }

    /**
     * 标记所有通知为已读
     */
    @Override
    public int markAllAsRead(Long userId, String type) {
        Date now = new Date();

        if (StringUtils.isEmpty(type)) {
            // 标记所有类型通知为已读
            return notificationUserMapper.markAllAsRead(userId, now);
        } else {
            // 标记指定类型通知为已读
            return notificationUserMapper.markAllAsReadByType(userId, type, now);
        }
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
     * 发送通知到各个渠道
     */
    private void sendNotificationToChannels(SysNotification notification,
                                            List<Long> userIds,
                                            SysNotificationTemplate template,
                                            Map<String, Object> templateParams) {
        // 这里可以添加发送到不同渠道的逻辑：短信、邮件等
        // 本实现中，只记录在数据库中
        log.info("发送通知到{}个用户: {}", userIds.size(), notification.getTitle());
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
     * 复制权限信息
     */
    private void copyPermissionInfo(SysNotification notification, SysNotificationTemplate template) {
        notification.setRoleIds(template.getRoleIds());
        notification.setMenuPerms(template.getMenuPerms());
        // 数据范围SQL可能需要特殊处理
    }
}
