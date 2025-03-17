package com.sinosoft.common.mq.adapter;

import cn.hutool.core.collection.CollUtil;
import com.sinosoft.common.mq.config.MqConfigProperties;
import com.sinosoft.common.mq.core.MqMessage;
import com.sinosoft.common.mq.core.MqResult;
import com.sinosoft.common.mq.exception.MqException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Kafka适配器实现
 */
@Slf4j
public class KafkaMqAdapter extends AbstractMqAdapter {

    //---------------------
    // 字段/属性
    //---------------------

    private KafkaProducer<String, Object> producer;
    private Map<String, KafkaConsumer<String, Object>> consumers = new ConcurrentHashMap<>();
    private Map<String, Function<MqMessage, MqResult<?>>> listeners = new ConcurrentHashMap<>();
    private ExecutorService executorService;
    private AdminClient adminClient;
    private final MqConfigProperties.KafkaProperties kafkaProperties;
    private final MqConfigProperties mqConfigProperties; // 添加整个配置对象的引用
    private volatile boolean consumerRunning = false;

    //---------------------
    // 构造函数
    //---------------------

    public KafkaMqAdapter(String clusterName, MqConfigProperties.KafkaProperties kafkaProperties, MqConfigProperties mqConfigProperties) {
        super(clusterName);
        this.kafkaProperties = kafkaProperties;
        this.mqConfigProperties = mqConfigProperties; // 保存对完整配置的引用
    }

    //---------------------
    // 初始化和生命周期方法
    //---------------------

    @Override
    protected void doInitialize() {
        // 初始化生产者
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.CLIENT_ID_CONFIG, clientId + "-producer");
        producerProps.put(ProducerConfig.ACKS_CONFIG, "all");
        producerProps.put(ProducerConfig.RETRIES_CONFIG, 3);

        // 合并用户自定义配置
        if (kafkaProperties.getProducerProps() != null) {
            producerProps.putAll(kafkaProperties.getProducerProps());
        }

        producer = new KafkaProducer<>(producerProps);

