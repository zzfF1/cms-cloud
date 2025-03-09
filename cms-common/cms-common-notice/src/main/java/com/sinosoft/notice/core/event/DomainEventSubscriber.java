package com.sinosoft.notice.core.event;

/**
 * 领域事件订阅者接口
 */
public interface DomainEventSubscriber {
    /**
     * 判断是否能处理事件
     */
    boolean canHandle(NotificationDomainEvent event);

    /**
     * 处理事件
     */
    void handle(NotificationDomainEvent event);
}
