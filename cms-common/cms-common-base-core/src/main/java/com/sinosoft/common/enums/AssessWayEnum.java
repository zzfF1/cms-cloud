package com.sinosoft.common.enums;

/**
 * 考核类型
 *
 * @program: cms6
 */
public enum AssessWayEnum {

    /**
     * 考核方式
     */
    TYPE10("10", "正式考核"),
    TYPE11("11", "月度考核"),
    TYPE12("12", "预警考核"),
    TYPE30("30", "职级调整"),
    TYPE40("40", "离职"),
    TYPE45("45", "人员异动"),
    TYPE46("46", "团队异动"),
    TYPE48("48", "主管任命"),
    UNKNOWN("unknown", "没有匹配");

    private String code;
    private String name;

    AssessWayEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    /**
     * 获取考核计算方式
     *
     * @param code 类型
     * @return 没有找到返回 unknown
     */
    public static AssessWayEnum getEnumType(String code) {
        AssessWayEnum[] assessWayEnums = AssessWayEnum.values();
        for (AssessWayEnum wayEnum : assessWayEnums) {
            if (wayEnum.getCode().equals(code)) {
                return wayEnum;
            }
        }
        return AssessWayEnum.UNKNOWN;
    }
}
