package com.sinosoft.notice.core.event;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 同步领域事件发布器实现
 */
@Component
public class SynchronousDomainEventPublisher implements DomainEventPublisher {

    private final List<DomainEventSubscriber> subscribers = new ArrayList<>();

    @Override
    public void publish(NotificationDomainEvent event) {
        for (DomainEventSubscriber subscriber : subscribers) {
            if (subscriber.canHandle(event)) {
                subscriber.handle(event);
            }
        }
    }

    /**
     * 注册事件订阅者
     */
    public void register(DomainEventSubscriber subscriber) {
        subscribers.add(subscriber);
    }
}
