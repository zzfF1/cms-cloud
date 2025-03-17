package com.sinosoft.common.mq.adapter;

import com.rabbitmq.client.*;
import com.sinosoft.common.mq.config.MqConfigProperties;
import com.sinosoft.common.mq.core.MqMessage;
import com.sinosoft.common.mq.core.MqResult;
import com.sinosoft.common.mq.exception.MqException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * RabbitMQ适配器实现
 */
@Slf4j
public class RabbitMqAdapter extends AbstractMqAdapter {

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Channel producerChannel;
    private Map<String, Channel> consumerChannels = new ConcurrentHashMap<>();
    private Map<String, String> consumerTags = new ConcurrentHashMap<>();
    private Map<String, Function<MqMessage, MqResult<?>>> listeners = new ConcurrentHashMap<>();
    private final MqConfigProperties.RabbitProperties rabbitProperties;
    private final MqConfigProperties mqConfigProperties; // 添加整个配置对象的引用


    public RabbitMqAdapter(String clusterName, MqConfigProperties.RabbitProperties rabbitProperties, MqConfigProperties mqConfigProperties) {
        super(clusterName);
        this.rabbitProperties = rabbitProperties;
        this.mqConfigProperties = mqConfigProperties; // 保存对完整配置的引用
    }

    @Override
    protected void doInitialize() {
        try {
            connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(rabbitProperties.getHost());
            connectionFactory.setPort(rabbitProperties.getPort());
            connectionFactory.setUsername(rabbitProperties.getUsername());
            connectionFactory.setPassword(rabbitProperties.getPassword());
            connectionFactory.setVirtualHost(rabbitProperties.getVirtualHost());

            // 其他连接配置
            connectionFactory.setAutomaticRecoveryEnabled(true);
            connectionFactory.setNetworkRecoveryInterval(5000);

            // 建立连接
            connection = connectionFactory.newConnection(clientId);

            // 创建生产者通道
            producerChannel = connection.createChannel();

            log.info("RabbitMQ连接已建立: {}", rabbitProperties.getHost());
        } catch (Exception e) {
            log.error("初始化RabbitMQ连接失败", e);
            throw new MqException("RABBITMQ-001", "初始化RabbitMQ连接失败: " + e.getMessage(), e);
        }
    }


    @Override
    public MqResult<String> send(MqMessage message) {
        checkInitialized();
        try {
            String exchange = getExchangeName(message);
            String routingKey = getRoutingKey(message);

            // 确保交换机存在
            declareExchange(producerChannel, exchange);

            AMQP.BasicProperties props = createMessageProperties(message);

            producerChannel.basicPublish(
                exchange,
                routingKey,
                props,
                serializePayload(message.getPayload())
            );

            return MqResult.success(message.getMessageId());
        } catch (Exception e) {
            log.error("发送RabbitMQ消息失败: {}", message, e);
            return MqResult.failure("RABBITMQ-002", "发送RabbitMQ消息失败: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<MqResult<String>> sendAsync(MqMessage message) {
        CompletableFuture<MqResult<String>> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            MqResult<String> result = send(message);
            future.complete(result);
        });
        return future;
    }

    @Override
    public MqResult<String> sendDelay(MqMessage message, long delayTime) {
        checkInitialized();
        try {
            String exchange = getExchangeName(message);
            String routingKey = getRoutingKey(message);
            String delayQueue = "delay.queue." + message.getTopic();
            String targetExchange = exchange;

            // 确保交换机存在
            declareExchange(exchange);

            // 声明死信交换机和队列用于延迟
            Map<String, Object> args = new HashMap<>();
            args.put("x-dead-letter-exchange", targetExchange);
            args.put("x-dead-letter-routing-key", routingKey);
            args.put("x-message-ttl", delayTime);

            // 声明延迟队列
            producerChannel.queueDeclare(delayQueue, true, false, false, args);
            producerChannel.queueBind(delayQueue, exchange, "delay." + routingKey);

            AMQP.BasicProperties props = createMessageProperties(message);

            producerChannel.basicPublish(
                exchange,
                "delay." + routingKey,
                props,
                serializePayload(message.getPayload())
            );

            return MqResult.success(message.getMessageId());
        } catch (Exception e) {
            log.error("发送延迟RabbitMQ消息失败: {}", message, e);
            return MqResult.failure("RABBITMQ-003", "发送延迟RabbitMQ消息失败: " + e.getMessage());
        }
    }

