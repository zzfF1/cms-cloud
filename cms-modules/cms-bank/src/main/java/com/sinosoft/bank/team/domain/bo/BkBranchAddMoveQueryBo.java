package com.sinosoft.bank.team.domain.bo;

import lombok.Data;

/**
 * 查询可异动的销售机构
 *
 * @author zzf
 * @date 2023-06-30
 */
@Data
public class BkBranchAddMoveQueryBo{
    /**
     * 管理机构
     */
    private String managecom;
    /**
     * 销售机构级别
     */
    private String branchlevel;
}
