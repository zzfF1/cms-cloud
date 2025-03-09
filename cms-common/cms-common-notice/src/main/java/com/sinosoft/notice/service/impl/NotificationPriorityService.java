package com.sinosoft.notice.service.impl;

import com.sinosoft.notice.core.priority.NotificationPriorityStrategy;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.mapper.SysNotificationMapper;
import com.sinosoft.notice.mapper.SysNotificationUserMapper;
import com.sinosoft.notice.model.dto.NotificationSseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 通知优先级管理服务 (使用SSE)
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationPriorityService {

    private final SysNotificationMapper notificationMapper;
    private final SysNotificationUserMapper notificationUserMapper;
    private final List<NotificationPriorityStrategy> priorityStrategies;
    private final NotificationCallbackService callbackService;
    private final SseNotificationService sseNotificationService;

    /**
     * 定时检查并调整通知优先级
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void checkAndAdjustPriorities() {
        log.info("开始检查并调整通知优先级");

        // 1. 查询所有正常状态的通知（非高优先级）
        List<SysNotification> notifications = notificationMapper.selectNormalNotificationsNotHighPriority();

        if (notifications.isEmpty()) {
            log.debug("没有需要调整优先级的通知");
            return;
        }

        log.info("找到 {} 条可能需要调整优先级的通知", notifications.size());
        Date currentTime = new Date();

        // 2. 检查每条通知是否需要升级优先级
        for (SysNotification notification : notifications) {
            checkAndAdjustNotificationPriority(notification, currentTime);
        }
    }

    /**
     * 检查并调整单条通知的优先级
     */
    private void checkAndAdjustNotificationPriority(SysNotification notification, Date currentTime) {
        // 1. 查询通知的阅读情况
        int totalCount = notificationUserMapper.countByNotificationId(notification.getNotificationId());
        int readCount = notificationUserMapper.countByNotificationIdAndIsRead(
            notification.getNotificationId(), "1");

        // 2. 应用所有策略，检查是否需要升级
        for (NotificationPriorityStrategy strategy : priorityStrategies) {
            if (strategy.shouldUpgradePriority(notification, currentTime, readCount, totalCount)) {
                // 获取建议的新优先级
                String newPriority = strategy.getSuggestedPriority(notification);

                // 如果确实需要升级
                if (!newPriority.equals(notification.getPriority())) {
                    log.info("通知优先级升级: ID={}, 原优先级={}, 新优先级={}, 策略={}",
                        notification.getNotificationId(),
                        notification.getPriority(),
                        newPriority,
                        strategy.getStrategyName());

                    // 记录原优先级
                    String oldPriority = notification.getPriority();

                    // 更新优先级
                    notification.setPriority(newPriority);
                    notification.setUpdateTime(currentTime);

                    // 保存更新
                    notificationMapper.updateById(notification);

                    // 触发优先级变更回调
                    handlePriorityChanged(notification, oldPriority, newPriority, strategy.getStrategyName());

                    // 一旦有一个策略导致升级，就停止检查其他策略
                    break;
                }
            }
        }
    }

    /**
     * 处理优先级变更回调
     */
    private void handlePriorityChanged(SysNotification notification,
                                       String oldPriority,
                                       String newPriority,
                                       String strategyName) {
        // 查询该通知的接收人
        List<Long> userIds = notificationUserMapper.selectUserIdsByNotificationId(
            notification.getNotificationId());

        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        // 创建优先级变更数据
        Map<String, Object> priorityData = new HashMap<>();
        priorityData.put("oldPriority", oldPriority);
        priorityData.put("newPriority", newPriority);
        priorityData.put("strategyName", strategyName);
        priorityData.put("updateTime", new Date());

        // 创建SSE消息
        NotificationSseDto sseDto = NotificationSseDto.builder()
            .type("priority_changed")
            .title("通知优先级已更新")
            .content("通知的优先级已升级为: " + getPriorityDisplayName(newPriority))
            .notificationId(notification.getNotificationId())
            .notificationType(notification.getType())
            .priority(newPriority)
            .timestamp(System.currentTimeMillis())
            .data(priorityData)
            .build();

        // 向每个用户发送SSE消息
        for (Long userId : userIds) {
            sseNotificationService.sendToUser(userId, sseDto);
        }
    }

    /**
     * 获取优先级显示名称
     */
    private String getPriorityDisplayName(String priority) {
        switch (priority) {
            case "high":
                return "高优先级";
            case "medium":
                return "中优先级";
            case "low":
                return "低优先级";
            default:
                return priority;
        }
    }
}
