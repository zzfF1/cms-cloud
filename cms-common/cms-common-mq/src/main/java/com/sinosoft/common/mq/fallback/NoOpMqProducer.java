package com.sinosoft.common.mq.fallback;

import com.sinosoft.common.mq.core.MqConsumer;
import com.sinosoft.common.mq.core.MqMessage;
import com.sinosoft.common.mq.core.MqProducer;
import com.sinosoft.common.mq.core.MqResult;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * MqProducer的空实现，用于MQ服务不可用时的降级处理
 */
@Slf4j
public class NoOpMqProducer implements MqProducer {

    @Override
    public MqResult<String> send(MqMessage message) {
        log.warn("MQ服务不可用，消息未发送: {}", message);
        return MqResult.failure("MQ-UNAVAILABLE", "MQ服务当前不可用，消息未发送");
    }

    @Override
    public CompletableFuture<MqResult<String>> sendAsync(MqMessage message) {
        log.warn("MQ服务不可用，异步消息未发送: {}", message);
        CompletableFuture<MqResult<String>> future = new CompletableFuture<>();
        future.complete(MqResult.failure("MQ-UNAVAILABLE", "MQ服务当前不可用，消息未发送"));
        return future;
    }

    @Override
    public MqResult<String> sendDelay(MqMessage message, long delayTime) {
        log.warn("MQ服务不可用，延迟消息未发送: {}", message);
        return MqResult.failure("MQ-UNAVAILABLE", "MQ服务当前不可用，消息未发送");
    }

    @Override
    public MqResult<String> sendTransactional(MqMessage message, Object arg) {
        log.warn("MQ服务不可用，事务消息未发送: {}", message);
        return MqResult.failure("MQ-UNAVAILABLE", "MQ服务当前不可用，消息未发送");
    }

    @Override
    public void initialize() {
        // 空实现
        log.debug("NoOpMqProducer初始化");
    }

    @Override
    public void shutdown() {
        // 空实现
        log.debug("NoOpMqProducer关闭");
    }
}

