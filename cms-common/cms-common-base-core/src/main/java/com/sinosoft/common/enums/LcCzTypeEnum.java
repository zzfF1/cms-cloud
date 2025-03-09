package com.sinosoft.common.enums;

import lombok.Getter;

/**
 * 流程操作类型枚举
 */
@Getter
public enum LcCzTypeEnum {

    SAVE(0, 0, "保存"),
    ADOPT(1, 1, "通过"),
    REJECT(-1, -1, "驳回"),
    REJECT_FIRST(-2, -1, "一键撤回,主动撤回"),
    ;

    /**
     * 代码
     */
    private final int code;
    /**
     * 操作类型
     * 0:保存 1:提交 -1:驳回
     */
    private final int operatorType;
    /**
     * 说明
     */
    private final String name;

    LcCzTypeEnum(int code, int operatorType, String name) {
        this.code = code;
        this.operatorType = operatorType;
        this.name = name;
    }
}
