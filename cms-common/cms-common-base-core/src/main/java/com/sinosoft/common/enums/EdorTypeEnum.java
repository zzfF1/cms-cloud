package com.sinosoft.common.enums;

public enum EdorTypeEnum {
    /**
     * 备份数据类型
     */
    ED01("01", "考核归属"),
    ED02("02", "团队调整"),
    ED03("03", "人员调动"),
    ED04("04", "职级调整"),
    ED05("05", "推荐关系调整"),
    ED06("06", "育成关系调整"),
    ED07("07", "机构主管任命"),
    ED08("08", "离职调整"),
    ED09("09", "离职恢复"),
    ED10("10", "血缘关系调整（推荐、育成关系同时变动）"),
    ED11("11", "增员"),
    ED12("12", "联动调整"),
    ED13("13", "退票"),
    ED14("14", "账户错误"),
    ED15("15", "财务付款查询"),
    ED16("16", "手续费删除"),
    ED17("17", "薪资删除"),
    ED18("18", "绩效调整"),
    ED19("19", "绩效核发"),
    ED20("20", "绩效回退"),
    ED21("21", "绩效删除"),
    ED30("30", "数据修改操作"),
    ED60("60", "数据删除操作"),
    ED99("99", "其他");

    /**
     * 编码
     */
    private String code;
    /**
     * 说明
     */
    private String remark;

    EdorTypeEnum(String code, String remark) {
        this.code = code;
        this.remark = remark;
    }

    public String getCode() {
        return this.code;
    }
}
