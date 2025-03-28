package com.sinosoft.bank.team.domain.vo;

import com.sinosoft.common.schema.team.domain.Labranchtoagent;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 机构与代理人关系视图对象 la_branch_to_agent
 *
 * @author zzf
 * @date 2024-06-04
 */
@Data
@AutoMapper(target = Labranchtoagent.class, reverseConvertGenerate = false)
public class BkBranchToAgentVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 管理机构
     */
    private String managecom;
    /**
     * 管理机构名称
     */
    private String managecomname;
    /**
     * 工号
     */
    private String agentcode;
    /**
     * 姓名
     */
    private String agentcodename;
    /**
     * 销售机构
     */
    private String agentgroup;
    /**
     * 销售机构
     */
    private String branchattr;
    /**
     * 销售机构
     */
    private String branchname;
    /**
     * 渠道
     */
    private String branchtype;
    /**
     * 分配起期
     */
    private Date startdate;
    /**
     * 止期
     */
    private Date enddate;
}
