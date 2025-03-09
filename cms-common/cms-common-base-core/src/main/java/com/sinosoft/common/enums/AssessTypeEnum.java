package com.sinosoft.common.enums;

public enum AssessTypeEnum {

    /**
     * 晋升
     */
    TYPE01("01", "晋升"),
    /**
     * 维持
     */
    TYPE02("02", "维持"),
    /**
     * 降级
     */
    TYPE03("03", "降级"),
    /**
     * 清退
     */
    TYPE04("04", "清退"),
    /**
     * 离职
     */
    TYPE05("05", "离职"),
    UNKNOWN("", "无");

    private String code;
    private String name;

    AssessTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    /**
     * @param assessType
     * @return
     */
    public static AssessTypeEnum getEnumType(String assessType) {
        AssessTypeEnum[] enums = AssessTypeEnum.values();
        for (AssessTypeEnum um : enums) {
            if (um.getCode().equals(assessType)) {
                return um;
            }
        }
        return AssessTypeEnum.UNKNOWN;
    }
}
