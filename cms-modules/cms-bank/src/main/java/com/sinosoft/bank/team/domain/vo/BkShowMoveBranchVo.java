package com.sinosoft.bank.team.domain.vo;

import lombok.Data;

/**
 * 异动销售机构异动视图
 *
 * @program: cms6
 * @author: zzf
 * @create: 2023-11-26 12:37
 */
@Data
public class BkShowMoveBranchVo {
    /**
     * 管理机构
     */
    private String managecom;
    /**
     * 管理机构名称
     */
    private String managecomname;
    /**
     * 销售机构
     */
    private String agentgroup;
    /**
     * 外部机构代码
     */
    private String branchattr;
    /**
     * 销售机构名称
     */
    private String branchname;
    /**
     * 主管
     */
    private String branchmanager;
    /**
     * 主管名称
     */
    private String branchmanagername;
    /**
     * 主管职级
     */
    private String branchmanagergrade;
    /**
     * 辖下人员数
     */
    private Long agentCount;
}
