package com.sinosoft.common.mq.core;

/**
 * MQ类型枚举
 */
public enum MqType {

    KAFKA("kafka"),
    RABBIT_MQ("rabbit"),
    ROCKET_MQ("rocket");

    private final String type;

    MqType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static MqType fromString(String type) {
        for (MqType mqType : MqType.values()) {
            if (mqType.getType().equalsIgnoreCase(type)) {
                return mqType;
            }
        }
        throw new IllegalArgumentException("Unknown MQ type: " + type);
    }
}
