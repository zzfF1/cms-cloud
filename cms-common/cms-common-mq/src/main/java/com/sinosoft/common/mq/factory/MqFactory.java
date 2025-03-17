package com.sinosoft.common.mq.factory;

import com.sinosoft.common.mq.adapter.AbstractMqAdapter;
import com.sinosoft.common.mq.adapter.KafkaMqAdapter;
import com.sinosoft.common.mq.adapter.RabbitMqAdapter;
import com.sinosoft.common.mq.config.MqConfigProperties;
import com.sinosoft.common.mq.core.MqCluster;
import com.sinosoft.common.mq.core.MqConsumer;
import com.sinosoft.common.mq.core.MqProducer;
import com.sinosoft.common.mq.core.MqType;
import com.sinosoft.common.mq.exception.MqException;
import com.sinosoft.common.mq.fallback.NoOpMqConsumer;
import com.sinosoft.common.mq.fallback.NoOpMqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MQ工厂，负责创建和管理MQ实例
 */
@Slf4j
public class MqFactory implements InitializingBean, DisposableBean {

    //--------------------
    // 字段/属性
    //--------------------

    /**
     * MQ配置属性
     */
    private MqConfigProperties configProperties;

    /**
     * 生产者缓存
     * Key格式: type#clusterName
     */
    private final Map<String, MqProducer> producerCache = new ConcurrentHashMap<>();

    /**
     * 消费者缓存
     * Key格式: type#clusterName
     */
    private final Map<String, MqConsumer> consumerCache = new ConcurrentHashMap<>();

    /**
     * 标记MQ服务是否可用
     */
    private volatile boolean mqServiceAvailable = false;

    //--------------------
    // 构造函数
    //--------------------

    /**
     * 构造函数
     */
    public MqFactory(MqConfigProperties configProperties) {
        this.configProperties = configProperties;
        // 在构造函数中就确认是否禁用，避免后续逻辑报错
        if (!configProperties.isEnabled()) {
            mqServiceAvailable = false;
            log.info("MQ功能已禁用");
        }
    }

    //--------------------
    // 初始化和生命周期方法
    //--------------------

    @Override
    public void afterPropertiesSet() {
        // 检查是否启用MQ功能
        if (!configProperties.isEnabled()) {
            log.info("MQ功能已禁用，跳过初始化");
            return;
        }

        // 尝试初始化默认实例
        tryInitDefaultInstances();
    }

    @Override
    public void destroy() {
        // 关闭所有MQ实例
        shutdown();
    }

    //--------------------
    // 主要对外API方法 - 获取生产者
    //--------------------

    /**
     * 获取Kafka默认生产者
     */
    public MqProducer getKafkaProducer() {
        return getProducer(MqCluster.defaultKafka());
    }


    /**
     * 获取指定名称的Kafka集群生产者
     */
    public MqProducer getKafkaProducer(String clusterName) {
        return getProducer(MqCluster.getKafkaCluster(clusterName));
    }

    /**
     * 获取RabbitMQ默认生产者
     */
    public MqProducer getRabbitProducer() {
        return getProducer(MqCluster.defaultRabbit());
    }


    /**
     * 获取指定名称的RabbitMQ集群生产者
     */
    public MqProducer getRabbitProducer(String clusterName) {
        return getProducer(MqCluster.getRabbitCluster(clusterName));
    }

    /**
     * 获取RocketMQ默认生产者
     */
    public MqProducer getRocketProducer() {
        return getProducer(MqCluster.defaultRocket());
    }


    /**
     * 获取指定名称的RocketMQ集群生产者
     */
    public MqProducer getRocketProducer(String clusterName) {
        return getProducer(MqCluster.getRocketCluster(clusterName));
    }

