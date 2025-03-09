package com.sinosoft.common.domain.bo;

import com.sinosoft.common.schema.agent.domain.Laagent;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

/**
 * @program: cms6
 * @description: 人员查询条件
 * @author: zzf
 * @create: 2023-07-06 22:54
 */
@Data
@AutoMapper(target = Laagent.class, reverseConvertGenerate = false)
public class AgentQueryBo {
    /**
     * 当前管理机构代码
     */
    private String manageCom;
    /**
     * 机构内部代码
     */
    private String agentGroup;
    /**
     * 机构外部代码
     */
    private String branchAttr;
    /**
     * 机构名称
     */
    private String branchName;
    /**
     * 渠道
     */
    private String branchType;
    /**
     * 机构级别
     */
    private String branchLevel;
    /**
     * 工号
     */
    private String agentCode;
    /**
     * 姓名
     */
    private String name;
    /**
     * 在职状态
     */
    private String agentState;
}
