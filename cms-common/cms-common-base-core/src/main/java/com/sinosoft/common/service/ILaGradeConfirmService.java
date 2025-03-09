package com.sinosoft.common.service;

import com.sinosoft.common.schema.agent.domain.Laagent;
import com.sinosoft.common.schema.agent.domain.Laagentgrade;
import com.sinosoft.common.schema.agent.domain.Latree;
import com.sinosoft.common.schema.assessment.domain.LaGradeConfirmConfig;
import com.sinosoft.common.schema.team.domain.Labranchgroup;
import com.sinosoft.common.schema.team.domain.Labranchlevel;

import java.util.List;

/**
 * 职级确认Service接口
 */
public interface ILaGradeConfirmService {

    /**
     * 查询职级确认配置
     *
     * @param branchType 渠道
     * @param assessWay  考核方式
     * @return 配置结果
     */
    List<LaGradeConfirmConfig> queryGradeConfirmConfig(String branchType, String assessWay);

    /**
     * 查询人员数据
     *
     * @param agentCodes 工号
     * @return 人员数据
     */
    List<Laagent> queryAgent(List<String> agentCodes);

    /**
     * 查询职级数据
     *
     * @param agentCodes 工号
     * @return 职级数据
     */
    List<Latree> queryTree(List<String> agentCodes);

    /**
     * @param branchType
     * @param branchs
     * @return
     */
    List<Laagent> queryAgentByBranch(String branchType, List<String> branchs);

    /**
     * 查询销售机构
     *
     * @param agentGroups 机构主键
     * @return 机构数据
     */
    List<Labranchgroup> queryBranchGroup(List<String> agentGroups);

    /**
     * 查询销售机构(查询区下的所有未停业机构)
     *
     * @param branchType    渠道
     * @param branchseriess 机构系列
     * @return 机构数据
     */
    List<Labranchgroup> queryBranchByBranchSeries(String branchType, List<String> branchseriess);

    /**
     * 查询职级对象
     *
     * @param gradeCodes 职级编码
     * @return 职级对象
     */
    List<Laagentgrade> queryAgentGrade(List<String> gradeCodes);

    /**
     * 查询机构级别
     * @param branchType 渠道
     * @return 机构级别
     */
    List<Labranchlevel> queryBranchLevel(String branchType);

    /**
     * 获取最大编号
     * @param branchType 渠道
     * @param branchLevel 级别
     * @param branchAttr  机构外部代码
     * @return 最大编号
     */
    String queryBranchMaxBh(String branchType, String branchLevel, String branchAttr);

    /**
     * 查询区的最大编号
     *
     * @param branchType  渠道
     * @param branchLevels 机构级别
     * @return 当前最大编号
     */
    String queryQuBranchMaxBh(String branchType, List<String> branchLevels);
}
