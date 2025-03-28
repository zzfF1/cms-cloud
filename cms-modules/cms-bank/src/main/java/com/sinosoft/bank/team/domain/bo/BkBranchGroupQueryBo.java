package com.sinosoft.bank.team.domain.bo;

import lombok.Data;

import java.util.List;

/**
 * 销售机构管理业务对象 la_branch_group
 *
 * @author zzf
 * @date 2023-06-30
 */
@Data
public class BkBranchGroupQueryBo {
    /**
     * 管理机构
     */
    private String managecom;
    /**
     * 机构名称
     */
    private String name;
    /**
     * 主管代码
     */
    private String branchmanager;
    /**
     * 主管姓名
     */
    private String branchmanagername;
    /**
     * 停业
     */
    private String endflag;
    /**
     * 销售机构内部编码
     */
    private String agentgroup;
    /**
     * 销售机构外部编码
     */
    private String branchattr;
    /**
     * 是否有主管
     */
    private String havemanager;
    /**
     * 级别
     */
    private List<String> branchlevel;
}
