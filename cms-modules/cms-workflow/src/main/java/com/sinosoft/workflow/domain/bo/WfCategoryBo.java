package com.sinosoft.workflow.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import com.sinosoft.workflow.domain.WfCategory;

/**
 * 流程分类业务对象 wf_category
 *
 * @author may
 * @date 2023-06-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = WfCategory.class, reverseConvertGenerate = false)
public class WfCategoryBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String categoryName;

    /**
     * 分类编码
     */
    @NotBlank(message = "分类编码不能为空", groups = {AddGroup.class, EditGroup.class})
    private String categoryCode;

    /**
     * 父级id
     */
    @NotNull(message = "父级id不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long parentId;

    /**
     * 排序
     */
    private Long sortNum;


}
