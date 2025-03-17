package com.sinosoft.common.mq.core;

/**
 * 消息发送器接口
 * 定义了发送消息的统一接口，不同MQ实现需实现此接口
 */
public interface MessageSender {
    /**
     * 发送消息到指定主题
     *
     * @param topic 主题名称
     * @param payload 消息内容，将被序列化为JSON
     * @return 发送是否成功
     */
    boolean send(String topic, Object payload);

    /**
     * 发送消息到指定主题，带路由键
     *
     * @param topic 主题名称
     * @param routingKey 路由键或分区键
     * @param payload 消息内容，将被序列化为JSON
     * @return 发送是否成功
     */
    boolean send(String topic, String routingKey, Object payload);
}
