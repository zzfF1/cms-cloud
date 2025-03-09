package com.sinosoft.notice.core.enums;

/**
 * 通知发送状态枚举
 */
public enum DeliveryStatus {
    /**
     * 待发送
     */
    PENDING("pending", "待发送"),

    /**
     * 已发送
     */
    SENT("sent", "已发送"),

    /**
     * 发送失败
     */
    FAILED("failed", "发送失败");

    private final String code;
    private final String description;

    DeliveryStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static DeliveryStatus fromCode(String code) {
        for (DeliveryStatus status : DeliveryStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return PENDING; // 默认待发送
    }
}
