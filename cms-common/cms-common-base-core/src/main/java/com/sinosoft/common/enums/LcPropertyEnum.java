package com.sinosoft.common.enums;

/**
 * 流程属性枚举
 */
public enum LcPropertyEnum {

    /**
     * 流程属性定义
     */
    NOTIFY_EXISTS("NOTIFY_EXISTS", "存在消息通知"),
    NOTIFY_MENU_PERMISSION("NOTIFY_MENU_PERMISSION", "菜单权限编码");

    private String code;
    private String name;

    LcPropertyEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }
}
