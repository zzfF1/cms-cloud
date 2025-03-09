package com.sinosoft.common.enums;

/**
 * 佣金计算类型
 */
public enum IndexcalTypeEnum {

    /**
     *
     */
    TYPE01("01", "佣金"),
    TYPE02("02", "佣金-预计算"),
    TYPE05("05", "考核预警"),
    TYPE20("20", "正式手续费计算"),
    TYPE21("21", "预提手续费计算"),

    TYPE90("90", "金牌会员计算");


    private String code;
    private String name;

    IndexcalTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    /**
     * 获取佣金类型
     *
     * @param indexCalType
     * @return 未找到默认 TYPE01
     */
    public static IndexcalTypeEnum getEnumType(String indexCalType) {
        IndexcalTypeEnum[] enums = IndexcalTypeEnum.values();
        for (IndexcalTypeEnum um : enums) {
            if (um.getCode().equals(indexCalType)) {
                return um;
            }
        }
        return IndexcalTypeEnum.TYPE01;
    }
}
