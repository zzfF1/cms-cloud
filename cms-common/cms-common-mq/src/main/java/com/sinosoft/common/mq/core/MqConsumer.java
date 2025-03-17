package com.sinosoft.common.mq.core;

import java.util.function.Function;

/**
 * 消息消费者接口
 */
public interface MqConsumer {

    /**
     * 订阅主题
     *
     * @param topic           主题
     * @param tags            标签表达式
     * @param messageListener 消息监听器
     */
    void subscribe(String topic, String tags, Function<MqMessage, MqResult<?>> messageListener);

    /**
     * 取消订阅
     *
     * @param topic 主题
     */
    void unsubscribe(String topic);

    /**
     * 初始化消费者
     */
    void initialize();

    /**
     * 启动消费者
     */
    void start();

    /**
     * 暂停消费者
     */
    void pause();

    /**
     * 恢复消费者
     */
    void resume();

    /**
     * 关闭消费者
     */
    void shutdown();
}
