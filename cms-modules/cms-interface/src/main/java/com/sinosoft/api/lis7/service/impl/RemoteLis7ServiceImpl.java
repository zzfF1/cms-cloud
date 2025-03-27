package com.sinosoft.api.lis7.service.impl;

import cn.hutool.core.util.IdUtil;
import com.sinosoft.api.lis7.client.Lis7Client;
import com.sinosoft.common.core.domain.GlobalResponse;
import com.sinosoft.common.core.utils.DateUtils;
import com.sinosoft.integration.api.core.ClientInfo;
import com.sinosoft.integration.api.core.Lis7HttpRequest;
import com.sinosoft.integration.api.core.Lis7HttpResponse;
import com.sinosoft.integration.api.core.ResponseBaseDto;
import com.sinosoft.integration.api.lis7.RemoteLis7Service;
import com.sinosoft.integration.api.lis7.model.OrphanPolicyAssignmentDto;
import com.sinosoft.integration.api.lis7.model.OrphanPolicyAssignmentResponseDto;
import com.sinosoft.integration.api.lis7.model.SyncBaseAgentDto;
import com.sinosoft.integration.api.lis7.model.SyncBaseTreeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;


/**
 * 结算服务实现
 *
 * @author: zzf
 * @create: 2025-03-13 18:41
 */
@RequiredArgsConstructor
@Service
@DubboService
@Slf4j
public class RemoteLis7ServiceImpl implements RemoteLis7Service {

    private final Lis7Client lis7Client;

    @Override
    public OrphanPolicyAssignmentResponseDto assignOrphanPolicy(OrphanPolicyAssignmentDto grpContNos) {
        // 构建请求对象
        Lis7HttpRequest<OrphanPolicyAssignmentDto> request = buildRequest(grpContNos, "20000001");
        //调用客户端请求
        GlobalResponse<OrphanPolicyAssignmentResponseDto> response = lis7Client.assignOrphanPolicy(request);
        // 处理响应
        return processResponse(response, OrphanPolicyAssignmentResponseDto::new);
    }

    @Override
    public Lis7HttpResponse syncAgent(SyncBaseAgentDto agentDto) {
        // 构建请求对象
        Lis7HttpRequest<SyncBaseAgentDto> request = buildRequest(agentDto, "20000002");
        //调用客户端请求
        GlobalResponse<Lis7HttpResponse> response = lis7Client.syncAgent(request);
        // 处理响应
        return processResponse(response, Lis7HttpResponse::new);
    }

    @Override
    public Lis7HttpResponse syncTree(SyncBaseTreeDto treeDto) {
        // 构建请求对象
        Lis7HttpRequest<SyncBaseTreeDto> request = buildRequest(treeDto, "20000003");
        //调用客户端请求
        GlobalResponse<Lis7HttpResponse> response = lis7Client.syncTree(request);
        // 处理响应
        return processResponse(response, Lis7HttpResponse::new);
    }


}
