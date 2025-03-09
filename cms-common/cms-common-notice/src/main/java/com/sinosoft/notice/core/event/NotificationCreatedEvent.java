
package com.sinosoft.notice.core.event;

import com.sinosoft.notice.domain.SysNotification;

/**
 * 通知创建事件
 */
public class NotificationCreatedEvent extends NotificationDomainEvent {
    private final Long notificationId;
    private final String type;
    private final String title;
    private final Long templateId;
    private final String businessKey;

    public NotificationCreatedEvent(Long notificationId, String type, String title,
                                    Long templateId, String businessKey) {
        super();
        this.notificationId = notificationId;
        this.type = type;
        this.title = title;
        this.templateId = templateId;
        this.businessKey = businessKey;
    }

    // Getters
    public Long getNotificationId() {
        return notificationId;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public String getBusinessKey() {
        return businessKey;
    }
}
