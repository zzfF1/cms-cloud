package com.sinosoft.notice.core.enums;

/**
 * 通知类型枚举
 */
public enum NotificationType {
    /**
     * 待办通知
     */
    TODO("todo", "待办"),

    /**
     * 消息通知
     */
    MESSAGE("message", "消息"),

    /**
     * 公告通知
     */
    ANNOUNCEMENT("announcement", "公告");

    private final String code;
    private final String description;

    NotificationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取通知类型枚举
     */
    public static NotificationType fromCode(String code) {
        for (NotificationType type : NotificationType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown notification type code: " + code);
    }
}
