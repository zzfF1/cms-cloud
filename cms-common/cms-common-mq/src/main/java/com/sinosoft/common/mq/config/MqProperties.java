package com.sinosoft.common.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MQ配置属性类，定义全局配置及不同MQ类型的具体配置
 * 通过application.yml或Nacos配置中心进行配置
 */
@Data
@ConfigurationProperties(prefix = "mq")
public class MqProperties {
    /**
     * 是否启用MQ功能，默认为true
     */
    private boolean enabled = true;

    /**
     * 默认MQ类型，可选kafka或rabbit，默认为kafka
     */
    private String defaultType = "kafka";

    /**
     * 连接失败是否启用降级机制，默认为true
     * 当为true时，MQ连接失败不会影响应用启动，会返回一个无操作的发送器
     */
    private boolean fallbackEnabled = true;

    /**
     * 预创建队列配置
     */
    private PreCreateConfig preCreate = new PreCreateConfig();

    /**
     * Kafka集群配置，key为集群名称
     */
    private Map<String, KafkaConfig> kafka = new HashMap<>();

    /**
     * RabbitMQ集群配置，key为集群名称
     */
    private Map<String, RabbitConfig> rabbit = new HashMap<>();

    /**
     * 预创建队列的配置
     */
    @Data
    public static class PreCreateConfig {
        /**
         * 是否启用预创建功能，默认为false
         */
        private boolean enabled = false;

        /**
         * 需要预创建的主题列表
         */
        private List<TopicConfig> topics = new ArrayList<>();
    }

    /**
     * 主题配置类，定义需要创建的主题/队列的属性
     */
    @Data
    public static class TopicConfig {
        /**
         * 主题/队列名称，必填
         */
        private String name;

        /**
         * MQ类型：kafka或rabbit，为空则使用全局defaultType
         */
        private String type;

        /**
         * 集群名称，默认为default
         */
        private String cluster = "default";

        /**
         * 路由键（RabbitMQ），默认为"#"通配符
         */
        private String routingKey = "#";

        /**
         * 分区数（Kafka），默认为3
         */
        private int partitions = 3;

        /**
         * 副本数（Kafka），默认为1
         */
        private int replicas = 1;
    }

    /**
     * Kafka集群配置类
     */
    @Data
    public static class KafkaConfig {
        /**
         * Kafka服务器地址，格式为host:port,host:port
         */
        private String bootstrapServers;

        /**
         * 消费者组ID，默认为default-consumer-group
         */
        private String groupId = "default-consumer-group";

        /**
         * 其他Kafka配置属性
         */
        private Map<String, String> properties = new HashMap<>();
    }

    /**
     * RabbitMQ集群配置类
     */
    @Data
    public static class RabbitConfig {
        /**
         * RabbitMQ服务器主机，默认为localhost
         */
        private String host = "localhost";

        /**
         * RabbitMQ服务器端口，默认为5672
         */
        private int port = 5672;

        /**
         * RabbitMQ用户名
         */
        private String username;

        /**
         * RabbitMQ密码
         */
        private String password;

        /**
         * RabbitMQ虚拟主机，默认为"/"
         */
        private String virtualHost = "/";

        /**
         * 交换机类型，默认为topic
         */
        private String exchangeType = "topic";

        /**
         * 队列是否持久化，默认为true
         */
        private boolean durableQueues = true;
    }
}
