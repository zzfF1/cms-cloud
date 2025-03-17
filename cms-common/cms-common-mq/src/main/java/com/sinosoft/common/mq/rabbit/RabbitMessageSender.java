package com.sinosoft.common.mq.rabbit;

import com.sinosoft.common.mq.config.MqProperties;
import com.sinosoft.common.mq.core.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.DisposableBean;

/**
 * RabbitMQ消息发送器实现
 * 基于Spring AMQP实现消息发送功能
 */
@Slf4j
public class RabbitMessageSender implements MessageSender, DisposableBean {
    /**
     * RabbitMQ模板，用于发送消息
     */
    private final RabbitTemplate rabbitTemplate;

    /**
     * 构造函数
     *
     * @param config          RabbitMQ配置
     * @param fallbackEnabled 是否启用降级
     * @throws RuntimeException 初始化失败且未启用降级时抛出
     */
    public RabbitMessageSender(MqProperties.RabbitConfig config, boolean fallbackEnabled) {
        try {
            // 创建连接工厂
            CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
            connectionFactory.setHost(config.getHost());
            connectionFactory.setPort(config.getPort());
            connectionFactory.setUsername(config.getUsername());
            connectionFactory.setPassword(config.getPassword());
            connectionFactory.setVirtualHost(config.getVirtualHost());

            // 创建RabbitTemplate
            rabbitTemplate = new RabbitTemplate(connectionFactory);
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

            // 测试连接
            connectionFactory.createConnection().close();
        } catch (Exception e) {
            log.error("创建RabbitMQ连接失败: {}", e.getMessage());
            if (fallbackEnabled) {
                log.warn("RabbitMQ连接失败，将使用降级策略");
                throw new RuntimeException("RabbitMQ连接失败", e);
            } else {
                throw e;
            }
        }
    }

    /**
     * 发送消息到指定主题（交换机）
     *
     * @param topic   主题名称，将自动添加"exchange."前缀
     * @param payload 消息内容
     * @return 发送是否成功
     */
    @Override
    public boolean send(String topic, Object payload) {
        try {
            rabbitTemplate.convertAndSend("exchange." + topic, "", payload);
            return true;
        } catch (Exception e) {
            log.error("发送RabbitMQ消息失败: topic={}, error={}", topic, e.getMessage());
            return false;
        }
    }

    /**
     * 发送消息到指定主题（交换机），使用指定的路由键
     *
     * @param topic      主题名称，将自动添加"exchange."前缀
     * @param routingKey 路由键
     * @param payload    消息内容
     * @return 发送是否成功
     */
    @Override
    public boolean send(String topic, String routingKey, Object payload) {
        try {
            rabbitTemplate.convertAndSend("exchange." + topic, routingKey, payload);
            return true;
        } catch (Exception e) {
            log.error("发送RabbitMQ消息失败: topic={}, routingKey={}, error={}",
                topic, routingKey, e.getMessage());
            return false;
        }
    }


    /**
     * 关闭发送器，释放资源
     */
    @Override
    public void destroy() {
        // RabbitTemplate会自动关闭资源
    }
}
