package com.sinosoft.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import com.sinosoft.common.schema.common.domain.SysImportConfig;

import java.util.Date;
import java.util.List;

/**
 * excel导入模板业务对象 sys_import_config
 *
 * @author zzf
 * @date 2024-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysImportConfig.class, reverseConvertGenerate = false)
public class SysImportConfigBo extends BaseEntity {

    /**
     * 序列号
     */
    @NotNull(message = "序列号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 代码
     */
    @NotBlank(message = "代码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String code;

    /**
     * 模板名称
     */
    @NotBlank(message = "模板名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 标题名称 多个sheet标题使用,分割
     */
    private String titleNames;

    /**
     * sheet名称 多个sheet标题使用,分割
     */
    private String sheetNames;

    /**
     * 渠道
     */
    @NotBlank(message = "渠道不能为空", groups = { AddGroup.class, EditGroup.class })
    private String branchType;

    /**
     * 说明
     */
    private String remark;

    /**
     * 配置项列表
     */
    private List<SysImportConfigItemBo> configItems;
}
