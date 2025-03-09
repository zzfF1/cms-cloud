package com.sinosoft.notice.core.event;

/**
 * 通知送达失败事件
 */
public class NotificationDeliveryFailedEvent extends NotificationDomainEvent {
    private final Long notificationId;
    private final Long userId;
    private final String channel;
    private final String errorMessage;

    public NotificationDeliveryFailedEvent(Long notificationId, Long userId, String channel, String errorMessage) {
        super();
        this.notificationId = notificationId;
        this.userId = userId;
        this.channel = channel;
        this.errorMessage = errorMessage;
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

    public String getErrorMessage() {
        return errorMessage;
    }
}
