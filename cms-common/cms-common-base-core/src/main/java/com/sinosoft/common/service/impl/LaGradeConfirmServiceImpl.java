package com.sinosoft.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.service.ILaGradeConfirmService;
import com.sinosoft.common.schema.agent.domain.Laagent;
import com.sinosoft.common.schema.agent.domain.Laagentgrade;
import com.sinosoft.common.schema.agent.domain.Latree;
import com.sinosoft.common.schema.agent.mapper.LaagentMapper;
import com.sinosoft.common.schema.agent.mapper.LaagentgradeMapper;
import com.sinosoft.common.schema.agent.mapper.LatreeMapper;
import com.sinosoft.common.schema.assessment.domain.LaGradeConfirmConfig;
import com.sinosoft.common.schema.assessment.mapper.LaGradeConfirmConfigMapper;
import com.sinosoft.common.schema.team.domain.Labranchgroup;
import com.sinosoft.common.schema.team.domain.Labranchlevel;
import com.sinosoft.common.schema.team.mapper.LabranchgroupMapper;
import com.sinosoft.common.schema.team.mapper.LabranchlevelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: cms6
 * @description: 职级确认实现
 * @author: zzf
 * @create: 2023-11-17 10:02
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LaGradeConfirmServiceImpl implements ILaGradeConfirmService {
    private final LaGradeConfirmConfigMapper baseMapper;
    private final LaagentMapper laAgentMapper;
    private final LatreeMapper laTreeMapper;
    private final LabranchgroupMapper laBranchGroupMapper;
    private final LaagentgradeMapper laAgentGradeMapper;
    private final LabranchlevelMapper laBranchLevelMapper;

    @Override
    public List<LaGradeConfirmConfig> queryGradeConfirmConfig(String branchType, String assessWay) {
        LambdaQueryWrapper<LaGradeConfirmConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(LaGradeConfirmConfig::getBranchType, branchType);
        lqw.eq(LaGradeConfirmConfig::getAssessWay, assessWay);
        lqw.eq(LaGradeConfirmConfig::getFlag, 1);
        return baseMapper.selectList(lqw);
    }

    /**
     * 查询人员数据
     *
     * @param agentCodes 工号
     * @return 人员数据
     */
    @Override
    public List<Laagent> queryAgent(List<String> agentCodes) {
        LambdaQueryWrapper<Laagent> lqw = Wrappers.lambdaQuery();
        lqw.in(Laagent::getAgentcode, agentCodes);
        return laAgentMapper.selectList(lqw);
    }

    /**
     * 查询职级数据
     *
     * @param agentCodes 工号
     * @return 职级数据
     */
    @Override
    public List<Latree> queryTree(List<String> agentCodes) {
        LambdaQueryWrapper<Latree> lqw = Wrappers.lambdaQuery();
        lqw.in(Latree::getAgentcode, agentCodes);
        return laTreeMapper.selectList(lqw);
    }

    /**
     * 根据销售机构查询在职人员
     *
     * @param branchType 渠道
     * @param branchs    机构
     * @return 人员数据
     */
    @Override
    public List<Laagent> queryAgentByBranch(String branchType, List<String> branchs) {
        LambdaQueryWrapper<Laagent> lqw = Wrappers.lambdaQuery();
        lqw.eq(Laagent::getBranchtype, branchType);
        lqw.in(Laagent::getAgentgroup, branchs);
        lqw.lt(Laagent::getAgentstate, "04");
        return laAgentMapper.selectList(lqw);
    }

    /**
     * 查询销售机构
     *
     * @param agentGroups 机构主键
     * @return 机构数据
     */
    @Override
    public List<Labranchgroup> queryBranchGroup(List<String> agentGroups) {
        LambdaQueryWrapper<Labranchgroup> lqw = Wrappers.lambdaQuery();
        lqw.in(Labranchgroup::getAgentgroup, agentGroups);
        return laBranchGroupMapper.selectList(lqw);
    }

    /**
     * 查询销售机构(查询区下的所有未停业机构)
     *
     * @param branchType    渠道
     * @param branchseriess 机构系列
     * @return 机构数据
     */
    @Override
    public List<Labranchgroup> queryBranchByBranchSeries(String branchType, List<String> branchseriess) {
        List<Labranchgroup> branchs = new ArrayList<>();
        //循环机构系列
        for (String series : branchseriess) {
            LambdaQueryWrapper<Labranchgroup> lqw = Wrappers.lambdaQuery();
            lqw.eq(Labranchgroup::getBranchtype, branchType);
            lqw.likeRight(Labranchgroup::getBranchseries, series);
            branchs.addAll(laBranchGroupMapper.selectList(lqw));
        }
        return branchs;
    }

    /**
     * 查询职级对象
     *
     * @param gradeCodes 职级编码
     * @return 职级对象
     */
    @Override
    public List<Laagentgrade> queryAgentGrade(List<String> gradeCodes) {
        LambdaQueryWrapper<Laagentgrade> lqw = Wrappers.lambdaQuery();
        lqw.in(Laagentgrade::getGradecode, gradeCodes);
        return laAgentGradeMapper.selectList(lqw);
    }

    /**
     * 查询机构级别
     *
     * @param branchType 渠道
     * @return 机构级别
     */
    @Override
    public List<Labranchlevel> queryBranchLevel(String branchType) {
        LambdaQueryWrapper<Labranchlevel> lqw = Wrappers.lambdaQuery();
        lqw.eq(Labranchlevel::getBranchtype, branchType);
        return laBranchLevelMapper.selectList(lqw);
    }

    /**
     * 查询最大编号
     *
     * @param branchType  渠道
     * @param branchLevel 机构级别
     * @param branchAttr  机构外部代码
     * @return 当前最大编号
     */
    @Override
    public String queryBranchMaxBh(String branchType, String branchLevel, String branchAttr) {
        return laBranchGroupMapper.getMaxBranchAttr(branchType, branchLevel, branchAttr);
    }

    /**
     * 查询区的最大编号
     *
     * @param branchType  渠道
     * @param branchLevels 机构级别
     * @return 当前最大编号
     */
    @Override
    public String queryQuBranchMaxBh(String branchType, List<String> branchLevels) {
        return laBranchGroupMapper.getQuMaxBranchAttr(branchType, branchLevels);
    }
}
