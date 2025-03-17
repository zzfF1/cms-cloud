package com.sinosoft.common.mq.core;

import lombok.Getter;

/**
 * MQ集群枚举，按功能分类
 * 命名规范：MQ类型_功能
 */
@Getter
public enum MqCluster {

    //==========================
    // Kafka集群
    //==========================
    KAFKA_DEFAULT("kafka", "default", "Kafka默认集群"),

    //==========================
    // RabbitMQ集群
    //==========================
    RABBIT_DEFAULT("rabbit", "default", "RabbitMQ默认集群"),

    //==========================
    // RocketMQ集群
    //==========================
    ROCKET_DEFAULT("rocket", "default", "RocketMQ默认集群");

    private final String mqType;
    private final String clusterName;
    private final String description;

    MqCluster(String mqType, String clusterName, String description) {
        this.mqType = mqType;
        this.clusterName = clusterName;
        this.description = description;
    }

    /**
     * 获取对应的MqType枚举
     *
     * @return MqType枚举
     */
    public MqType getMqTypeEnum() {
        return MqType.fromString(mqType);
    }

    /**
     * 根据MqType和集群名获取对应的MqCluster
     *
     * @param mqType      MQ类型
     * @param clusterName 集群名称
     * @return 对应的MqCluster枚举，找不到时返回null
     */
    public static MqCluster fromTypeAndName(String mqType, String clusterName) {
        for (MqCluster cluster : values()) {
            if (cluster.mqType.equals(mqType) && cluster.clusterName.equals(clusterName)) {
                return cluster;
            }
        }
        return null;
    }

    /**
     * 根据MqType和集群名获取对应的MqCluster
     *
     * @param mqType      MQ类型
     * @param clusterName 集群名称
     * @return 对应的MqCluster枚举，找不到时返回null
     */
    public static MqCluster fromTypeAndName(MqType mqType, String clusterName) {
        return fromTypeAndName(mqType.getType(), clusterName);
    }


    /**
     * 获取Kafka默认集群
     *
     * @return Kafka默认集群
     */
    public static MqCluster getKafkaDefault() {
        return KAFKA_DEFAULT;
    }


    /**
     * 获取RabbitMQ默认集群
     *
     * @return RabbitMQ默认集群
     */
    public static MqCluster getRabbitDefault() {
        return RABBIT_DEFAULT;
    }


    /**
     * 获取RocketMQ默认集群
     *
     * @return RocketMQ默认集群
     */
    public static MqCluster getRocketDefault() {
        return ROCKET_DEFAULT;
    }

    /**
     * 获取Kafka集群
     *
     * @param clusterName 集群名称，如果为null则返回默认集群
     * @return 匹配的MqCluster
     */
    public static MqCluster getKafkaCluster(String clusterName) {
        if (clusterName == null || clusterName.isEmpty() || "default".equals(clusterName)) {
            return KAFKA_DEFAULT;
        }

        MqCluster cluster = fromTypeAndName("kafka", clusterName);
        return cluster != null ? cluster : KAFKA_DEFAULT;
    }

    /**
     * 获取RabbitMQ集群
     *
     * @param clusterName 集群名称，如果为null则返回默认集群
     * @return 匹配的MqCluster
     */
    public static MqCluster getRabbitCluster(String clusterName) {
        if (clusterName == null || clusterName.isEmpty() || "default".equals(clusterName)) {
            return RABBIT_DEFAULT;
        }

        MqCluster cluster = fromTypeAndName("rabbit", clusterName);
        return cluster != null ? cluster : RABBIT_DEFAULT;
    }

    /**
     * 获取RocketMQ集群
     *
     * @param clusterName 集群名称，如果为null则返回默认集群
     * @return 匹配的MqCluster
     */
    public static MqCluster getRocketCluster(String clusterName) {
        if (clusterName == null || clusterName.isEmpty() || "default".equals(clusterName)) {
            return ROCKET_DEFAULT;
        }

        MqCluster cluster = fromTypeAndName("rocket", clusterName);
        return cluster != null ? cluster : ROCKET_DEFAULT;
    }

    /**
     * 默认Kafka集群
     */
    public static MqCluster defaultKafka() {
        return KAFKA_DEFAULT;
    }

    /**
     * 默认RabbitMQ集群
     */
    public static MqCluster defaultRabbit() {
        return RABBIT_DEFAULT;
    }

    /**
     * 默认RocketMQ集群
     */
    public static MqCluster defaultRocket() {
        return ROCKET_DEFAULT;
    }
}
