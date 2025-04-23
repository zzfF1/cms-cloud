package com.sinosoft.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import com.sinosoft.common.schema.common.domain.SysPageConfigQuery;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 界面查询配置视图对象 sys_page_config_query
 *
 * @author zzf
 * @date 2024-07-02
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysPageConfigQuery.class)
public class SysPageConfigQueryVo implements Serializable {

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
    private Long pageId;

    /**
     * 类型
     */
    @ExcelProperty(value = "类型")
    private String fieldType;

    /**
     * 字段前缀
     */
    @ExcelProperty(value = "字段前缀")
    private String fieldPrefix;

    /**
     * 别名
     */
    @ExcelProperty(value = "别名")
    private String alias;

    /**
     * 字段名
     */
    @ExcelProperty(value = "字段名")
    private String fieldName;

    /**
     * 条件
     */
    @ExcelProperty(value = "条件")
    private String condOperator;

    /**
     * 排序字段
     */
    @ExcelProperty(value = "排序字段")
    private BigDecimal queryOrder;

    /**
     * 特殊代码
     */
    @ExcelProperty(value = "特殊代码")
    private String specialCode;

    /**
     * 说明
     */
    @ExcelProperty(value = "说明")
    private String remark;

    /**
     * 类型 0:前台查询条件 1:后台追加条件 2:排序字段 3:高级查询
     */
    @ExcelProperty(value = "类型")
    private String type;

    /**
     * 默认值
     */
    @ExcelProperty(value = "默认值")
    private String defaultValue;

    /**
     * 组件类型
     */
    @ExcelProperty(value = "组件类型")
    private String componentType;

    /**
     * 字典类型编码
     */
    @ExcelProperty(value = "字典类型编码")
    private String dictType;

    /**
     * 数据源类型
     */
    @ExcelProperty(value = "数据源类型")
    private String dataSource;

    /**
     * Bean
     */
    @ExcelProperty(value = "Bean")
    private String beanName;

    /**
     * 依赖字段
     */
    @ExcelProperty(value = "依赖字段")
    private String dependencyField;

    /**
     * 选项配置
     */
    @ExcelProperty(value = "选项配置")
    private String optionsConfig;

    /**
     * 占位提示文字
     */
    @ExcelProperty(value = "占位提示文字")
    private String placeholder;

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