    /**
     * 获取指定集群的生产者（使用枚举类型增强类型安全）
     */
    public MqProducer getProducer(MqCluster mqCluster) {
        // 检查MQ功能是否启用
        if (!configProperties.isEnabled()) {
            log.warn("MQ功能已禁用，返回空实现");
            return new NoOpMqProducer();
        }

        // 检查MQ服务可用性
        checkMqServiceAndInit();

        String cacheKey = mqCluster.getMqType() + "#" + mqCluster.getClusterName();

        // 检查缓存
        MqProducer producer = producerCache.get(cacheKey);
        if (producer != null) {
            return producer;
        }

        // 如果MQ服务不可用且启用了降级，返回一个空实现
        if (!mqServiceAvailable && configProperties.isFallbackEnabled()) {
            log.warn("MQ服务不可用，返回空实现生产者");
            return new NoOpMqProducer();
        }

        // 创建新实例
        AbstractMqAdapter adapter = createAdapter(mqCluster);
        try {
            adapter.initialize();
            mqServiceAvailable = true;

            // 缓存并返回
            producerCache.put(cacheKey, adapter);
            return adapter;
        } catch (Exception e) {
            mqServiceAvailable = false;
            log.error("无法初始化MQ生产者: {}", e.getMessage());
            log.debug("初始化异常详情", e);

            if (configProperties.isFallbackEnabled()) {
                log.warn("返回空实现生产者作为降级策略");
                return new NoOpMqProducer();
            }
            throw e;
        }
    }

    //--------------------
    // 主要对外API方法 - 获取消费者
    //--------------------

    /**
     * 获取Kafka默认消费者
     */
    public MqConsumer getKafkaConsumer() {
        return getConsumer(MqCluster.defaultKafka());
    }


    /**
     * 获取指定名称的Kafka集群消费者
     */
    public MqConsumer getKafkaConsumer(String clusterName) {
        return getConsumer(MqCluster.getKafkaCluster(clusterName));
    }

    /**
     * 获取RabbitMQ默认消费者
     */
    public MqConsumer getRabbitConsumer() {
        return getConsumer(MqCluster.defaultRabbit());
    }


    /**
     * 获取指定名称的RabbitMQ集群消费者
     */
    public MqConsumer getRabbitConsumer(String clusterName) {
        return getConsumer(MqCluster.getRabbitCluster(clusterName));
    }

    /**
     * 获取RocketMQ默认消费者
     */
    public MqConsumer getRocketConsumer() {
        return getConsumer(MqCluster.defaultRocket());
    }


    /**
     * 获取指定名称的RocketMQ集群消费者
     */
    public MqConsumer getRocketConsumer(String clusterName) {
        return getConsumer(MqCluster.getRocketCluster(clusterName));
    }

    /**
     * 获取指定集群的消费者（使用枚举类型增强类型安全）
     */
    public MqConsumer getConsumer(MqCluster mqCluster) {
        // 检查MQ功能是否启用
        if (!configProperties.isEnabled()) {
            log.warn("MQ功能已禁用，返回空实现");
            return new NoOpMqConsumer();
        }

        // 检查MQ服务可用性
        checkMqServiceAndInit();

        String cacheKey = mqCluster.getMqType() + "#" + mqCluster.getClusterName();

        // 检查缓存
        MqConsumer consumer = consumerCache.get(cacheKey);
        if (consumer != null) {
            return consumer;
        }

        // 如果MQ服务不可用且启用了降级，返回一个空实现
        if (!mqServiceAvailable && configProperties.isFallbackEnabled()) {
            log.warn("MQ服务不可用，返回空实现消费者");
            return new NoOpMqConsumer();
        }

        // 创建新实例
        AbstractMqAdapter adapter = createAdapter(mqCluster);
        try {
            adapter.initialize();
            mqServiceAvailable = true;

            // 缓存并返回
            consumerCache.put(cacheKey, adapter);
            return adapter;
        } catch (Exception e) {
            mqServiceAvailable = false;
            log.error("无法初始化MQ消费者: {}", e.getMessage());
            log.debug("初始化异常详情", e);

            if (configProperties.isFallbackEnabled()) {
                log.warn("返回空实现消费者作为降级策略");
                return new NoOpMqConsumer();
            }
            throw e;
        }
    }

    //--------------------
    // 主题管理方法
    //--------------------

