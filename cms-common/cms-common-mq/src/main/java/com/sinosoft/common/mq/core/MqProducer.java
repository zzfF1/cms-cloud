package com.sinosoft.common.mq.core;

import java.util.concurrent.CompletableFuture;

/**
 * 消息生产者接口
 */
public interface MqProducer {

    /**
     * 同步发送消息
     *
     * @param message 消息
     * @return 发送结果
     */
    MqResult<String> send(MqMessage message);

    /**
     * 异步发送消息
     *
     * @param message 消息
     * @return 异步结果
     */
    CompletableFuture<MqResult<String>> sendAsync(MqMessage message);

    /**
     * 发送延迟消息
     *
     * @param message 消息
     * @param delayTime 延迟时间(毫秒)
     * @return 发送结果
     */
    MqResult<String> sendDelay(MqMessage message, long delayTime);

    /**
     * 发送事务消息
     *
     * @param message 消息
     * @param arg 事务参数
     * @return 发送结果
     */
    MqResult<String> sendTransactional(MqMessage message, Object arg);

    /**
     * 初始化生产者
     */
    void initialize();

    /**
     * 关闭生产者
     */
    void shutdown();
}
