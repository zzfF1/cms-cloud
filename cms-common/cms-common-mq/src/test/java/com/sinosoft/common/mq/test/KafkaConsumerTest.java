package com.sinosoft.common.mq.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumerTest {

    @KafkaListener(topics = "test-queue-kafka", groupId = "test-consumer-group")
    public void handleKafkaMessage(Object message) {
        log.info("接收到Kafka消息: {}", message);
    }
}
