
package com.sinosoft.notice.core.event;


/**
 * 通知状态变更事件
 */
public class NotificationStatusChangedEvent extends NotificationDomainEvent {
    private final Long notificationId;
    private final String oldStatus;
    private final String newStatus;

    public NotificationStatusChangedEvent(Long notificationId, String oldStatus, String newStatus) {
        super();
        this.notificationId = notificationId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    // Getters
    public Long getNotificationId() {
        return notificationId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }
}
