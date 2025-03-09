package com.sinosoft.common.domain.bo;

import com.sinosoft.common.schema.team.domain.Labranchgroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

/**
 * @program: cms6
 * @description: 基础销售机构查询请求
 * @author: zzf
 * @create: 2023-07-06 22:54
 */
@Data
@AutoMapper(target = Labranchgroup.class, reverseConvertGenerate = false)
public class BranchGroupQueryBo {
    /**
     * 当前管理机构代码
     */
    private String managecom;
    /**
     * 销售机构内部代码
     */
    private String agentgroup;
    /**
     * 销售机构外部代码
     */
    private String branchattr;
    /**
     * 销售机构名称
     */
    private String name;
    /**
     * 渠道
     */
    private String branchtype;
    /**
     * 机构级别 使用,号分割
     */
    private String branchlevels;
    /**
     * 主管代码
     */
    private String branchmanager;
    /**
     * 主管姓名
     */
    private String branchmanagername;
    /**
     * 是否停业
     */
    private String endflag;
    /**
     * 只显示有代理人的机构
     */
    private String showhaveagent;
}
