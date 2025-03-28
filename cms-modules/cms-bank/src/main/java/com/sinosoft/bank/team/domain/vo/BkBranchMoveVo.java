package com.sinosoft.bank.team.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import com.sinosoft.common.schema.team.domain.Labranchchange;

import java.util.Date;

/**
 * @program: cms6
 * @description: 个险人员异动列表展示
 * @author: zzf
 * @create: 2023-11-23 16:27
 */
@Data
@AutoMapper(target = Labranchchange.class)
public class BkBranchMoveVo {
    /**
     * 主键
     */
    private Long id;
    /**
     * 渠道
     */
    private String branchtype;
    /**
     * 旧管理机构
     */
    private String oldmanagecom;
    /**
     * 旧管理机构名称
     */
    private String oldmanagecomname;
    /**
     * 新管理机构
     */
    private String newmanagecom;
    /**
     * 新管理机构
     */
    private String newmanagecomname;
    /**
     * 旧销售机构
     */
    private String oldagentgroup;
    /**
     * 旧销售机构外部代码
     */
    private String oldbranchattr;
    /**
     * 旧销售机构名称
     */
    private String oldbranchname;
    /**
     * 新销售机构
     */
    private String newagentgroup;
    /**
     * 新销售机构外部代码
     */
    private String newbranchattr;
    /**
     * 新销售机构名称
     */
    private String newbranchname;
    /**
     * 调整日期
     */
    private Date manoeuvredate;
    /**
     * 生效日期
     */
    private Date effectdate;
    /**
     *
     */
    private String remark;
}
