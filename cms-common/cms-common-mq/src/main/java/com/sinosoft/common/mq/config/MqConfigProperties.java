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
     * 为向后兼容保留，但功能上已不再使用
     */
    private String defaultType = MqType.KAFKA.getType();

    /**
     * 是否启用降级机制（当MQ服务不可用时使用空实现）
     */
    private boolean fallbackEnabled = true;

    /**
     * 是否只作为生产者使用（不消费消息）
     */
    private boolean producerOnly = false;

    /**
     * 是否自动注册消费者
     */
    private boolean consumerEnabled = true;

    /**
     * 主题自动创建配置
     */
    private AutoCreateConfig autoCreate = new AutoCreateConfig();

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
     */
    @Data
    public static class AutoCreateConfig {
        /**
         * 是否启用从注解自动创建主题
         */
        private boolean enabled = true;

        /**
         * 预定义主题列表
         */
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

    /**
     * Kafka配置属性
     */
    @Data
    public static class KafkaProperties {
        private List<KafkaCluster> clusters;
        private String defaultCluster = "default";  // 默认使用default集群
        private String consumerGroup = "default-consumer-group";
        private int consumerThreads = 10;
        private boolean autoCreateTopics = true;
        private int defaultPartitions = 3;
        private int defaultReplicas = 1;
        private Properties producerProps;
        private Properties consumerProps;
        private boolean autoCreateTopicsOnSend = false;

        /**
         * 获取默认集群的Bootstrap Servers
         */
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

        /**
         * 获取指定集群的Bootstrap Servers
         */
        public String getBootstrapServers(String clusterName) {
            if (clusters == null || clusters.isEmpty()) {
                throw new IllegalStateException("未配置Kafka集群");
            }

            for (KafkaCluster cluster : clusters) {
                if (clusterName.equals(cluster.getName())) {
                    return cluster.getBootstrapServers();
                }
            }

            // 如果找不到指定的集群，使用默认集群
            log.warn("未找到Kafka集群: {}, 使用默认集群", clusterName);
            return getBootstrapServers();
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
        private List<RabbitCluster> clusters;
        private String defaultCluster = "default";  // 默认使用default集群
        private String exchangeType = "topic";
        private int prefetchCount = 10;
        private boolean autoCreateQueues = true;
        private boolean autoDeleteQueues = false;
        private boolean durableQueues = true;
        private String msgTtl = "10000";

        /**
         * 获取默认集群的地址
         */
        public String getAddresses() {
            if (clusters == null || clusters.isEmpty()) {
                throw new IllegalStateException("未配置RabbitMQ集群");
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

        /**
         * 获取指定集群的地址
         */
        public String getAddresses(String clusterName) {
            if (clusters == null || clusters.isEmpty()) {
                throw new IllegalStateException("未配置RabbitMQ集群");
            }

            for (RabbitCluster cluster : clusters) {
                if (clusterName.equals(cluster.getName())) {
                    return cluster.getAddresses();
                }
            }

            // 如果找不到指定的集群，使用默认集群
            log.warn("未找到RabbitMQ集群: {}, 使用默认集群", clusterName);
            return getAddresses();
        }

        /**
         * 获取用户名
         */
        public String getUsername() {
            if (clusters != null && !clusters.isEmpty()) {
                if (defaultCluster != null) {
                    for (RabbitCluster cluster : clusters) {
                        if (defaultCluster.equals(cluster.getName()) && cluster.getUsername() != null) {
                            return cluster.getUsername();
                        }
                    }
                }
                return clusters.get(0).getUsername();
            }
            throw new IllegalStateException("未配置RabbitMQ集群");
        }

        /**
         * 获取密码
         */
        public String getPassword() {
            if (clusters != null && !clusters.isEmpty()) {
                if (defaultCluster != null) {
                    for (RabbitCluster cluster : clusters) {
                        if (defaultCluster.equals(cluster.getName()) && cluster.getPassword() != null) {
                            return cluster.getPassword();
                        }
                    }
                }
                return clusters.get(0).getPassword();
            }
            throw new IllegalStateException("未配置RabbitMQ集群");
        }

        /**
         * 获取虚拟主机
         */
        public String getVirtualHost() {
            if (clusters != null && !clusters.isEmpty()) {
                if (defaultCluster != null) {
                    for (RabbitCluster cluster : clusters) {
                        if (defaultCluster.equals(cluster.getName()) && cluster.getVirtualHost() != null) {
                            return cluster.getVirtualHost();
                        }
                    }
                }
                return clusters.get(0).getVirtualHost();
            }
            throw new IllegalStateException("未配置RabbitMQ集群");
        }

        /**
         * 获取默认主机
         */
        public String getHost() {
            String addresses = getAddresses();
            if (addresses.contains(":")) {
                return addresses.split(":")[0];
            }
            return addresses;
        }

        /**
         * 获取默认端口
         */
        public int getPort() {
            String addresses = getAddresses();
            if (addresses.contains(":")) {
                return Integer.parseInt(addresses.split(":")[1]);
            }
            return 5672; // 默认端口
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
        private List<RocketCluster> clusters;
        private String defaultCluster = "default";  // 默认使用default集群
        private String nameServer = "localhost:9876";
        private String producerGroup = "default-producer-group";
        private String consumerGroup = "default-consumer-group";
        private int sendMsgTimeout = 3000;
        private int retryTimes = 2;
        private int consumeThreadMin = 5;
        private int consumeThreadMax = 20;

        /**
         * 获取默认集群的NameServer地址
         */
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

        /**
         * 获取指定集群的NameServer地址
         */
        public String getNameServer(String clusterName) {
            if (clusters == null || clusters.isEmpty()) {
                return nameServer;
            }

            for (RocketCluster cluster : clusters) {
                if (clusterName.equals(cluster.getName())) {
                    return cluster.getNameServer();
                }
            }

            // 如果找不到指定的集群，使用默认集群
            log.warn("未找到RocketMQ集群: {}, 使用默认集群", clusterName);
            return getNameServer();
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
}