    /**
     * 根据注解信息创建主题
     */
    public void createTopic(String topic, MqType mqType, String clusterName) {
        // 检查MQ功能是否启用
        if (!configProperties.isEnabled()) {
            log.warn("MQ功能已禁用，跳过主题创建: {}", topic);
            return;
        }

        MqCluster mqCluster = MqCluster.fromTypeAndName(mqType, clusterName);
        if (mqCluster == null) {
            log.warn("未找到指定的集群配置: type={}, name={}, 使用该类型的默认集群", mqType, clusterName);
            switch (mqType) {
                case KAFKA:
                    mqCluster = MqCluster.defaultKafka();
                    break;
                case RABBIT_MQ:
                    mqCluster = MqCluster.defaultRabbit();
                    break;
                case ROCKET_MQ:
                    mqCluster = MqCluster.defaultRocket();
                    break;
                default:
                    throw new MqException("MQ-102", "不支持的MQ类型: " + mqType);
            }
        }

        createTopic(topic, mqCluster);
    }

    /**
     * 使用MqCluster创建主题
     */
    public void createTopic(String topic, MqCluster mqCluster) {
        // 检查MQ功能是否启用
        if (!configProperties.isEnabled()) {
            log.warn("MQ功能已禁用，跳过主题创建: {}", topic);
            return;
        }

        // 获取或创建适配器
        AbstractMqAdapter adapter;
        String cacheKey = mqCluster.getMqType() + "#" + mqCluster.getClusterName();

        // 先检查缓存中是否已有适配器
        MqProducer cachedProducer = producerCache.get(cacheKey);
        if (cachedProducer instanceof AbstractMqAdapter) {
            adapter = (AbstractMqAdapter) cachedProducer;
        } else {
            adapter = createAdapter(mqCluster);
            try {
                adapter.initialize();
            } catch (Exception e) {
                log.error("初始化适配器失败，无法创建主题: {}, 错误: {}", topic, e.getMessage());
                return;
            }
        }

        // 根据适配器类型创建主题
        try {
            if (adapter instanceof KafkaMqAdapter) {
                ((KafkaMqAdapter) adapter).createTopic(topic);
            } else if (adapter instanceof RabbitMqAdapter) {
                ((RabbitMqAdapter) adapter).createExchangeAndQueue(topic);
            } else {
                log.warn("未知的适配器类型，无法创建主题: {}", topic);
                return;
            }

            log.info("已创建主题: {}, 集群: {}", topic, mqCluster.getDescription());
        } catch (Exception e) {
            log.error("创建主题失败: {}, 集群: {}, 错误: {}",
                topic, mqCluster.getDescription(), e.getMessage());
        }
    }

    //--------------------
    // 向后兼容的方法
    //--------------------

    /**
     * 为了向后兼容，提供接受MqType和集群名字符串的方法
     */
    public MqProducer getProducer(MqType mqType, String clusterName) {
        MqCluster cluster = MqCluster.fromTypeAndName(mqType, clusterName);
        if (cluster == null) {
            log.warn("未找到指定的集群配置: type={}, name={}, 使用该类型的默认集群", mqType, clusterName);
            // 使用该类型的默认集群
            switch (mqType) {
                case KAFKA:
                    cluster = MqCluster.defaultKafka();
                    break;
                case RABBIT_MQ:
                    cluster = MqCluster.defaultRabbit();
                    break;
                case ROCKET_MQ:
                    cluster = MqCluster.defaultRocket();
                    break;
                default:
                    throw new MqException("MQ-102", "不支持的MQ类型: " + mqType);
            }
        }
        return getProducer(cluster);
    }

    /**
     * 为了向后兼容，提供接受MqType和集群名字符串的方法
     */
    public MqConsumer getConsumer(MqType mqType, String clusterName) {
        MqCluster cluster = MqCluster.fromTypeAndName(mqType, clusterName);
        if (cluster == null) {
            log.warn("未找到指定的集群配置: type={}, name={}, 使用该类型的默认集群", mqType, clusterName);
            // 使用该类型的默认集群
            switch (mqType) {
                case KAFKA:
                    cluster = MqCluster.defaultKafka();
                    break;
                case RABBIT_MQ:
                    cluster = MqCluster.defaultRabbit();
                    break;
                case ROCKET_MQ:
                    cluster = MqCluster.defaultRocket();
                    break;
                default:
                    throw new MqException("MQ-102", "不支持的MQ类型: " + mqType);
            }
        }
        return getConsumer(cluster);
    }

    /**
     * 获取默认生产者（向后兼容）
     */
    public MqProducer getProducer() {
        return getKafkaProducer();
    }

