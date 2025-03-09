package com.sinosoft.common.enums;

/**
 * 费率条件枚举
 */
public enum RateConditionEnum {

    //条件
    EQ("=", "等于"),
    GT(">", "大于"),
    GE(">=", "大于等于"),
    LT("<", "小于"),
    LE("<=", "小于等于"),
    LIKE("like", "模糊匹配"),
    LIKE_LEFT("%like", "模糊左匹配"),
    LIKE_RIGHT("like%", "模糊右匹配"),
    UNKNOWN("", "无条件");
    /**
     * 编码
     */
    private String code;
    /**
     * 说明
     */
    private String remark;

    RateConditionEnum(String code, String remark) {
        this.code = code;
        this.remark = remark;
    }

    public String getCode() {
        return this.code;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 类型
     * @return 枚举
     */
    public static RateConditionEnum getEnum(String code) {
        for (RateConditionEnum rateConditionEnum : RateConditionEnum.values()) {
            if (rateConditionEnum.getCode().equals(code)) {
                return rateConditionEnum;
            }
        }
        return RateConditionEnum.UNKNOWN;
    }
}
