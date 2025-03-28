package com.sinosoft.bank.team.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.sinosoft.common.core.xss.Xss;
import com.sinosoft.common.schema.team.domain.Labranchgroup;

import java.util.Date;

/**
 * @program: cms6
 * @description: 银保渠道销售机构表单
 * @author: zzf
 * @create: 2023-08-21 10:36
 */
@Data
@AutoMapper(target = Labranchgroup.class)
public class BkBranchGroupFormBo {
    /**
     * 销售机构代码
     */
    private String agentgroup;
    /**
     * 管理机构代码
     */
    @NotBlank(message = "管理机构不能为空")
    private String managecom;
    /**
     * 销售机构外部代码
     */
    private String branchattr;
    /**
     * 销售机构名称
     */
    @Xss(message = "机构名称不能包含脚本字符")
    @NotBlank(message = "机构名称不能为空")
    @Size(min = 0, max = 80, message = "机构名称长度不能超过{max}个字符")
    private String name;
    /**
     * 级别
     */
    @NotBlank(message = "机构级别不能为空")
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "成立日期不能为空")
    private Date founddate;
    /**
     * 停业日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date enddate;
    /**
     * 停业标志
     */
    @NotBlank(message = "停业标志不能为空")
    private String endflag;
    /**
     * 独立职场
     */
    @NotBlank(message = "独立职场不能为空")
    private String fieldflag;
    /**
     * 展业机构地址
     */
    @Xss(message = "机构地址不能包含脚本字符")
    @Size(min = 0, max = 80, message = "销售机构地址长度不能超过{max}个字符")
    private String branchaddress;
    /**
     * 展业机构电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "电话不合法")
    private String branchphone;
    /**
     * 展业机构邮编
     */
    private String branchzipcode;
    /**
     * 传真
     */
    private String branchfax;
    /**
     * 上级机构主键
     */
    private String upbranch;
    /**
     * 直辖标志
     */
    private String upbranchattr;
}
