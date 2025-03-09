package com.sinosoft.notice.core.event;

import java.util.Date;
import java.util.UUID;

/**
 * 通知领域事件基类
 */
public abstract class NotificationDomainEvent {
    /**
     * 事件ID
     */
    private final String eventId;

    /**
     * 事件发生时间
     */
    private final Date occurredOn;

    /**
     * 构造方法
     */
    protected NotificationDomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = new Date();
    }

    public String getEventId() {
        return eventId;
    }

    public Date getOccurredOn() {
        return occurredOn;
    }
}
