package com.sinosoft.api.lis7.service.impl;

import com.sinosoft.api.lis7.constants.Lis7BusinessCodes;
import com.sinosoft.api.lis7.factory.Lis7ServiceFactory;
import com.sinosoft.integration.api.core.Lis7HttpResponse;
import com.sinosoft.integration.api.lis7.RemoteLis7Service;
import com.sinosoft.integration.api.lis7.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * LIS7服务实现
 */
@RequiredArgsConstructor
@Service
@DubboService
@Slf4j
public class RemoteLis7ServiceImpl implements RemoteLis7Service {

    private final Lis7ServiceFactory lis7ServiceFactory;

    @Override
    public OrphanPolicyAssignmentResponseDto assignOrphanPolicy(OrphanPolicyAssignmentDto grpContNos) {
        log.debug("处理孤儿单分配请求");
        return lis7ServiceFactory.sendRequest(grpContNos, Lis7BusinessCodes.ORPHAN_POLICY_ASSIGNMENT);
    }

    @Override
    public Lis7HttpResponse syncAgent(SyncBaseAgentDto agentDto) {
        log.debug("处理人员基础信息同步请求");
        return lis7ServiceFactory.sendRequest(agentDto, Lis7BusinessCodes.SYNC_AGENT);
    }

    @Override
    public Lis7HttpResponse syncTree(SyncBaseTreeDto treeDto) {
        log.debug("处理人员职级信息同步请求");
        return lis7ServiceFactory.sendRequest(treeDto, Lis7BusinessCodes.SYNC_TREE);
    }

    @Override
    public Lis7HttpResponse syncBranch(SyncBaseBranchDto branchDto) {
        log.debug("处理人员销售机构信息同步请求");
        return lis7ServiceFactory.sendRequest(branchDto, Lis7BusinessCodes.SYNC_BRANCH);
    }
}
