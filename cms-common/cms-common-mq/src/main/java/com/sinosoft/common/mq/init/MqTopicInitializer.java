package com.sinosoft.common.mq.init;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sinosoft.common.mq.config.MqProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * MQ主题初始化器
 * 负责在应用启动时预创建配置的主题/队列
 */
@Slf4j
@Component
public class MqTopicInitializer implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * MQ配置属性
     */
    private final MqProperties properties;

    /**
     * 构造函数
     *
     * @param properties MQ配置属性
     */
    public MqTopicInitializer(MqProperties properties) {
        this.properties = properties;
    }

    /**
     * 处理应用上下文刷新事件，用于在应用启动时创建主题
     *
     * @param event 上下文刷新事件
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 只处理根上下文刷新事件
        if (event.getApplicationContext().getParent() != null) {
            return;
        }

        // 确保MQ功能和预创建功能已启用
        if (!properties.isEnabled() || !properties.getPreCreate().isEnabled()) {
            log.info("MQ主题预创建功能未启用");
            return;
        }

        // 检查是否有预创建的主题
        if (properties.getPreCreate().getTopics() == null ||
            properties.getPreCreate().getTopics().isEmpty()) {
            log.info("没有需要预创建的MQ主题");
            return;
        }

        log.info("开始预创建MQ主题，共 {} 个", properties.getPreCreate().getTopics().size());

        // 预创建每个主题
        for (MqProperties.TopicConfig topic : properties.getPreCreate().getTopics()) {
            try {
                createTopic(topic);
            } catch (Exception e) {
                log.error("创建主题 {} 失败: {}", topic.getName(), e.getMessage());
                // 继续创建其他主题
            }
        }

        log.info("MQ主题预创建完成");
    }

    /**
     * 根据配置创建主题
     *
     * @param topic 主题配置
     */
    private void createTopic(MqProperties.TopicConfig topic) {
        // 根据类型创建Kafka或RabbitMQ主题
        String type = topic.getType() != null ? topic.getType() : properties.getDefaultType();

        if ("kafka".equals(type)) {
            createKafkaTopic(topic);
        } else if ("rabbit".equals(type)) {
            createRabbitTopic(topic);
        } else {
            log.warn("不支持的MQ类型: {}，跳过创建主题 {}", type, topic.getName());
        }
    }

    /**
     * 创建Kafka主题
     *
     * @param topic 主题配置
     */
    private void createKafkaTopic(MqProperties.TopicConfig topic) {
        // 获取Kafka集群配置
        MqProperties.KafkaConfig config = properties.getKafka().get(topic.getCluster());
        if (config == null) {
            log.warn("未找到Kafka集群配置: {}，跳过创建主题 {}", topic.getCluster(), topic.getName());
            return;
        }

        AdminClient adminClient = null;
        try {
            // 创建AdminClient
            Properties props = new Properties();
            props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
            props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");
            adminClient = AdminClient.create(props);

            // 检查主题是否存在
            if (adminClient.listTopics().names().get(5, TimeUnit.SECONDS).contains(topic.getName())) {
                log.info("Kafka主题已存在: {}", topic.getName());
                return;
            }

            // 创建主题
            NewTopic newTopic = new NewTopic(
                topic.getName(),
                topic.getPartitions(),
                (short) topic.getReplicas()
            );

            adminClient.createTopics(Collections.singleton(newTopic))
                .all().get(10, TimeUnit.SECONDS);

            log.info("已创建Kafka主题: {}", topic.getName());
        } catch (Exception e) {
            if (e.getCause() instanceof TopicExistsException) {
                log.info("Kafka主题已存在: {}", topic.getName());
            } else {
                log.error("创建Kafka主题失败: {}, 错误: {}", topic.getName(), e.getMessage());
                // 忽略异常，继续创建其他主题
            }
        } finally {
            if (adminClient != null) {
                try {
                    adminClient.close(java.time.Duration.ofSeconds(5));
                } catch (Exception e) {
                    log.warn("关闭Kafka AdminClient时发生错误: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * 创建RabbitMQ交换机和队列
     *
     * @param topic 主题配置
     */
    private void createRabbitTopic(MqProperties.TopicConfig topic) {
        // 获取RabbitMQ集群配置
        MqProperties.RabbitConfig config = properties.getRabbit().get(topic.getCluster());
        if (config == null) {
            log.warn("未找到RabbitMQ集群配置: {}，跳过创建队列 {}", topic.getCluster(), topic.getName());
            return;
        }

        ConnectionFactory factory = null;
        Connection connection = null;
        Channel channel = null;

        try {
            // 创建连接
            factory = new ConnectionFactory();
            factory.setHost(config.getHost());
            factory.setPort(config.getPort());
            factory.setUsername(config.getUsername());
            factory.setPassword(config.getPassword());

            if (StringUtils.hasText(config.getVirtualHost())) {
                factory.setVirtualHost(config.getVirtualHost());
            }

            connection = factory.newConnection("admin-connection");
            channel = connection.createChannel();

            String exchangeName = "exchange." + topic.getName();
            String queueName = "queue." + topic.getName();
            String routingKey = topic.getRoutingKey();

            // 声明交换机
            channel.exchangeDeclare(
                exchangeName,
                config.getExchangeType(),
                true,   // 持久化
                false,  // 不自动删除
                null    // 无特殊参数
            );

            // 声明队列
            channel.queueDeclare(
                queueName,
                config.isDurableQueues(),  // 持久化
                false,                     // 不排他
                false,                     // 不自动删除
                null                       // 无特殊参数
            );

            // 绑定队列到交换机
            channel.queueBind(queueName, exchangeName, routingKey);

            log.info("已创建RabbitMQ交换机和队列: {}", topic.getName());
        } catch (Exception e) {
            log.error("创建RabbitMQ交换机和队列失败: {}, 错误: {}", topic.getName(), e.getMessage());
        } finally {
            // 关闭资源
            try {
                if (channel != null && channel.isOpen()) {
                    channel.close();
                }
                if (connection != null && connection.isOpen()) {
                    connection.close();
                }
            } catch (Exception e) {
                log.warn("关闭RabbitMQ连接时发生错误: {}", e.getMessage());
            }
        }
    }
}
