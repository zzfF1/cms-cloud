package com.sinosoft.common.excel.core;

/**
 * Excel动态下拉框接口
 */
public interface ExcelDynamicSelect {

    /**
     * 获取动态生成的下拉框可选数据
     *
     * @return 动态生成的下拉框可选数据
     */
    String[] getSource(String parameter);
}