        // 初始化线程池
        executorService = new ThreadPoolExecutor(
            kafkaProperties.getConsumerThreads(),
            kafkaProperties.getConsumerThreads() * 2,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            r -> new Thread(r, "kafka-consumer-" + r.hashCode()),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // 初始化AdminClient用于主题管理
        if (kafkaProperties.isAutoCreateTopics()) {
            Properties adminProps = new Properties();
            adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(clusterName));
            adminProps.put(AdminClientConfig.CLIENT_ID_CONFIG, clientId + "-admin");
            adminClient = AdminClient.create(adminProps);
            // 创建预定义主题
            createPredefinedTopics();
        }
    }

    @Override
    protected void doStart() {
        startConsumers();
    }

    @Override
    protected void doPause() {
        consumerRunning = false;
    }

    @Override
    protected void doResume() {
        startConsumers();
    }

    @Override
    protected void doShutdown() {
        consumerRunning = false;
        // 关闭消费者
        for (KafkaConsumer<String, Object> consumer : consumers.values()) {
            try {
                consumer.close();
            } catch (Exception e) {
                log.warn("关闭Kafka消费者时发生错误", e);
            }
        }
        consumers.clear();
        listeners.clear();

        // 关闭生产者
        if (producer != null) {
            try {
                producer.close();
            } catch (Exception e) {
                log.warn("关闭Kafka生产者时发生错误", e);
            }
        }

        // 关闭线程池
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        // 关闭AdminClient
        if (adminClient != null) {
            try {
                adminClient.close();
            } catch (Exception e) {
                log.warn("关闭Kafka管理客户端时发生错误", e);
            }
        }
    }

    //---------------------
    // 生产者方法
    //---------------------

    @Override
    public MqResult<String> send(MqMessage message) {
        checkInitialized();
        try {
            // 检查并创建主题
            ensureTopicExists(message.getTopic());
            ProducerRecord<String, Object> record = createProducerRecord(message);
            RecordMetadata metadata = producer.send(record).get();
            return MqResult.success(metadata.topic() + "-" + metadata.partition() + "-" + metadata.offset());
        } catch (Exception e) {
            log.error("发送Kafka消息失败: {}", message, e);
            return MqResult.failure("KAFKA-001", "发送Kafka消息失败: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<MqResult<String>> sendAsync(MqMessage message) {
        checkInitialized();
        CompletableFuture<MqResult<String>> future = new CompletableFuture<>();
        try {
            ProducerRecord<String, Object> record = createProducerRecord(message);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    future.complete(MqResult.failure("KAFKA-002", "异步发送Kafka消息失败: " + exception.getMessage()));
                } else {
                    future.complete(MqResult.success(metadata.topic() + "-" + metadata.partition() + "-" + metadata.offset()));
                }
            });
        } catch (Exception e) {
            log.error("异步发送Kafka消息失败: {}", message, e);
            future.complete(MqResult.failure("KAFKA-003", "异步发送Kafka消息失败: " + e.getMessage()));
        }
        return future;
    }

    @Override
    public MqResult<String> sendDelay(MqMessage message, long delayTime) {
        // Kafka原生不支持延迟消息，可以通过时间戳实现
        message.addHeader("__DELAY_TIME__", System.currentTimeMillis() + delayTime);
        return send(message);
    }

    @Override
    public MqResult<String> sendTransactional(MqMessage message, Object arg) {
        // 基础版本暂不实现事务消息
        log.warn("Kafka适配器尚未实现事务消息功能");
        return send(message);
    }

    //---------------------
    // 消费者方法
    //---------------------

    @Override
    public void subscribe(String topic, String tags, Function<MqMessage, MqResult<?>> messageListener) {
        checkInitialized();
        String consumerKey = topic + "#" + (StringUtils.hasText(tags) ? tags : "*");

        // 创建消费者
        if (!consumers.containsKey(consumerKey)) {
            Properties consumerProps = new Properties();
            consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
            consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumerGroup());
            consumerProps.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId + "-consumer-" + topic);
            consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

            // 合并用户自定义配置
            if (kafkaProperties.getConsumerProps() != null) {
                consumerProps.putAll(kafkaProperties.getConsumerProps());
            }

            KafkaConsumer<String, Object> consumer = new KafkaConsumer<>(consumerProps);
            consumer.subscribe(Collections.singletonList(topic));
            consumers.put(consumerKey, consumer);
        }

        // 注册监听器
        listeners.put(consumerKey, messageListener);

        // 如果消费者正在运行，则不需要再次启动
        if (!consumerRunning && running) {
            startConsumers();
        }

        log.info("已订阅Kafka主题: {}, 标签: {}", topic, tags);
    }

    @Override
    public void unsubscribe(String topic) {
        consumers.entrySet().removeIf(entry -> {
            if (entry.getKey().startsWith(topic + "#")) {
                entry.getValue().close();
                listeners.remove(entry.getKey());
                log.info("已取消订阅Kafka主题: {}", topic);
                return true;
            }
            return false;
        });
    }

    //---------------------
    // 主题管理方法
    //---------------------

    /**
     * 创建预定义主题
     */
    private void createPredefinedTopics() {
        if (adminClient == null) {
            return;
        }

        try {
            // 这里使用mqConfigProperties而不是尝试转换kafkaProperties
            MqConfigProperties.AutoCreateConfig autoCreateConfig = mqConfigProperties.getAutoCreate();

            if (autoCreateConfig.isEnabled() && !CollUtil.isEmpty(autoCreateConfig.getTopics())) {
                List<NewTopic> newTopics = new ArrayList<>();
                for (MqConfigProperties.TopicConfig topicConfig : autoCreateConfig.getTopics()) {
                    NewTopic newTopic = new NewTopic(
                        topicConfig.getName(),
                        topicConfig.getPartitions() > 0 ? topicConfig.getPartitions() : kafkaProperties.getDefaultPartitions(),
                        (short) (topicConfig.getReplicas() > 0 ? topicConfig.getReplicas() : kafkaProperties.getDefaultReplicas())
                    );
                    newTopics.add(newTopic);
                }

                CreateTopicsResult result = adminClient.createTopics(newTopics);
                result.all().get(30, TimeUnit.SECONDS);

                log.info("成功创建预定义Kafka主题: {}",
                    autoCreateConfig.getTopics().stream().map(MqConfigProperties.TopicConfig::getName).toList());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("创建Kafka主题时被中断", e);
        } catch (Exception e) {
            log.warn("部分Kafka主题创建失败", e);
        }
    }

    /**
     * 发送前检查并创建主题
     */
    private void ensureTopicExists(String topic) {
        if (!kafkaProperties.isAutoCreateTopics() || adminClient == null) {
            return;
        }

        try {
            // 检查主题是否存在
            Set<String> existingTopics = adminClient.listTopics().names().get(5, TimeUnit.SECONDS);

            if (!existingTopics.contains(topic)) {
                // 创建新主题
                NewTopic newTopic = new NewTopic(
                    topic,
                    kafkaProperties.getDefaultPartitions(),
                    (short) kafkaProperties.getDefaultReplicas()
                );

                adminClient.createTopics(Collections.singleton(newTopic))
                    .all().get(10, TimeUnit.SECONDS);

                log.info("自动创建Kafka主题: {}", topic);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("检查/创建Kafka主题时被中断: {}", topic, e);
        } catch (Exception e) {
            log.warn("检查/创建Kafka主题失败: {}", topic, e);
        }
    }

    /**
     * 只创建主题，不发送消息
     * 用于从注解自动创建主题
     */
    public void createTopic(String topic) {
        if (!kafkaProperties.isAutoCreateTopics() || adminClient == null) {
            log.info("跳过主题创建：{} (未启用自动创建或adminClient为null)", topic);
            return;
        }

        try {
            // 检查主题是否存在
            Set<String> existingTopics = adminClient.listTopics().names().get(5, TimeUnit.SECONDS);

            if (!existingTopics.contains(topic)) {
                // 创建新主题
                NewTopic newTopic = new NewTopic(
                    topic,
                    kafkaProperties.getDefaultPartitions(),
                    (short) kafkaProperties.getDefaultReplicas()
                );

                adminClient.createTopics(Collections.singleton(newTopic))
                    .all().get(10, TimeUnit.SECONDS);

                log.info("成功创建Kafka主题: {}", topic);
            } else {
                log.info("Kafka主题已存在: {}", topic);
            }
        } catch (Exception e) {
            log.warn("创建Kafka主题失败: {}", topic, e);
        }
    }

    //---------------------
    // 辅助方法
    //---------------------

    /**
     * 启动消费者线程
     */
    private void startConsumers() {
        if (consumers.isEmpty() || consumerRunning) {
            return;
        }

        consumerRunning = true;

        for (Map.Entry<String, KafkaConsumer<String, Object>> entry : consumers.entrySet()) {
            final String consumerKey = entry.getKey();
            final KafkaConsumer<String, Object> consumer = entry.getValue();

            executorService.submit(() -> {
                try {
                    while (consumerRunning) {
                        try {
                            ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(100));

                            for (ConsumerRecord<String, Object> record : records) {
                                processRecord(consumerKey, consumer, record);
                            }

                            consumer.commitSync();
                        } catch (Exception e) {
                            if (consumerRunning) {
                                log.error("处理Kafka消息时发生错误", e);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Kafka消费者线程异常", e);
                }
            });
        }
    }

    /**
     * 处理消费记录
     */
    private void processRecord(String consumerKey, KafkaConsumer<String, Object> consumer, ConsumerRecord<String, Object> record) {
        Function<MqMessage, MqResult<?>> listener = listeners.get(consumerKey);

        if (listener != null) {
            MqMessage message = new MqMessage();
            message.setTopic(record.topic());
            message.setMessageId(record.topic() + "-" + record.partition() + "-" + record.offset());
            message.setPayload(record.value());
            message.setTimestamp(record.timestamp());

            // 转换记录头为消息头
            record.headers().forEach(header ->
                message.addHeader(header.key(), new String(header.value()))
            );

            try {
                listener.apply(message);
            } catch (Exception e) {
                log.error("处理Kafka消息时发生异常: {}", message, e);
                // 异常处理逻辑，可以考虑重试或者进入死信队列
            }
        }
    }

    /**
     * 创建生产者记录
     */
    private ProducerRecord<String, Object> createProducerRecord(MqMessage message) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(
            message.getTopic(),
            message.getMessageId(),
            message.getPayload()
        );

        // 添加消息头
        message.getHeaders().forEach((key, value) ->
            record.headers().add(key, value.toString().getBytes())
        );

        return record;
    }
}
