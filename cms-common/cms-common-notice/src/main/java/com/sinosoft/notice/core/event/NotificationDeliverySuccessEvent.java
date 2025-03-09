package com.sinosoft.notice.core.event;

/**
 * 通知送达成功事件
 */
public class NotificationDeliverySuccessEvent extends NotificationDomainEvent {
    private final Long notificationId;
    private final Long userId;
    private final String channel;

    public NotificationDeliverySuccessEvent(Long notificationId, Long userId, String channel) {
        super();
        this.notificationId = notificationId;
        this.userId = userId;
        this.channel = channel;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getChannel() {
        return channel;
    }
}
