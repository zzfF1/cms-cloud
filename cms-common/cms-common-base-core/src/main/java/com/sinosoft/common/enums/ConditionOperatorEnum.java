package com.sinosoft.common.enums;

/**
 * 查询条件枚举
 */
public enum ConditionOperatorEnum {
    EQ,     // 等于 =
    NE,     // 不等于 <>
    GT,     // 大于 >
    GE,     // 大于等于 >=
    LT,     // 小于 <
    LE,     // 小于等于 <=
    LIKE,   // LIKE '%abc%'
    NOTLIKE, // NOT LIKE '%abc%'
    IN     // IN (a, b, c)
}
