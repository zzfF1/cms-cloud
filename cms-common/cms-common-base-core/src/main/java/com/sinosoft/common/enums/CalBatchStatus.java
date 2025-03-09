package com.sinosoft.common.enums;

/**
 * 运行状态
 */
public enum CalBatchStatus {
    INIT("初始化"),
    RUNNING("运行中"),
    COMPLETED("已完成"),
    FAILED("失败");

    private final String description;

    CalBatchStatus(String description) {
        this.description = description;
    }

    public String getCode() {
        return this.name();
    }

    public String getDescription() {
        return this.description;
    }
}
