package com.sinosoft.common.enums;

import java.util.ArrayList;

/**
 * wageCalculationdefinition.wagetype 对应 枚举
 */
public enum WageTypeEnum {

    /**
     * 佣金正式计算
     */
    TYPE01("0", "佣金正式计算", new ArrayList<String>() {{
        this.add("0");
    }}),
    /**
     * 佣金正式合并计税
     */
    TYPE02("1", "合并正式计税", new ArrayList<String>() {{
        this.add("1");
    }}),
    /**
     * 佣金计算与合并计税一起进行
     */
    TYPE03("2", "佣金全部计算", new ArrayList<String>() {{
        this.add("0");
        this.add("1");
    }}),
    /**
     * 佣金预提计算
     */
    TYPE04("3", "佣金删除", new ArrayList<String>() {{
        this.add("0");
        this.add("1");
    }});

    private String code;
    private String name;
    private ArrayList listArray;

    WageTypeEnum(String code, String name, ArrayList<String> array) {
        this.code = code;
        this.name = name;
        this.listArray = array;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<String> getWageTypes() {
        return this.listArray;
    }

    /**
     * 获取枚举对象
     *
     * @param code 类型
     * @return 未找到默认 TYPE01
     */
    public static WageTypeEnum getEnumType(String code) {
        WageTypeEnum[] branchLevelEnums = WageTypeEnum.values();
        //循环
        for (WageTypeEnum um : branchLevelEnums) {
            if (um.getCode().equals(code)) {
                return um;
            }
        }
        return WageTypeEnum.TYPE01;
    }
}
