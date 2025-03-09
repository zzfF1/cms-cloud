package com.sinosoft.notice.core.enums;

/**
 * 通知状态枚举
 */
public enum NotificationStatus {
    /**
     * 正常
     */
    NORMAL("0", "正常"),

    /**
     * 过期
     */
    EXPIRED("1", "过期"),

    /**
     * 取消
     */
    CANCELLED("2", "取消");

    private final String code;
    private final String description;

    NotificationStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static NotificationStatus fromCode(String code) {
        for (NotificationStatus status : NotificationStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return NORMAL; // 默认正常状态
    }
}
