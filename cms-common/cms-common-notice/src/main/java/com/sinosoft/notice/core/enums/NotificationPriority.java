package com.sinosoft.notice.core.enums;

/**
 * 通知优先级枚举
 */
public enum NotificationPriority {
    /**
     * 高优先级
     */
    HIGH("high", "高"),

    /**
     * 中优先级
     */
    MEDIUM("medium", "中"),

    /**
     * 低优先级
     */
    LOW("low", "低");

    private final String code;
    private final String description;

    NotificationPriority(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static NotificationPriority fromCode(String code) {
        for (NotificationPriority priority : NotificationPriority.values()) {
            if (priority.getCode().equals(code)) {
                return priority;
            }
        }
        return MEDIUM; // 默认中优先级
    }
}
