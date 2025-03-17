package com.sinosoft.common.mq.manager;

import com.sinosoft.common.mq.config.MqProperties;
import com.sinosoft.common.mq.core.MessageSender;
import com.sinosoft.common.mq.kafka.KafkaMessageSender;
import com.sinosoft.common.mq.rabbit.RabbitMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MQ管理器，负责创建和管理消息发送器实例
 * 提供按需获取不同类型MQ发送器的方法
 */
@Slf4j
public class MqManager implements DisposableBean {
    /**
     * MQ配置属性
     */
    private final MqProperties properties;

    /**
     * 发送器缓存，key格式为：mqType:clusterName
     */
    private final Map<String, MessageSender> senders = new ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param properties MQ配置属性
     */
    public MqManager(MqProperties properties) {
        this.properties = properties;
    }

    /**
     * 获取指定集群的Kafka消息发送器
     *
     * @param clusterName 集群名称
     * @return Kafka消息发送器，如果MQ服务不可用且启用了降级，则返回无操作发送器
     * @throws IllegalArgumentException 如果指定的集群不存在
     */
    public MessageSender getKafkaSender(String clusterName) {
        if (!properties.isEnabled()) {
            log.warn("MQ功能已禁用，返回无操作发送器");
            return new NoOpMessageSender();
        }

        String key = "kafka:" + clusterName;
        return senders.computeIfAbsent(key, k -> {
            MqProperties.KafkaConfig config = properties.getKafka().get(clusterName);
            if (config == null) {
                throw new IllegalArgumentException("未知的Kafka集群: " + clusterName);
            }

            try {
                return new KafkaMessageSender(config, properties.isFallbackEnabled());
            } catch (Exception e) {
                log.error("创建Kafka发送器失败: {}", e.getMessage());
                if (properties.isFallbackEnabled()) {
                    log.warn("启用降级模式，返回无操作发送器");
                    return new NoOpMessageSender();
                }
                throw e;
            }
        });
    }

    /**
     * 获取指定集群的RabbitMQ消息发送器
     *
     * @param clusterName 集群名称
     * @return RabbitMQ消息发送器，如果MQ服务不可用且启用了降级，则返回无操作发送器
     * @throws IllegalArgumentException 如果指定的集群不存在
     */
    public MessageSender getRabbitSender(String clusterName) {
        if (!properties.isEnabled()) {
            log.warn("MQ功能已禁用，返回无操作发送器");
            return new NoOpMessageSender();
        }

        String key = "rabbit:" + clusterName;
        return senders.computeIfAbsent(key, k -> {
            MqProperties.RabbitConfig config = properties.getRabbit().get(clusterName);
            if (config == null) {
                throw new IllegalArgumentException("未知的RabbitMQ集群: " + clusterName);
            }

            try {
                return new RabbitMessageSender(config, properties.isFallbackEnabled());
            } catch (Exception e) {
                log.error("创建RabbitMQ发送器失败: {}", e.getMessage());
                if (properties.isFallbackEnabled()) {
                    log.warn("启用降级模式，返回无操作发送器");
                    return new NoOpMessageSender();
                }
                throw e;
            }
        });
    }

    /**
     * 获取默认消息发送器
     * 根据defaultType配置返回对应类型的默认集群发送器
     *
     * @return 默认消息发送器
     */
    public MessageSender getDefaultSender() {
        if ("kafka".equals(properties.getDefaultType())) {
            return getKafkaSender("default");
        } else {
            return getRabbitSender("default");
        }
    }

    /**
     * 关闭所有消息发送器，释放资源
     */
    @Override
    public void destroy() {
        for (MessageSender sender : senders.values()) {
            if (sender instanceof DisposableBean) {
                try {
                    ((DisposableBean) sender).destroy();
                } catch (Exception e) {
                    log.warn("关闭MQ发送器时发生错误: {}", e.getMessage());
                }
            }
        }
        senders.clear();
    }

    /**
     * 无操作消息发送器，用于MQ服务不可用时的降级处理
     * 所有方法都只记录日志，不执行实际操作
     */
    private static class NoOpMessageSender implements MessageSender {
        @Override
        public boolean send(String topic, Object payload) {
            log.warn("MQ服务不可用，消息未发送: topic={}", topic);
            return false;
        }

        @Override
        public boolean send(String topic, String routingKey, Object payload) {
            log.warn("MQ服务不可用，消息未发送: topic={}, routingKey={}", topic, routingKey);
            return false;
        }

    }
}
