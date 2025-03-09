package com.sinosoft.notice.core.enums;

/**
 * 通知合并策略枚举
 */
public enum MergeStrategy {
    /**
     * 不合并
     */
    NONE("none", "不合并"),

    /**
     * 更新已有通知
     */
    UPDATE("update", "更新已有"),

    /**
     * 计数合并
     */
    COUNT("count", "计数合并"),

    /**
     * 列表合并
     */
    LIST("list", "列表合并");

    private final String code;
    private final String description;

    MergeStrategy(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MergeStrategy fromCode(String code) {
        for (MergeStrategy strategy : MergeStrategy.values()) {
            if (strategy.getCode().equals(code)) {
                return strategy;
            }
        }
        return NONE; // 默认不合并
    }
}
