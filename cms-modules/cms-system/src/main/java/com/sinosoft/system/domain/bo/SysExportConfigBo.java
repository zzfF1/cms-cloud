package com.sinosoft.system.domain.bo;

import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import com.sinosoft.common.schema.common.domain.SysExportConfig;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * excel导出配置业务对象 sys_export_config
 *
 * @author demo
 * @date 2024-04-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysExportConfig.class, reverseConvertGenerate = false)
public class SysExportConfigBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 代码
     */
    @NotBlank(message = "代码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String code;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 路径
     */
    private String path;

    /**
     * 类型 0-生成方式 1-模板方式
     */
    @NotBlank(message = "类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String type;

    /**
     * 渠道
     */
    @NotBlank(message = "渠道不能为空", groups = { AddGroup.class, EditGroup.class })
    private String branchType;

    /**
     * Sheet配置列表
     */
    private List<SysExportConfigSheetBo> sheets;
}