    @Override
    public MqResult<String> sendTransactional(MqMessage message, Object arg) {
        checkInitialized();
        try {
            producerChannel.txSelect();

            String exchange = getExchangeName(message);
            String routingKey = getRoutingKey(message);

            // 确保交换机存在
            declareExchange(exchange);

            AMQP.BasicProperties props = createMessageProperties(message);

            producerChannel.basicPublish(
                exchange,
                routingKey,
                props,
                serializePayload(message.getPayload())
            );

            producerChannel.txCommit();
            return MqResult.success(message.getMessageId());
        } catch (Exception e) {
            try {
                producerChannel.txRollback();
            } catch (IOException ex) {
                log.error("回滚RabbitMQ事务失败", ex);
            }

            log.error("发送事务性RabbitMQ消息失败: {}", message, e);
            return MqResult.failure("RABBITMQ-004", "发送事务性RabbitMQ消息失败: " + e.getMessage());
        }
    }

    @Override
    public void subscribe(String topic, String tags, Function<MqMessage, MqResult<?>> messageListener) {
        checkInitialized();
        // 添加降级模式检查
        if (fallbackMode) {
            log.warn("MQ客户端处于降级模式，忽略订阅操作: topic={}, tags={}", topic, tags);
            return;  // 直接返回，不执行后续操作
        }
        String consumerKey = topic + "#" + (tags != null ? tags : "*");
        try {
            // 创建消费者通道
            Channel channel = connection.createChannel();
            consumerChannels.put(consumerKey, channel);

            // 确保交换机和队列存在
            String exchange = "exchange." + topic;
            String queue = "queue." + topic;
            String routingKey = tags != null ? tags : "#";

            // 自动创建交换机和队列
            ensureExchangeAndQueueExist(channel, exchange, queue, routingKey);

            declareExchange(exchange);
            channel.queueDeclare(queue, true, false, false, null);
            channel.queueBind(queue, exchange, routingKey);

            // 设置预取数量
            channel.basicQos(rabbitProperties.getPrefetchCount());

            // 创建消费者
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(
                    String consumerTag,
                    Envelope envelope,
                    AMQP.BasicProperties properties,
                    byte[] body) throws IOException {

                    processMessage(channel, envelope, properties, body, messageListener);
                }
            };

            // 开始消费
            String consumerTag = channel.basicConsume(queue, false, consumer);
            consumerTags.put(consumerKey, consumerTag);

            // 保存监听器
            listeners.put(consumerKey, messageListener);

            log.info("已订阅RabbitMQ主题: {}, 标签: {}", topic, tags);
        } catch (Exception e) {
            log.error("订阅RabbitMQ主题失败: {}", topic, e);
            throw new MqException("RABBITMQ-005", "订阅RabbitMQ主题失败: " + e.getMessage(), e);
        }
    }

    /**
     * 确保交换机和队列存在
     */
    private void ensureExchangeAndQueueExist(Channel channel, String exchange, String queue, String routingKey) throws IOException {
        // 声明交换机
        declareExchange(channel, exchange);

        // 如果开启自动创建队列
        if (rabbitProperties.isAutoCreateQueues()) {
            // 声明队列 - 指定持久化和自动删除属性
            Map<String, Object> args = new HashMap<>();
            // 可以在这里添加队列参数，如消息TTL、死信交换机等

            channel.queueDeclare(
                queue,
                rabbitProperties.isDurableQueues(),  // 持久化
                false,                               // 排他性
                rabbitProperties.isAutoDeleteQueues(), // 自动删除
                args
            );

            // 绑定队列到交换机
            channel.queueBind(queue, exchange, routingKey);

            log.debug("确保RabbitMQ交换机'{}'和队列'{}'存在，绑定键'{}'",
                exchange, queue, routingKey);
        }
    }

    /**
     * 声明交换机
     */
    private void declareExchange(Channel channel, String exchange) throws IOException {
        channel.exchangeDeclare(
            exchange,
            rabbitProperties.getExchangeType(),
            true,    // 持久化
            false,   // 自动删除
            null     // 参数
        );
    }

    @Override
    public void unsubscribe(String topic) {
        consumerChannels.entrySet().removeIf(entry -> {
            if (entry.getKey().startsWith(topic + "#")) {
                try {
                    String consumerTag = consumerTags.get(entry.getKey());
                    if (consumerTag != null) {
                        entry.getValue().basicCancel(consumerTag);
                    }
                    entry.getValue().close();
                    listeners.remove(entry.getKey());
                    consumerTags.remove(entry.getKey());
                    log.info("已取消订阅RabbitMQ主题: {}", topic);
                } catch (Exception e) {
                    log.warn("取消订阅RabbitMQ主题时发生错误: {}", topic, e);
                }
                return true;
            }
            return false;
        });
    }

    @Override
    protected void doStart() {
        // 消费者已经在订阅时启动
    }

    @Override
    protected void doPause() {
        consumerChannels.forEach((key, channel) -> {
            try {
                String consumerTag = consumerTags.get(key);
                if (consumerTag != null) {
                    channel.basicCancel(consumerTag);
                }
            } catch (Exception e) {
                log.warn("暂停RabbitMQ消费者时发生错误: {}", key, e);
            }
        });
    }

    @Override
    protected void doResume() {
        consumerChannels.forEach((key, channel) -> {
            try {
                String[] parts = key.split("#");
                String topic = parts[0];
                String tags = parts.length > 1 ? parts[1] : "#";
                Function<MqMessage, MqResult<?>> listener = listeners.get(key);

                if (listener != null) {
                    String queue = "queue." + topic;

                    DefaultConsumer consumer = new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(
                            String consumerTag,
                            Envelope envelope,
                            AMQP.BasicProperties properties,
                            byte[] body) throws IOException {

                            processMessage(channel, envelope, properties, body, listener);
                        }
                    };

                    String consumerTag = channel.basicConsume(queue, false, consumer);
                    consumerTags.put(key, consumerTag);
                }
            } catch (Exception e) {
                log.warn("恢复RabbitMQ消费者时发生错误: {}", key, e);
            }
        });
    }

    @Override
    protected void doShutdown() {
        // 关闭消费者通道
        for (Channel channel : consumerChannels.values()) {
            try {
                channel.close();
            } catch (Exception e) {
                log.warn("关闭RabbitMQ消费者通道时发生错误", e);
            }
        }
        consumerChannels.clear();
        consumerTags.clear();
        listeners.clear();

        // 关闭生产者通道
        if (producerChannel != null) {
            try {
                producerChannel.close();
            } catch (Exception e) {
                log.warn("关闭RabbitMQ生产者通道时发生错误", e);
            }
        }

        // 关闭连接
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                log.warn("关闭RabbitMQ连接时发生错误", e);
            }
        }
    }

    /**
     * 处理接收到的消息
     */
    private void processMessage(
        Channel channel,
        Envelope envelope,
        AMQP.BasicProperties properties,
        byte[] body,
        Function<MqMessage, MqResult<?>> listener) throws IOException {

        try {
            MqMessage message = new MqMessage();
            message.setTopic(envelope.getExchange());
            message.setTag(envelope.getRoutingKey());
            message.setMessageId(properties.getMessageId());
            message.setTimestamp(properties.getTimestamp() != null ? properties.getTimestamp().getTime() : System.currentTimeMillis());

            // 设置消息体
            message.setPayload(deserializePayload(body));

            // 设置头部属性
            if (properties.getHeaders() != null) {
                properties.getHeaders().forEach((key, value) -> message.addHeader(key, value));
            }

            // 调用监听器处理消息
            MqResult<?> result = listener.apply(message);

            if (result.isSuccess()) {
                channel.basicAck(envelope.getDeliveryTag(), false);
            } else {
                // 根据错误码决定是否重试
                if (shouldRetry(result.getCode())) {
                    channel.basicNack(envelope.getDeliveryTag(), false, true);
                } else {
                    // 拒绝且不重试，可能进入死信队列
                    channel.basicReject(envelope.getDeliveryTag(), false);
                }
            }
        } catch (Exception e) {
            log.error("处理RabbitMQ消息时发生异常", e);
            // 发生异常时拒绝消息并重新入队
            channel.basicNack(envelope.getDeliveryTag(), false, true);
        }
    }

    /**
     * 根据错误码判断是否应该重试
     */
    private boolean shouldRetry(String code) {
        // 根据业务需求实现重试策略
        return code != null && code.startsWith("RETRY");
    }

    /**
     * 声明交换机
     */
    private void declareExchange(String exchange) throws IOException {
        producerChannel.exchangeDeclare(exchange, rabbitProperties.getExchangeType(), true);
    }

    /**
     * 创建消息属性
     */
    private AMQP.BasicProperties createMessageProperties(MqMessage message) {
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder()
            .messageId(message.getMessageId())
            .timestamp(new Date(message.getTimestamp()))
            .contentType("application/json")
            .deliveryMode(2) // 持久化
            .expiration(rabbitProperties.getMsgTtl());

        // 添加头部属性
        if (!message.getHeaders().isEmpty()) {
            builder.headers(new HashMap<>(message.getHeaders()));
        }

        return builder.build();
    }

    /**
     * 获取交换机名称
     */
    private String getExchangeName(MqMessage message) {
        return "exchange." + message.getTopic();
    }

    /**
     * 获取路由键
     */
    private String getRoutingKey(MqMessage message) {
        return message.getTag() != null ? message.getTag() : "";
    }

    /**
     * 序列化消息体
     */
    private byte[] serializePayload(Object payload) {
        if (payload instanceof String) {
            return ((String) payload).getBytes(StandardCharsets.UTF_8);
        } else if (payload instanceof byte[]) {
            return (byte[]) payload;
        } else {
            // TODO: 使用JSON或其他序列化方式
            return payload.toString().getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * 反序列化消息体
     */
    private Object deserializePayload(byte[] data) {
        // TODO: 根据消息类型进行反序列化
        return new String(data, StandardCharsets.UTF_8);
    }
}
