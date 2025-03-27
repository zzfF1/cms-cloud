package com.sinosoft.common.sync.enums;

import lombok.Getter;

/**
 * 数据操作类型枚举
 */
@Getter
public enum OperationType {

    INSERT(1, "新增"),
    UPDATE(2, "修改"),
    DELETE(3, "删除");

    private final int code;
    private final String desc;

    OperationType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OperationType getByCode(int code) {
        for (OperationType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
