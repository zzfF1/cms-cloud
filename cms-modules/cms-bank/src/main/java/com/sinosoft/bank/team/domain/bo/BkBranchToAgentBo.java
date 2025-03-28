package com.sinosoft.bank.team.domain.bo;

import com.sinosoft.common.schema.team.domain.Labranchtoagent;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 机构与代理人关系业务对象 la_branch_to_agent
 *
 * @author zzf
 * @date 2024-06-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Labranchtoagent.class, reverseConvertGenerate = false)
public class BkBranchToAgentBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 工号
     */
    @NotBlank(message = "工号不能为空", groups = {AddGroup.class, EditGroup.class})
    private String agentCode;

    /**
     * 销售机构
     */
    @NotBlank(message = "销售机构不能为空", groups = {AddGroup.class, EditGroup.class})
    private String agentGroup;

    /**
     * 渠道
     */
    @NotBlank(message = "渠道不能为空", groups = {AddGroup.class, EditGroup.class})
    private String branchType;

    /**
     * 分配起期
     */
    @NotNull(message = "分配起期不能为空", groups = {AddGroup.class, EditGroup.class})
    private Date startDate;

    /**
     * 止期
     */
    @NotNull(message = "止期不能为空", groups = {AddGroup.class, EditGroup.class})
    private Date endDate;
}
