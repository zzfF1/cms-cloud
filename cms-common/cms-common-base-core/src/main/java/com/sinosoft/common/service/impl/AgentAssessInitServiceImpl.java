package com.sinosoft.common.service.impl;

import com.sinosoft.common.schema.agent.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.assess.AdjustContext;
import com.sinosoft.common.assess.GraderConfirmAgent;
import com.sinosoft.common.schema.team.mapper.LabranchgroupMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: cms6
 * @description: 人员考核初始化
 * @author: zzf
 * @create: 2024-11-01 17:58
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AgentAssessInitServiceImpl {
    private final LaagentMapper laAgentMapper;
    private final LaagentbMapper laAgentBMapper;
    private final LatreeMapper laTreeMapper;
    private final LatreebMapper laTreeBMapper;
    private final LaagentgradeMapper laAgentGradeMapper;
    private final LabranchgroupMapper laBranchGroupMapper;
    private final LamanoeuvreMapper laManoeuvreMapper;


    /**
     * 初始人员考核
     *
     * @param graderConfirmAgents 调整的人员
     * @param adjustContext       调整上下文
     */
    public void initAssess(List<GraderConfirmAgent> graderConfirmAgents, AdjustContext adjustContext) {
        log.info("人员考核初始化");
        if(graderConfirmAgents.isEmpty()){

        }
    }
}
