package com.sinosoft.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import com.sinosoft.common.schema.common.domain.SysImportConfigItem;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * excel导入模板配置视图对象 sys_import_config_item
 *
 * @author zzf
 * @date 2024-01-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysImportConfigItem.class)
public class SysImportConfigItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 序列号
     */
    @ExcelProperty(value = "序列号")
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
     * 标题
     */
    @ExcelProperty(value = "标题")
    private String title;

    /**
     * 字段必录
     */
    @ExcelProperty(value = "字段必录")
    private Long fieldRequired;

    /**
     * 字段类型
     */
    @ExcelProperty(value = "字段类型")
    private String dataType;

    /**
     * 填写说明
     */
    @ExcelProperty(value = "填写说明")
    private String fillSm;

    /**
     * 列宽
     */
    @ExcelProperty(value = "列宽")
    private Integer width;

    /**
     * 下拉处理类
     */
    @ExcelProperty(value = "下拉处理类")
    private String downSelHandler;

    /**
     * 参数
     */
    @ExcelProperty(value = "参数")
    private String parameter;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    private BigDecimal sort;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 创建人
     */
    private String operator;

    /**
     * 创建日期
     */
    private String makedate;

    /**
     * 创建时间
     */
    private String maketime;

    /**
     * 修改人
     */
    private String modifyoperator;

    /**
     * 修改日期
     */
    private String modifydate;

    /**
     * 修改时间
     */
    private String modifytime;
}
