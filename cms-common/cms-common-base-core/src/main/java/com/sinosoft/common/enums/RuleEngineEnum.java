package com.sinosoft.common.enums;

/**
 * 规则引擎枚举
 */
public enum RuleEngineEnum {

    RULE_CAL_WAGE("12","佣金计算"),
    RULE_CAL_TAX("13","合并计税"),
    RULE_CAL_ASSESS_ZS_1("15","正式考核"),
    RULE_CAL_ASSESS_ZS_2("16","正式考核"),
    RULE_CAL_ASSESS_FIR("14","首次考核");



    private  String configId;
    private  String name;

    RuleEngineEnum(String configId, String name) {
        this.configId = configId;
        this.name = name;
    }

    public String getConfigId() {
        return this.configId;
    }
    public String getName() {
        return this.name;
    }

}
