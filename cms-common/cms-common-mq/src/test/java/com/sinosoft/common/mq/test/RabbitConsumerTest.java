package com.sinosoft.common.mq.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitConsumerTest {

    @RabbitListener(queues = "queue.test-queue-rabbit")
    public void handleRabbitMessage(Object message) {
        log.info("接收到RabbitMQ消息: {}", message);
    }
}
