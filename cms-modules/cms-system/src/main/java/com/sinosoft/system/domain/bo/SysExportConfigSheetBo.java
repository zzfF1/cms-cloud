package com.sinosoft.system.domain.bo;


import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import com.sinosoft.common.schema.common.domain.SysExportConfigSheet;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * excel导出sheet配置业务对象 sys_export_config_sheet
 *
 * @author demo
 * @date 2024-04-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysExportConfigSheet.class, reverseConvertGenerate = false)
public class SysExportConfigSheetBo extends BaseEntity {

    /**
     * 主键
     */
    private Long id;

    /**
     * 配置id
     */
    private Long configId;

    /**
     * sheet索引
     */
    private Long sheetIndex;

    /**
     * 标题名称
     */
    @NotBlank(message = "标题名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String titleName;

    /**
     * sheet名称
     */
    @NotBlank(message = "sheet名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String sheetName;

    /**
     * 起始行
     */
    private String beginRow;

    /**
     * 起始列
     */
    private String beginCol;

    /**
     * 查询sql字段
     */
    @NotBlank(message = "查询SQL字段不能为空", groups = { AddGroup.class, EditGroup.class })
    private String sqlField;

    /**
     * sql条件
     */
    @NotBlank(message = "SQL条件不能为空", groups = { AddGroup.class, EditGroup.class })
    private String sqlConditions;

    /**
     * 汇总字段
     */
    private String sqlGroup;

    /**
     * 排序字段
     */
    private String sqlOrder;

    /**
     * 字段配置列表
     */
    private List<SysExportConfigItemBo> items;
}
