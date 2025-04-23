package com.sinosoft.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import com.sinosoft.common.schema.common.domain.SysPageConfigQuery;

import java.math.BigDecimal;

/**
 * 界面查询配置业务对象 sys_page_config_query
 *
 * @author zzf
 * @date 2024-07-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysPageConfigQuery.class, reverseConvertGenerate = false)
public class SysPageConfigQueryBo extends BaseEntity {

    /**
     * 主键
     */
    private Long id;

    /**
     * 配置id
     */
    private Long pageId;

    /**
     * 类型
     */
    private String fieldType;

    /**
     * 字段前缀
     */
    private String fieldPrefix;

    /**
     * 别名
     */
    private String alias;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 条件
     */
    private String condOperator;

    /**
     * 排序字段
     */
    private BigDecimal queryOrder;

    /**
     * 特殊代码
     */
    private String specialCode;

    /**
     * 说明
     */
    private String remark;

    /**
     * 类型 0:前台查询条件 1:后台追加条件 2:排序字段 3:高级查询
     */
    private String type;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 组件类型
     */
    private String componentType;

    /**
     * 字典类型编码
     */
    private String dictType;

    /**
     * 数据源类型
     */
    private String dataSource;

    /**
     * Bean
     */
    private String beanName;

    /**
     * 依赖字段
     */
    private String dependencyField;

    /**
     * 选项配置
     */
    private String optionsConfig;

    /**
     * 占位提示文字
     */
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
