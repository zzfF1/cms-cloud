package com.sinosoft.common.enums;

/**
 * 数据同步枚举
 */
public enum SyncDataEnum {

    /**
     * 人员信息
     */
    AGENT("laagent", "代理人"),
    BRANCH("labranchgroup", "销售机构");

    private String table;
    private String remark;

    SyncDataEnum(String table, String remark) {
        this.table = table;
        this.remark = remark;
    }
}
