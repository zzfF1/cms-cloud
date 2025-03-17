package com.sinosoft.common.mq.config;

import com.sinosoft.common.mq.core.MqType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * MQ配置属性类
 */
@Slf4j
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "mq")
public class MqConfigProperties {

    /**
     * 是否启用MQ功能
     */
    private boolean enabled = true;

    /**
     * 默认MQ类型
     */
    private String defaultType = MqType.KAFKA.getType();

    /**
     * 是否懒加载（首次使用时初始化）
     */
    private boolean lazyInit = false;

    /**
     * 是否启用降级机制（当MQ服务不可用时使用空实现）
     */
    private boolean fallbackEnabled = true;

    /**
     * 是否自动重连
     */
    private boolean autoReconnect = true;

    /**
     * 重连间隔（秒）
     */
    private int reconnectIntervalSeconds = 30;

    /**
     * 最大重连次数，-1表示无限重试
     */
    private int maxReconnectAttempts = -1;

    /**
     * 是否只作为生产者使用（不消费消息）
     */
    private boolean producerOnly = false;

    /**
     * 是否自动注册消费者
     */
    private boolean consumerEnabled = true;

    /**
     * Kafka配置
     */
    private KafkaProperties kafka = new KafkaProperties();

    /**
     * RabbitMQ配置
     */
    private RabbitProperties rabbit = new RabbitProperties();

    /**
     * RocketMQ配置
     */
    private RocketMqProperties rocket = new RocketMqProperties();

    /**
     * 主题自动创建配置
     * 简化为只保留一个启用开关，不再需要声明主题列表
     */
    private AutoCreateConfig autoCreate = new AutoCreateConfig();


    public void setKafka(KafkaProperties kafka) {
        this.kafka = kafka;
        log.info("Kafka配置被设置: default-cluster={}, clusters={}",
            kafka.getDefaultCluster(),
            kafka.getClusters() != null ? kafka.getClusters().stream()
                .map(c -> c.getName() + ":" + c.getBootstrapServers())
                .collect(Collectors.joining(",")) : "null");
    }

    public void setRabbit(RabbitProperties rabbit) {
        this.rabbit = rabbit;
        log.info("rabbit配置被设置: default-cluster={}, clusters={}",
            rabbit.getDefaultCluster(),
            rabbit.getClusters() != null ? rabbit.getClusters().stream()
                .map(c -> c.getName() + ":" + c.getAddresses())
                .collect(Collectors.joining(",")) : "null");
    }

    /**
     * Kafka配置属性
     */
    @Data
    public static class KafkaProperties {
        // 原有属性
        private List<KafkaCluster> clusters;
        private String defaultCluster;
        private int consumerThreads = 10;
        private String consumerGroup = "default-consumer-group";
        private Properties producerProps;
        private Properties consumerProps;
        private boolean autoCreateTopics = true;
        private int defaultPartitions = 3;
        private int defaultReplicas = 1;

        /**
         * 是否在发送消息时自动创建主题
         */
        private boolean autoCreateTopicsOnSend = false;

        // 原有方法
        public String getBootstrapServers() {
            if (clusters == null || clusters.isEmpty()) {
                throw new IllegalStateException("未配置Kafka集群");
            }

            if (defaultCluster != null) {
                for (KafkaCluster cluster : clusters) {
                    if (defaultCluster.equals(cluster.getName())) {
                        return cluster.getBootstrapServers();
                    }
                }
            }

            return clusters.get(0).getBootstrapServers();
        }

        public String getBootstrapServers(String clusterName) {
            if (clusters == null || clusters.isEmpty()) {
                throw new IllegalStateException("未配置Kafka集群");
            }

            for (KafkaCluster cluster : clusters) {
                if (clusterName.equals(cluster.getName())) {
                    return cluster.getBootstrapServers();
                }
            }

            throw new IllegalArgumentException("未找到Kafka集群: " + clusterName);
        }
    }

    /**
     * Kafka集群配置
     */
    @Data
    public static class KafkaCluster {
        private String name;
        private String bootstrapServers;
    }

    /**
     * RabbitMQ配置属性
     */
    @Data
    public static class RabbitProperties {
        // 原有属性
        private List<RabbitCluster> clusters;
        private String defaultCluster;
        private String host = "localhost";
        private int port = 5672;
        private String username = "ruoyi";
        private String password = "ruoyi";
        private String virtualHost = "/";
        private String exchangeType = "topic";
        private int prefetchCount = 10;
        private String msgTtl = "10000";
        private boolean autoCreateQueues = true;
        private boolean autoDeleteQueues = false;
        private boolean durableQueues = true;

        // 原有方法
        public String getAddresses() {
            if (clusters == null || clusters.isEmpty()) {
                return host + ":" + port;
            }

            if (defaultCluster != null) {
                for (RabbitCluster cluster : clusters) {
                    if (defaultCluster.equals(cluster.getName())) {
                        return cluster.getAddresses();
                    }
                }
            }

            return clusters.get(0).getAddresses();
        }

        public String getAddresses(String clusterName) {
            if (clusters == null || clusters.isEmpty()) {
                return host + ":" + port;
            }

            for (RabbitCluster cluster : clusters) {
                if (clusterName.equals(cluster.getName())) {
                    return cluster.getAddresses();
                }
            }

            throw new IllegalArgumentException("未找到RabbitMQ集群: " + clusterName);
        }
    }

    /**
     * RabbitMQ集群配置
     */
    @Data
    public static class RabbitCluster {
        private String name;
        private String addresses;
        private String username;
        private String password;
        private String virtualHost;
    }

    /**
     * RocketMQ配置属性
     */
    @Data
    public static class RocketMqProperties {
        // 原有属性
        private List<RocketCluster> clusters;
        private String defaultCluster;
        private String nameServer = "localhost:9876";
        private String producerGroup = "default-producer-group";
        private String consumerGroup = "default-consumer-group";
        private int sendMsgTimeout = 3000;
        private int retryTimes = 2;
        private int consumeThreadMin = 5;
        private int consumeThreadMax = 20;

        // 原有方法
        public String getNameServer() {
            if (clusters == null || clusters.isEmpty()) {
                return nameServer;
            }

            if (defaultCluster != null) {
                for (RocketCluster cluster : clusters) {
                    if (defaultCluster.equals(cluster.getName())) {
                        return cluster.getNameServer();
                    }
                }
            }

            return clusters.get(0).getNameServer();
        }

        public String getNameServer(String clusterName) {
            if (clusters == null || clusters.isEmpty()) {
                return nameServer;
            }

            for (RocketCluster cluster : clusters) {
                if (clusterName.equals(cluster.getName())) {
                    return cluster.getNameServer();
                }
            }

            throw new IllegalArgumentException("未找到RocketMQ集群: " + clusterName);
        }
    }

    /**
     * RocketMQ集群配置
     */
    @Data
    public static class RocketCluster {
        private String name;
        private String nameServer;
    }

    /**
     * 主题自动创建配置 (简化版)
     */
    @Data
    public static class AutoCreateConfig {
        /**
         * 是否启用从注解自动创建主题
         */
        private boolean enabled = true;

        /**
         * 预定义主题列表，已废弃，保留向后兼容
         * 现在使用注解扫描自动创建
         */
        @Deprecated
        private List<TopicConfig> topics = new ArrayList<>();
    }

    /**
     * 主题配置
     */
    @Data
    public static class TopicConfig {
        private String name;
        private int partitions = 3;
        private int replicas = 1;
    }
}
