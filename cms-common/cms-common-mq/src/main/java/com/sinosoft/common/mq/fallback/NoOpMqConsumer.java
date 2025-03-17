package com.sinosoft.common.mq.fallback;

import com.sinosoft.common.mq.core.MqConsumer;
import com.sinosoft.common.mq.core.MqMessage;
import com.sinosoft.common.mq.core.MqResult;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * MqConsumer的空实现，用于MQ服务不可用时的降级处理
 */
@Slf4j
public class NoOpMqConsumer implements MqConsumer {

    @Override
    public void subscribe(String topic, String tags, Function<MqMessage, MqResult<?>> messageListener) {
        log.warn("MQ服务不可用，订阅失败: topic={}, tags={}", topic, tags);
    }

    @Override
    public void unsubscribe(String topic) {
        log.warn("MQ服务不可用，取消订阅无效: topic={}", topic);
    }

    @Override
    public void initialize() {
        // 空实现
        log.debug("NoOpMqConsumer初始化");
    }

    @Override
    public void start() {
        // 空实现
        log.debug("NoOpMqConsumer启动");
    }

    @Override
    public void pause() {
        // 空实现
        log.debug("NoOpMqConsumer暂停");
    }

    @Override
    public void resume() {
        // 空实现
        log.debug("NoOpMqConsumer恢复");
    }

    @Override
    public void shutdown() {
        // 空实现
        log.debug("NoOpMqConsumer关闭");
    }
}
