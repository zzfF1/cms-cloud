package com.sinosoft.common.domain.bo;

import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import com.sinosoft.common.schema.common.domain.BaseLawVersion;

/**
 * 基本法版本业务对象 base_law_version
 *
 * @author zzf
 * @date 2023-10-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BaseLawVersion.class, reverseConvertGenerate = false)
public class BaseLawVersionBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Integer id;

    /**
     * 渠道
     */
    @NotBlank(message = "渠道不能为空", groups = { AddGroup.class, EditGroup.class })
    private String branchType;

    /**
     * 佣金类型
     */
    @NotBlank(message = "佣金类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String indexCalType;

    /**
     * 启用状态
     */
    @NotNull(message = "启用状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer status;

    /**
     * 计算人员实现类
     */
    @NotBlank(message = "计算人员实现类不能为空", groups = { AddGroup.class, EditGroup.class })
    private String calculateClass;

    /**
     * 计算完成处理队列
     */
    @NotBlank(message = "计算完成处理队列不能为空", groups = { AddGroup.class, EditGroup.class })
    private String handleQueue;


}
