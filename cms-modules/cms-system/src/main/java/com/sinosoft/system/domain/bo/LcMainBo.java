package com.sinosoft.system.domain.bo;

import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.schema.common.domain.LcMain;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 流程定义业务对象 lc_main
 *
 * @author zzf
 * @date 2023-11-12
 */
@Data
@AutoMapper(target = LcMain.class, reverseConvertGenerate = false)
public class LcMainBo{

    /**
     * 流水号
     */
    @NotNull(message = "流水号不能为空", groups = { EditGroup.class })
    private Integer serialno;

    /**
     * 流程类型名称
     */
    @NotBlank(message = "流程类型名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 顺序号
     */
    @NotNull(message = "顺序号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer recno;

    /**
     * 业务表
     */
    @NotBlank(message = "业务表不能为空", groups = { AddGroup.class, EditGroup.class })
    private String lcTable;

    /**
     * 流程字段
     */
    @NotBlank(message = "流程字段不能为空", groups = { AddGroup.class, EditGroup.class })
    private String lcField;

    /**
     * 业务表主键
     */
    @NotBlank(message = "业务表主键不能为空", groups = { AddGroup.class, EditGroup.class })
    private String idField;

    /**
     * 业务表是不是多主键 1多主键 0单主键
     */
    @NotNull(message = "业务表是不是多主键 1多主键 0单主键不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer mulkey;


}
