package com.sinosoft.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import com.sinosoft.common.schema.common.domain.SysPageConfigTab;

import java.math.BigDecimal;

/**
 * 界面展示表格业务对象 sys_page_config_tab
 *
 * @author zzf
 * @date 2024-07-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysPageConfigTab.class, reverseConvertGenerate = false)
public class SysPageConfigTabBo extends BaseEntity {

    /**
     * 主键
     */
    private Long id;

    /**
     * 配置id
     */
    private Long pageId;

    /**
     * 标题
     */
    private String label;

    /**
     * 字段名称
     */
    private String prop;

    /**
     * 对齐方式
     */
    private String align;

    /**
     * 列宽
     */
    private String width;

    /**
     * 固定方式
     */
    private String fixed;

    /**
     * 列的类型
     */
    private String type;

    /**
     * 插槽
     */
    private String slot;

    /**
     * 格式化字段
     */
    private String code;

    /**
     * 格式化名称
     */
    private String name;

    /**
     * 自定义格式化方法
     */
    private String formatter;

    /**
     * 显示提示
     */
    private String tooltip;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal sort;

    /**
     * 说明
     */
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
