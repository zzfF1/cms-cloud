package com.sinosoft.bank.team.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.sinosoft.common.translation.annotation.Translation;
import com.sinosoft.common.translation.constant.TransConstant;

import java.util.Date;

/**
 * @program: cms6
 * @description: 银保渠道销售机构显示
 * @author: zzf
 * @create: 2023-08-21 10:36
 */
@Data
public class BkBranchGroupShowVo {
    /**
     * 销售机构代码
     */
    private String agentgroup;
    /**
     * 管理机构代码
     */
    private String managecom;
    /**
     * 管理机构代码名称
     */
    private String managecomname;
    /**
     * 销售机构外部代码
     */
    private String branchattr;
    /**
     * 销售机构名称
     */
    private String name;
    /**
     * 级别
     */
    private String branchlevel;
    /**
     * 级别名称
     */
    private String branchlevelname;
    /**
     * 主管代码
     */
    private String branchmanager;
    /**
     * 主管名称
     */
    private String branchmanagername;
    /**
     * 成立日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date founddate;
    /**
     * 停业日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date enddate;
    /**
     * 停业
     */
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, other = "sys_yes_no")
    private String endflag;
    /**
     * 机构类型
     */
    private String upbranchattr;
    /**
     * 机构类型
     */
    private String upbranchattrname;
}
