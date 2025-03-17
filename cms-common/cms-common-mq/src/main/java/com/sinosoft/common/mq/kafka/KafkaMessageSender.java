package com.sinosoft.common.mq.kafka;

import com.sinosoft.common.mq.config.MqProperties;
import com.sinosoft.common.mq.core.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Kafka消息发送器实现
 * 基于Spring Kafka实现消息发送功能
 */
@Slf4j
public class KafkaMessageSender implements MessageSender, DisposableBean {
    /**
     * Kafka模板，用于发送消息
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 构造函数
     *
     * @param config          Kafka配置
     * @param fallbackEnabled 是否启用降级
     * @throws RuntimeException 初始化失败且未启用降级时抛出
     */
    public KafkaMessageSender(MqProperties.KafkaConfig config, boolean fallbackEnabled) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        // 添加自定义配置
        if (config.getProperties() != null) {
            props.putAll(config.getProperties());
        }

        DefaultKafkaProducerFactory<String, Object> factory =
            new DefaultKafkaProducerFactory<>(props);
        this.kafkaTemplate = new KafkaTemplate<>(factory);

        // 测试连接
        try {
            kafkaTemplate.getDefaultTopic();
        } catch (Exception e) {
            log.warn("Kafka连接测试失败: {}", e.getMessage());
            if (!fallbackEnabled) {
                throw new RuntimeException("Kafka连接失败", e);
            }
        }
    }

    /**
     * 发送消息到指定主题
     *
     * @param topic   主题名称
     * @param payload 消息内容
     * @return 发送是否成功
     */
    @Override
    public boolean send(String topic, Object payload) {
        try {
            kafkaTemplate.send(topic, payload).get(5, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("发送Kafka消息失败: topic={}, error={}", topic, e.getMessage());
            return false;
        }
    }

    /**
     * 发送消息到指定主题，使用指定的键
     *
     * @param topic   主题名称
     * @param key     消息键，用于分区路由
     * @param payload 消息内容
     * @return 发送是否成功
     */
    @Override
    public boolean send(String topic, String key, Object payload) {
        try {
            kafkaTemplate.send(topic, key, payload).get(5, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("发送Kafka消息失败: topic={}, key={}, error={}", topic, key, e.getMessage());
            return false;
        }
    }


    /**
     * 关闭发送器，释放资源
     */
    @Override
    public void destroy() {
        if (kafkaTemplate != null) {
            try {
                kafkaTemplate.flush();
            } catch (Exception e) {
                log.warn("Kafka发送器关闭时发生错误: {}", e.getMessage());
            }
        }
    }
}
