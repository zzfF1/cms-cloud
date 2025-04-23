package com.sinosoft.system.domain.vo;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sinosoft.common.schema.common.domain.SysExportConfigSheet;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * excel导出sheet配置视图对象 sys_export_config_sheet
 *
 * @author demo
 * @date 2024-04-20
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysExportConfigSheet.class)
public class SysExportConfigSheetVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 配置id
     */
    @ExcelProperty(value = "配置id")
    private Long configId;

    /**
     * sheet索引
     */
    @ExcelProperty(value = "sheet索引")
    private Long sheetIndex;

    /**
     * 标题名称
     */
    @ExcelProperty(value = "标题名称")
    private String titleName;

    /**
     * sheet名称
     */
    @ExcelProperty(value = "sheet名称")
    private String sheetName;

    /**
     * 起始行
     */
    @ExcelProperty(value = "起始行")
    private String beginRow;

    /**
     * 起始列
     */
    @ExcelProperty(value = "起始列")
    private String beginCol;

    /**
     * 查询sql字段
     */
    @ExcelProperty(value = "查询sql字段")
    private String sqlField;

    /**
     * sql条件
     */
    @ExcelProperty(value = "sql条件")
    private String sqlConditions;

    /**
     * 汇总字段
     */
    @ExcelProperty(value = "汇总字段")
    private String sqlGroup;

    /**
     * 排序字段
     */
    @ExcelProperty(value = "排序字段")
    private String sqlOrder;

    /**
     * 字段配置列表
     */
    private List<SysExportConfigItemVo> items;
}
