package com.sinosoft.bank.team.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import com.sinosoft.common.schema.team.domain.Labranchgroup;

import java.util.Date;

/**
 * @program: cms6
 * @description: 个险渠道销售机构显示
 * @author: zzf
 * @create: 2023-08-21 10:36
 */
@Data
@AutoMapper(target = Labranchgroup.class)
public class BkBranchGroupFormVo {
    /**
     * 销售机构代码
     */
    private String agentgroup;
    /**
     * 管理机构代码
     */
    private String managecom;
    /**
     * 管理机构名称
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
    private Date founddate;
    /**
     * 停业日期
     */
    private Date enddate;
    /**
     * 停业
     */
    private String endflag;
    /**
     * 展业机构地址
     */
    private String branchaddress;
    /**
     * 展业机构电话
     */
    private String branchphone;
    /**
     * 展业机构传真
     */
    private String branchfax;
    /**
     * 展业机构邮编
     */
    private String branchzipcode;
    /**
     * 机构类型
     */
    private String upbranchattr;
    /**
     * 上级机构主键
     */
    private String upbranch;
    /**
     *  上级机构名称
     */
    private String upbranchgroupname;
    /**
     * 上级机构外部代码
     */
    private String upbranchgroupattr;
}
