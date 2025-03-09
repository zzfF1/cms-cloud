package com.sinosoft.common.excel.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: cms6
 * @description:
 * @author: zzf
 * @create: 2023-11-30 06:50
 */
@Data
@Slf4j
public class ExcelSelectedResolve {
    /**
     * 数据来源代码
     */
    private String sourceCode;
    /**
     * 下拉内容
     */
    private String[] source;

    /**
     * 设置下拉框的结束行，默认为最后一行
     */
    private int lastRow;
}
