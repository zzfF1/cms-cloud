package com.sinosoft.common.mq.core;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 统一消息模型
 */
@Setter
@Getter
public class MqMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // Getters and Setters
    // 消息ID
    private String messageId;

    // 主题
    private String topic;

    // 标签/路由键
    private String tag;

    // 消息体
    private Object payload;

    // 消息头部属性
    private Map<String, Object> headers = new HashMap<>();

    // 创建时间
    private long timestamp;

    public MqMessage() {
        this.messageId = UUID.randomUUID().toString().replace("-", "");
        this.timestamp = System.currentTimeMillis();
    }

    public MqMessage(String topic, Object payload) {
        this();
        this.topic = topic;
        this.payload = payload;
    }

    public MqMessage(String topic, String tag, Object payload) {
        this(topic, payload);
        this.tag = tag;
    }

    public void addHeader(String key, Object value) {
        headers.put(key, value);
    }

    public Object getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public String toString() {
        return "MqMessage{" +
            "messageId='" + messageId + '\'' +
            ", topic='" + topic + '\'' +
            ", tag='" + tag + '\'' +
            ", payload=" + payload +
            ", headers=" + headers +
            ", timestamp=" + timestamp +
            '}';
    }
}