    /**
     * 获取默认消费者（向后兼容）
     */
    public MqConsumer getConsumer() {
        return getKafkaConsumer();
    }

    //--------------------
    // 内部辅助方法
    //--------------------

    /**
     * 尝试初始化默认实例，但捕获异常不影响应用启动
     */
    private void tryInitDefaultInstances() {
        try {
            // 初始化所有类型的默认实例
            initDefaultInstances();
            mqServiceAvailable = true;
        } catch (Exception e) {
            log.warn("MQ服务初始化失败，应用将继续启动但MQ功能不可用: {}", e.getMessage());
            log.debug("MQ初始化异常详情", e);
            mqServiceAvailable = false;
        }
    }

    /**
     * 初始化默认MQ实例
     */
    private void initDefaultInstances() {
        // 预初始化所有类型的默认实例
        getKafkaProducer();
        getRabbitProducer();
        getRocketProducer();
    }

    /**
     * 检查MQ服务可用性，如果需要则初始化
     */
    private void checkMqServiceAndInit() {
        // 简化的双重检查逻辑，移除initializing标志
        if (!mqServiceAvailable) {
            synchronized (this) {
                if (!mqServiceAvailable) {
                    try {
                        // 尝试初始化默认实例
                        initDefaultInstances();
                        mqServiceAvailable = true;
                    } catch (Exception e) {
                        log.warn("MQ服务初始化失败: {}", e.getMessage());
                        log.debug("MQ初始化异常详情", e);
                        mqServiceAvailable = false;
                    }
                }
            }
        }

        // 如果服务不可用且未启用降级，则抛出异常
        if (!mqServiceAvailable && !configProperties.isFallbackEnabled()) {
            throw new MqException("MQ-SERVICE-UNAVAILABLE", "MQ服务当前不可用");
        }
    }

    /**
     * 创建适配器实例（使用MqCluster枚举）
     */
    private AbstractMqAdapter createAdapter(MqCluster mqCluster) {
        MqType mqType = mqCluster.getMqTypeEnum();
        String clusterName = mqCluster.getClusterName();

        switch (mqType) {
            case KAFKA:
                return new KafkaMqAdapter(clusterName, configProperties.getKafka(), configProperties);
            case RABBIT_MQ:
                return new RabbitMqAdapter(clusterName, configProperties.getRabbit(), configProperties);
            case ROCKET_MQ:
                // 可以添加RocketMQ适配器的实现
                // return new RocketMqAdapter(clusterName, configProperties.getRocket(), configProperties);
            default:
                throw new MqException("MQ-101", "不支持的MQ类型: " + mqType);
        }
    }

    //--------------------
    // 配置更新和状态方法
    //--------------------

    /**
     * 更新MQ配置
     */
    public synchronized void updateConfig(MqConfigProperties newConfig) {
        Assert.notNull(newConfig, "MQ配置不能为空");

        // 保存新配置
        this.configProperties = newConfig;

        // 重新初始化所有实例
        reinitializeAll();

        log.info("MQ配置已更新");
    }

    /**
     * 重新初始化所有实例
     */
    private void reinitializeAll() {
        // 关闭所有实例
        shutdown();

        // 清空缓存
        producerCache.clear();
        consumerCache.clear();

        // 检查是否启用MQ功能
        if (!configProperties.isEnabled()) {
            log.info("MQ功能已禁用，跳过重新初始化");
            return;
        }

        // 重新初始化默认实例
        tryInitDefaultInstances();
    }

    /**
     * 检查MQ服务是否可用
     */
    public boolean isMqServiceAvailable() {
        return mqServiceAvailable || !configProperties.isEnabled();
    }

    //--------------------
    // 关闭和清理方法
    //--------------------

    /**
     * 关闭所有MQ实例
     */
    private void shutdown() {
        // 关闭所有生产者
        for (MqProducer producer : producerCache.values()) {
            try {
                producer.shutdown();
            } catch (Exception e) {
                log.warn("关闭MQ生产者时发生错误", e);
            }
        }

        // 关闭所有消费者
        for (MqConsumer consumer : consumerCache.values()) {
            try {
                consumer.shutdown();
            } catch (Exception e) {
                log.warn("关闭MQ消费者时发生错误", e);
            }
        }
    }
}
