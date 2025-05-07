package com.sinosoft.system.domain.bo;

import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.schema.common.domain.LcDefine;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 流程明细业务对象 lc_define
 *
 * @author zzf
 * @date 2023-11-12
 */
@Data
@AutoMapper(target = LcDefine.class, reverseConvertGenerate = false)
public class LcDefineBo {

    /**
     * 流水号
     */
    @NotNull(message = "流程主键", groups = {AddGroup.class, EditGroup.class})
    private Integer id;

    /**
     * 流程名称
     */
    @NotBlank(message = "流程名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String name;

    /**
     * 顺序号
     */
    @NotNull(message = "顺序号不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer recno;

    /**
     * 流程类型
     */
    @NotBlank(message = "流程类型不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer lcSerialNo;

    /**
     * 下一个流程节点
     */
    private Integer nextId;
}
