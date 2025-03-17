package com.sinosoft.common.mq.test;

import com.sinosoft.common.mq.core.MessageSender;
import com.sinosoft.common.mq.manager.MqManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class MqSendTest {

    @Autowired
    private MqManager mqManager;

    @Test
    public void testSendMessages() {
        log.info("开始执行MQ消息发送测试...");

        // 测试发送到Kafka队列
        testKafkaSend();

        // 测试发送到RabbitMQ队列
        testRabbitSend();

        log.info("MQ消息发送测试完成");
    }

    private void testKafkaSend() {
        MessageSender sender = mqManager.getKafkaSender("default");

        // 创建测试消息
        TestMessage message = new TestMessage();
        message.setId(1001L);
        message.setContent("这是发送到Kafka队列的测试消息");
        message.setTimestamp(System.currentTimeMillis());

        // 发送消息
        boolean result = sender.send("test-queue-kafka", message);
        log.info("Kafka消息发送{}，消息内容: {}", result ? "成功" : "失败", message);
    }

    private void testRabbitSend() {
        MessageSender sender = mqManager.getRabbitSender("default");

        // 创建测试消息
        TestMessage message = new TestMessage();
        message.setId(2001L);
        message.setContent("这是发送到RabbitMQ队列的测试消息");
        message.setTimestamp(System.currentTimeMillis());

        // 发送消息
        boolean result = sender.send("test-queue-rabbit", message);
        log.info("RabbitMQ消息发送{}，消息内容: {}", result ? "成功" : "失败", message);
    }

    public static class TestMessage {
        private Long id;
        private String content;
        private long timestamp;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

        @Override
        public String toString() {
            return "TestMessage{id=" + id + ", content='" + content + "', timestamp=" + timestamp + "}";
        }
    }
}
