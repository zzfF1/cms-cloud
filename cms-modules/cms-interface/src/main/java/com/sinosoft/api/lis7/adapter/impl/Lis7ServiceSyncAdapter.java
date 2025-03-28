package com.sinosoft.api.lis7.adapter.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.schema.agent.domain.Laagent;
import com.sinosoft.common.schema.agent.domain.Latree;
import com.sinosoft.common.schema.team.domain.Labranchgroup;
import com.sinosoft.common.sync.adapter.ServiceSyncAdapter;
import com.sinosoft.integration.api.core.Lis7HttpResponse;
import com.sinosoft.integration.api.lis7.RemoteLis7Service;
import com.sinosoft.integration.api.lis7.model.SyncBaseAgentDto;
import com.sinosoft.integration.api.lis7.model.SyncBaseBranchDto;
import com.sinosoft.integration.api.lis7.model.SyncBaseTreeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Lis7ServiceSyncAdapter implements ServiceSyncAdapter {

    private final RemoteLis7Service remoteLis7Service;
    private final ObjectMapper objectMapper;

    @Override
    public SyncResult syncAgentData(List<?> agentList, String businessCode) {
        try {
            // 转换泛型列表
            List<Laagent> typedList = agentList.stream().map(obj -> (Laagent) obj).collect(Collectors.toList());
            // 构建请求DTO
            SyncBaseAgentDto agentDto = SyncBaseAgentDto.builder().lAAgents(typedList).build();
            // 调用Dubbo服务
            Lis7HttpResponse response = remoteLis7Service.syncAgent(agentDto);
            // 返回结果
            String resultData = null;
            try {
                resultData = objectMapper.writeValueAsString(response);
            } catch (Exception e) {
                log.warn("序列化响应失败", e);
            }
            return new SyncResult(response != null && response.isSuccess(), response != null ? response.getMessage() : "No response", resultData);
        } catch (Exception e) {
            log.error("同步人员基础信息时发生异常", e);
            return new SyncResult(false, e.getMessage());
        }
    }

    @Override
    public SyncResult syncTreeData(List<?> treeList, String businessCode) {
        try {
            // 转换泛型列表
            List<Latree> typedList = treeList.stream().map(obj -> (Latree) obj).collect(Collectors.toList());
            // 构建请求DTO
            SyncBaseTreeDto treeDto = SyncBaseTreeDto.builder().lATrees(typedList).build();
            // 调用Dubbo服务
            Lis7HttpResponse response = remoteLis7Service.syncTree(treeDto);
            // 返回结果
            String resultData = null;
            try {
                resultData = objectMapper.writeValueAsString(response);
            } catch (Exception e) {
                log.warn("序列化响应失败", e);
            }
            return new SyncResult(response != null && response.isSuccess(), response != null ? response.getMessage() : "No response", resultData);
        } catch (Exception e) {
            log.error("同步人员职级信息时发生异常", e);
            return new SyncResult(false, e.getMessage());
        }
    }

    @Override
    public SyncResult syncBranchData(List<?> branchList, String businessCode) {
        try {
            // 转换泛型列表
            List<Labranchgroup> typedList = branchList.stream().map(obj -> (Labranchgroup) obj).collect(Collectors.toList());
            // 构建请求DTO
            SyncBaseBranchDto branchDto = SyncBaseBranchDto.builder().lABranchGroups(typedList).build();
            // 调用Dubbo服务
            Lis7HttpResponse response = remoteLis7Service.syncBranch(branchDto);
            // 返回结果
            String resultData = null;
            try {
                resultData = objectMapper.writeValueAsString(response);
            } catch (Exception e) {
                log.warn("序列化响应失败", e);
            }
            return new SyncResult(response != null && response.isSuccess(), response != null ? response.getMessage() : "No response", resultData);
        } catch (Exception e) {
            log.error("同步人员职级信息时发生异常", e);
            return new SyncResult(false, e.getMessage());
        }
    }
}
