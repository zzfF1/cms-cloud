package com.sinosoft.notice.core.event;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * 领域事件发布器接口
 */
public interface DomainEventPublisher {
    /**
     * 发布领域事件
     *
     * @param event 领域事件
     */
    void publish(NotificationDomainEvent event);
}
