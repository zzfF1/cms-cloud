package com.sinosoft.notice.service.impl;

import com.alibaba.fastjson2.JSON;
import com.sinosoft.common.sse.dto.SseMessageDto;
import com.sinosoft.common.sse.utils.SseMessageUtils;
import com.sinosoft.notice.core.callback.NotificationCallback;
import com.sinosoft.notice.core.callback.NotificationCallbackEvent;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.mapper.SysNotificationMapper;
import com.sinosoft.notice.mapper.SysNotificationUserMapper;
import com.sinosoft.notice.model.dto.NotificationSseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * SSE通知回调实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SseNotificationService implements NotificationCallback {

    private final SysNotificationMapper notificationMapper;
    private final SysNotificationUserMapper notificationUserMapper;

    @Override
    public String getCallbackType() {
        return "sse";
    }

    @Override
    public void onNotificationCreated(NotificationCallbackEvent.Created event) {
        // 查询详细信息
        SysNotification notification = notificationMapper.selectById(event.getNotificationId());
        if (notification == null) {
            return;
        }

        // 创建SSE消息
        NotificationSseDto sseDto = createSseDto(
            "notification_created",
            "新" + getTypeDisplayName(notification.getType()),
            notification.getTitle(),
            notification
        );

        // 查询该通知的接收人
        List<Long> userIds = notificationUserMapper.selectUserIdsByNotificationId(notification.getNotificationId());

        if (userIds != null && !userIds.isEmpty()) {
            // 创建SSE消息
            SseMessageDto sseMessage = new SseMessageDto();
            sseMessage.setMessage(JSON.toJSONString(sseDto));
            sseMessage.setUserIds(userIds);

            // 发送SSE消息
            SseMessageUtils.publishMessage(sseMessage);

            log.debug("发送通知创建SSE消息, 通知ID: {}, 接收人数: {}", notification.getNotificationId(), userIds.size());
        }
    }

    @Override
    public void onNotificationRead(NotificationCallbackEvent.Read event) {
        // 创建SSE消息
        NotificationSseDto sseDto = createSseDto(
            "notification_read",
            "已读确认",
            "通知已标记为已读",
            event.getNotificationId(),
            event.getType(),
            null,
            null
        );

        // 向用户发送消息
        sendToUser(event.getUserId(), sseDto);
    }

    @Override
    public void onNotificationProcessed(NotificationCallbackEvent.Processed event) {
        // 创建SSE消息
        NotificationSseDto sseDto = createSseDto(
            "notification_processed",
            "处理确认",
            "通知已成功处理",
            event.getNotificationId(),
            event.getType(),
            null,
            event.getProcessNote()
        );

        // 向用户发送消息
        sendToUser(event.getUserId(), sseDto);
    }

    @Override
    public void onNotificationSent(NotificationCallbackEvent.Sent event) {
        // SSE通常不处理此事件
    }

    @Override
    public void onNotificationSendFailed(NotificationCallbackEvent.SendFailed event) {
        // SSE通常不处理此事件
    }

    @Override
    public void onNotificationExpired(NotificationCallbackEvent.Expired event) {
        // 创建SSE消息
        NotificationSseDto sseDto = createSseDto(
            "notification_expired",
            "通知已过期",
            "有通知已过期",
            event.getNotificationId(),
            event.getType(),
            null,
            createExpiredData(event)
        );

        // 广播给所有用户（实际项目中应该只发给相关用户）
        SseMessageUtils.publishAll(JSON.toJSONString(sseDto));
    }

    @Override
    public void onNotificationCancelled(NotificationCallbackEvent.Cancelled event) {
        // 创建SSE消息
        NotificationSseDto sseDto = createSseDto(
            "notification_cancelled",
            "通知已取消",
            "有通知已被取消: " + event.getCancelReason(),
            event.getNotificationId(),
            event.getType(),
            null,
            event.getCancelReason()
        );

        // 广播给所有用户（实际项目中应该只发给相关用户）
        SseMessageUtils.publishAll(JSON.toJSONString(sseDto));
    }

    /**
     * 创建SSE消息DTO（基于通知对象）
     */
    private NotificationSseDto createSseDto(String type, String title, String content, SysNotification notification) {
        return NotificationSseDto.builder()
            .type(type)
            .title(title)
            .content(content)
            .notificationId(notification.getNotificationId())
            .notificationType(notification.getType())
            .priority(notification.getPriority())
            .timestamp(System.currentTimeMillis())
            .data(notification)
            .build();
    }

    /**
     * 创建SSE消息DTO（基本信息）
     */
    private NotificationSseDto createSseDto(
        String type, String title, String content,
        Long notificationId, String notificationType, String priority, Object data) {

        return NotificationSseDto.builder()
            .type(type)
            .title(title)
            .content(content)
            .notificationId(notificationId)
            .notificationType(notificationType)
            .priority(priority)
            .timestamp(System.currentTimeMillis())
            .data(data)
            .build();
    }

    /**
     * 向指定用户发送SSE消息
     */
    public void sendToUser(Long userId, NotificationSseDto sseDto) {
        if (userId == null) {
            return;
        }
        SseMessageUtils.sendMessage(userId, JSON.toJSONString(sseDto));
    }

    /**
     * 创建过期数据
     */
    private Map<String, Object> createExpiredData(NotificationCallbackEvent.Expired event) {
        Map<String, Object> data = new HashMap<>();
        data.put("unreadCount", event.getUnreadCount());
        data.put("unprocessedCount", event.getUnprocessedCount());
        data.put("expireTime", event.getExpireTime());
        return data;
    }

    /**
     * 获取通知类型显示名称
     */
    private String getTypeDisplayName(String type) {
        switch (type) {
            case "todo":
                return "待办事项";
            case "alert":
                return "预警提醒";
            case "announcement":
                return "系统公告";
            default:
                return "通知";
        }
    }

    /**
     * 发送未读计数更新
     */
    public void sendUnreadCountUpdate(Long userId, Map<String, Integer> countMap) {
        NotificationSseDto sseDto = NotificationSseDto.builder()
            .type("unread_count_update")
            .title("未读计数更新")
            .content("未读通知计数已更新")
            .timestamp(System.currentTimeMillis())
            .data(countMap)
            .build();

        sendToUser(userId, sseDto);
    }
}
