package com.sinosoft.notice.core.enums;

/**
 * 通知渠道枚举
 */
public enum NotificationChannel {
    /**
     * 系统内通知
     */
    SYSTEM("system", "系统内通知"),

    /**
     * 短信通知
     */
    SMS("sms", "短信"),

    /**
     * 邮件通知
     */
    EMAIL("email", "邮件");

    private final String code;
    private final String description;

    NotificationChannel(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static NotificationChannel fromCode(String code) {
        for (NotificationChannel channel : NotificationChannel.values()) {
            if (channel.getCode().equals(code)) {
                return channel;
            }
        }
        throw new IllegalArgumentException("Unknown notification channel code: " + code);
    }
}
