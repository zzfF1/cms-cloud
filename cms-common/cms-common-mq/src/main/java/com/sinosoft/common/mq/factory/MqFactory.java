package com.sinosoft.common.mq.factory;

import com.sinosoft.common.mq.adapter.AbstractMqAdapter;
import com.sinosoft.common.mq.adapter.KafkaMqAdapter;
import com.sinosoft.common.mq.adapter.RabbitMqAdapter;
import com.sinosoft.common.mq.config.MqConfigProperties;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private volatile boolean initializing = false;

    /**
     * 用于重试连接的调度器
     */
    private ScheduledExecutorService reconnectScheduler;

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

        // 判断是否启用延迟初始化
        if (configProperties.isLazyInit()) {
            log.info("MQ懒加载模式启用，将在首次使用时初始化MQ客户端");
            return;
        }

        if (configProperties.isAutoReconnect()) {
            // 启动重连调度器
            startReconnectScheduler();
        } else {
            // 尝试初始化，但不阻止应用启动
            tryInitDefaultInstances();
        }
    }

    @Override
    public void destroy() {
        // 关闭重连调度器
        if (reconnectScheduler != null) {
            reconnectScheduler.shutdownNow();
        }

        // 关闭所有MQ实例
        shutdown();
    }

    //--------------------
    // 主要对外API方法
    //--------------------

    /**
     * 获取默认类型的生产者
     */
    public MqProducer getProducer() {
        // 检查MQ功能是否启用
        if (!configProperties.isEnabled()) {
            log.warn("MQ功能已禁用，返回空实现");
            return new NoOpMqProducer();
        }

        // 检查MQ服务可用性
        checkMqServiceAndInit();

        MqType defaultType = MqType.fromString(configProperties.getDefaultType());
        return getProducer(defaultType, null);
    }

    /**
     * 获取指定类型的生产者
     */
    public MqProducer getProducer(MqType mqType, String clusterName) {
        // 检查MQ功能是否启用
        if (!configProperties.isEnabled()) {
            log.warn("MQ功能已禁用，返回空实现");
            return new NoOpMqProducer();
        }

        // 检查MQ服务可用性
        checkMqServiceAndInit();

        String cacheKey = mqType.getType() + "#" + (clusterName != null ? clusterName : "default");

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
        AbstractMqAdapter adapter = createAdapter(mqType, clusterName);
        try {
            adapter.initialize();
            mqServiceAvailable = true;

            // 缓存并返回
            producerCache.put(cacheKey, adapter);
            return adapter;
        } catch (Exception e) {
            mqServiceAvailable = false;
            log.error("无法初始化MQ生产者: {}", e.getMessage());

            if (configProperties.isFallbackEnabled()) {
                log.warn("返回空实现生产者作为降级策略");
                return new NoOpMqProducer();
            }
            throw e;
        }
    }

    /**
     * 获取默认类型的消费者
     */
    public MqConsumer getConsumer() {
        // 检查MQ功能是否启用
        if (!configProperties.isEnabled()) {
            log.warn("MQ功能已禁用，返回空实现");
            return new NoOpMqConsumer();
        }

        checkMqServiceAndInit();
        MqType defaultType = MqType.fromString(configProperties.getDefaultType());
        return getConsumer(defaultType, null);
    }

    /**
     * 获取指定类型的消费者
     */
    public MqConsumer getConsumer(MqType mqType, String clusterName) {
        // 检查MQ功能是否启用
        if (!configProperties.isEnabled()) {
            log.warn("MQ功能已禁用，返回空实现");
            return new NoOpMqConsumer();
        }

        // 检查MQ服务可用性
        checkMqServiceAndInit();

        String cacheKey = mqType.getType() + "#" + (clusterName != null ? clusterName : "default");

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
        AbstractMqAdapter adapter = createAdapter(mqType, clusterName);
        try {
            adapter.initialize();
            mqServiceAvailable = true;

            // 缓存并返回
            consumerCache.put(cacheKey, adapter);
            return adapter;
        } catch (Exception e) {
            mqServiceAvailable = false;
            log.error("无法初始化MQ消费者: {}", e.getMessage());

            if (configProperties.isFallbackEnabled()) {
                log.warn("返回空实现消费者作为降级策略");
                return new NoOpMqConsumer();
            }
            throw e;
        }
    }

    /**
     * 根据注解信息创建主题
     */
    public void createTopic(String topic, MqType mqType, String clusterName) {
        // 检查MQ功能是否启用
        if (!configProperties.isEnabled()) {
            log.warn("MQ功能已禁用，跳过主题创建: {}", topic);
            return;
        }

        // 获取或创建适配器
        AbstractMqAdapter adapter;
        String cacheKey = mqType.getType() + "#" + (clusterName != null ? clusterName : "default");

        // 先检查缓存中是否已有适配器
        MqProducer cachedProducer = producerCache.get(cacheKey);
        if (cachedProducer instanceof AbstractMqAdapter) {
            adapter = (AbstractMqAdapter) cachedProducer;
        } else {
            adapter = createAdapter(mqType, clusterName);
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

            log.info("已创建主题: {}, 类型: {}, 集群: {}", topic, mqType, clusterName);
        } catch (Exception e) {
            log.error("创建主题失败: {}, 类型: {}, 集群: {}, 错误: {}",
                topic, mqType, clusterName, e.getMessage());
        }
    }

    //--------------------
    // 内部辅助方法
    //--------------------

    /**
     * 启动重连调度器
     */
    private void startReconnectScheduler() {
        reconnectScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "mq-reconnect-scheduler");
            thread.setDaemon(true); // 使用守护线程，不阻止应用关闭
            return thread;
        });

        // 立即执行一次，然后按配置的间隔重试
        reconnectScheduler.scheduleWithFixedDelay(() -> {
            if (!mqServiceAvailable) {
                log.info("尝试连接MQ服务...");
                tryInitDefaultInstances();
            }
        }, 0, configProperties.getReconnectIntervalSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 尝试初始化默认实例，但捕获异常不影响应用启动
     */
    private void tryInitDefaultInstances() {
        try {
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
        MqType defaultType = MqType.fromString(configProperties.getDefaultType());

        // 为每种MQ类型初始化默认实例
        switch (defaultType) {
            case KAFKA:
                getProducer(MqType.KAFKA, null);
                break;
            case RABBIT_MQ:
                getProducer(MqType.RABBIT_MQ, null);
                break;
            case ROCKET_MQ:
                getProducer(MqType.ROCKET_MQ, null);
                break;
            default:
                log.warn("未知的MQ类型: {}", defaultType);
        }
    }

    /**
     * 检查MQ服务可用性，如果启用了延迟初始化则尝试初始化
     */
    private void checkMqServiceAndInit() {
        if (!mqServiceAvailable && configProperties.isLazyInit() && !initializing) {
            synchronized (this) {
                if (!mqServiceAvailable && !initializing) {
                    initializing = true;
                    try {
                        tryInitDefaultInstances();
                    } finally {
                        initializing = false;
                    }
                }
            }
        }

        if (!mqServiceAvailable && !configProperties.isFallbackEnabled()) {
            throw new MqException("MQ-SERVICE-UNAVAILABLE", "MQ服务当前不可用");
        }
    }

    /**
     * 创建适配器实例
     */
    private AbstractMqAdapter createAdapter(MqType mqType, String clusterName) {
        if (clusterName == null) {
            clusterName = "default";
        }

        switch (mqType) {
            case KAFKA:
                return new KafkaMqAdapter(clusterName, configProperties.getKafka(), configProperties);
            case RABBIT_MQ:
                return new RabbitMqAdapter(clusterName, configProperties.getRabbit(), configProperties);
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
        if (!configProperties.isLazyInit()) {
            tryInitDefaultInstances();
        }
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
