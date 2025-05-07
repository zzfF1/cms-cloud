package com.sinosoft.system.domain.vo;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sinosoft.common.schema.common.domain.SysExportConfigItem;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * excel导出配置明细视图对象 sys_export_config_item
 *
 * @author demo
 * @date 2024-04-20
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysExportConfigItem.class)
public class SysExportConfigItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * sheet索引
     */
    @ExcelProperty(value = "sheet索引")
    private Long sheetId;

    /**
     * 配置id
     */
    @ExcelProperty(value = "配置id")
    private Long configId;

    /**
     * 字段
     */
    @ExcelProperty(value = "字段")
    private String field;

    /**
     * 名称
     */
    @ExcelProperty(value = "名称")
    private String name;

    /**
     * 字段类型 0-字符串,1-数字(整形),2-数字(2位),3-数字(4位),4-日期,9-序号
     */
    @ExcelProperty(value = "字段类型")
    private Integer type;

    /**
     * 显示长度
     */
    @ExcelProperty(value = "显示长度")
    private Integer dispLength;

    /**
     * 格式
     */
    @ExcelProperty(value = "格式")
    private String format;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    private BigDecimal sort;
}
