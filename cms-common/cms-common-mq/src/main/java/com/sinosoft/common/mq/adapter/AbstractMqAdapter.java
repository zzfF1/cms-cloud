package com.sinosoft.common.mq.adapter;

import com.sinosoft.common.mq.core.MqConsumer;
import com.sinosoft.common.mq.core.MqProducer;
import com.sinosoft.common.mq.exception.MqException;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象MQ适配器
 */
@Slf4j
public abstract class AbstractMqAdapter implements MqProducer, MqConsumer {

    // 集群名称
    protected String clusterName;

    // 客户端ID
    protected String clientId;

    // 是否已初始化
    protected volatile boolean initialized = false;

    // 是否正在运行
    protected volatile boolean running = false;

    // 降级模式标志
    protected volatile boolean fallbackMode = false;

    // 配置是否启用降级
    protected boolean fallbackEnabled = true;

    public AbstractMqAdapter(String clusterName) {
        this.clusterName = clusterName;
        this.clientId = generateClientId();
    }

    public AbstractMqAdapter(String clusterName, boolean fallbackEnabled) {
        this(clusterName);
        this.fallbackEnabled = fallbackEnabled;
    }

    /**
     * 生成客户端ID
     */
    protected String generateClientId() {
        return System.getProperty("spring.application.name", "unknown") + "_" +
            System.currentTimeMillis() + "_" +
            Thread.currentThread().getId();
    }

    /**
     * 检查初始化状态，支持降级模式
     */
    protected void checkInitialized() {
        if (!initialized && !fallbackMode) {
            throw new MqException("MQ-001", "MQ客户端尚未初始化");
        }
    }

    /**
     * 检查运行状态，支持降级模式
     */
    protected void checkRunning() {
        if (!running && !fallbackMode) {
            throw new MqException("MQ-002", "MQ客户端未运行");
        }
    }

    @Override
    public void initialize() {
        if (initialized) {
            log.warn("MQ客户端已经初始化，跳过本次初始化");
            return;
        }

        try {
            doInitialize();
            initialized = true;
            fallbackMode = false;
            log.info("MQ客户端初始化成功，客户端ID: {}", clientId);
        } catch (Exception e) {
            log.error("MQ客户端初始化失败: {}", e.getMessage());
            log.debug("MQ初始化异常详情", e);

            if (fallbackEnabled) {
                // 进入降级模式
                log.warn("MQ客户端进入降级模式");
                fallbackMode = true;
            } else {
                throw new MqException("MQ-003", "MQ客户端初始化失败: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void start() {
        if (fallbackMode) {
            log.warn("MQ客户端处于降级模式，忽略启动操作");
            return;
        }

        checkInitialized();
        if (running) {
            log.warn("MQ客户端已经启动，跳过本次启动");
            return;
        }

        try {
            doStart();
            running = true;
            log.info("MQ客户端启动成功，客户端ID: {}", clientId);
        } catch (Exception e) {
            log.error("MQ客户端启动失败: {}", e.getMessage());

            if (fallbackEnabled) {
                // 进入降级模式
                log.warn("MQ客户端进入降级模式");
                fallbackMode = true;
            } else {
                throw new MqException("MQ-004", "MQ客户端启动失败: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void pause() {
        if (fallbackMode) {
            log.warn("MQ客户端处于降级模式，忽略暂停操作");
            return;
        }

        checkInitialized();
        checkRunning();

        try {
            doPause();
            running = false;
            log.info("MQ客户端暂停成功，客户端ID: {}", clientId);
        } catch (Exception e) {
            log.error("MQ客户端暂停失败: {}", e.getMessage());
            throw new MqException("MQ-005", "MQ客户端暂停失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void resume() {
        if (fallbackMode) {
            log.warn("MQ客户端处于降级模式，忽略恢复操作");
            return;
        }

        checkInitialized();
        if (running) {
            log.warn("MQ客户端已经在运行，跳过本次恢复");
            return;
        }

        try {
            doResume();
            running = true;
            log.info("MQ客户端恢复成功，客户端ID: {}", clientId);
        } catch (Exception e) {
            log.error("MQ客户端恢复失败: {}", e.getMessage());
            throw new MqException("MQ-006", "MQ客户端恢复失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void shutdown() {
        if (!initialized && !fallbackMode) {
            log.warn("MQ客户端尚未初始化，跳过关闭操作");
            return;
        }

        try {
            doShutdown();
            initialized = false;
            running = false;
            fallbackMode = false;
            log.info("MQ客户端关闭成功，客户端ID: {}", clientId);
        } catch (Exception e) {
            log.error("MQ客户端关闭失败: {}", e.getMessage());
            throw new MqException("MQ-007", "MQ客户端关闭失败: " + e.getMessage(), e);
        }
    }

    /**
     * 是否处于降级模式
     */
    public boolean isInFallbackMode() {
        return fallbackMode;
    }

    /**
     * 尝试重新连接
     *
     * @return 是否连接成功
     */
    public boolean tryReconnect() {
        if (!fallbackMode) {
            return true; // 已经是正常状态
        }

        log.info("尝试重新连接MQ服务，客户端ID: {}", clientId);
        try {
            // 重置状态
            initialized = false;
            running = false;
            fallbackMode = false;

            // 尝试初始化
            initialize();

            // 如果成功则启动
            if (initialized) {
                start();
            }

            return !fallbackMode;
        } catch (Exception e) {
            log.warn("重连尝试失败: {}", e.getMessage());
            fallbackMode = true;
            return false;
        }
    }

    /**
     * 执行初始化
     */
    protected abstract void doInitialize();

    /**
     * 执行启动
     */
    protected abstract void doStart();

    /**
     * 执行暂停
     */
    protected abstract void doPause();

    /**
     * 执行恢复
     */
    protected abstract void doResume();

    /**
     * 执行关闭
     */
    protected abstract void doShutdown();
}
