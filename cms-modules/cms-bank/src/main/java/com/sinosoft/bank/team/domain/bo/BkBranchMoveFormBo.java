package com.sinosoft.bank.team.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sinosoft.common.schema.team.domain.Labranchchange;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @program: cms6
 * @description: 团队异动表单
 * @author: zzf
 * @create: 2023-12-04 13:56
 */
@Data
@AutoMapper(target = Labranchchange.class)
public class BkBranchMoveFormBo {
    /**
     * 主键
     */
    private Long id;
    /**
     * 要异动机构
     */
    private List<String> agentgroups;
    /**
     * 异动后机构名称
     */
    private List<String> newbranchnames;
    /**
     * 旧管理机构
     */
    private String oldmanagecom;
    /**
     * 新管理机构
     */
    private String newmanagecom;
    /**
     * 旧销售机构
     */
    private String oldagentgroup;
    /**
     * 旧销售机构外部代码
     */
    private String oldagentgroupbranchattr;
    /**
     * 新销售机构
     */
    private String newagentgroup;
    /**
     * 新销售机构外部代码
     */
    private String newagentgroupbranchattr;
    /**
     * 调整日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date manoeuvredate;
    /**
     * 生效日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date effectdate;
    /**
     *
     */
    private String reason;
}
