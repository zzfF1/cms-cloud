package com.sinosoft.common.sync.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.sync.adapter.ServiceSyncAdapter;
import com.sinosoft.common.sync.config.SyncConfig;
import com.sinosoft.common.sync.domain.SyncRecord;
import com.sinosoft.common.sync.event.DataSyncEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Dubbo同步策略实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DubboSyncStrategy implements SyncStrategy {

    private final ServiceSyncAdapter serviceSyncAdapter;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(SyncConfig.TargetSystemConfig targetConfig) {
        return "DUBBO".equalsIgnoreCase(targetConfig.getSyncMode());
    }

    @Override
    public void syncData(Map<String, Object> syncMessage, SyncConfig.TargetSystemConfig targetConfig,
                         DataSyncEvent event, String primaryKeyValue, List<SyncRecord> syncRecords) {
        try {
            // 获取实体数据
            Object data = syncMessage.get("data");
            String tableName = event.getTableName();

            ServiceSyncAdapter.SyncResult result = null;

            // 根据表名判断要调用的服务方法
            if ("laagent".equalsIgnoreCase(tableName)) {
                // 处理人员基础信息同步
                List<?> agentList = data instanceof List ? (List<?>) data : Collections.singletonList(data);
                result = serviceSyncAdapter.syncAgentData(agentList, event.getBusinessCode());

            } else if ("latree".equalsIgnoreCase(tableName)) {
                // 处理人员职级信息同步
                List<?> treeList = data instanceof List ? (List<?>) data : Collections.singletonList(data);
                result = serviceSyncAdapter.syncTreeData(treeList, event.getBusinessCode());

            } else {
                throw new UnsupportedOperationException("不支持同步的表类型: " + tableName);
            }

            // 记录同步结果
            SyncRecord record = SyncRecord.builder()
                .tableName(event.getTableName())
                .primaryKeyValue(primaryKeyValue)
                .operationType(event.getOperationType().getCode())
                .targetSystem(targetConfig.getSystemCode())
                .targetSystemName(targetConfig.getSystemName())
                .syncMode("DUBBO")
                .apiUrl(targetConfig.getServiceId())
                .apiResponse(result != null ? result.getResultData() : null)
                .status(result != null && result.isSuccess() ? 1 : 0) // 1-成功，0-失败
                .failReason(result != null && !result.isSuccess() ? result.getMessage() : null)
                .syncTime(new Date())
                .operator(event.getLoginUser() != null ? event.getLoginUser().getUsername() : "system")
                .businessCode(event.getBusinessCode())
                .remark(event.getRemark())
                .build();

            syncRecords.add(record);

            if (result == null || !result.isSuccess()) {
                log.error("通过Dubbo服务同步表 {} 的数据 {} 到系统 {} 失败: {}",
                    event.getTableName(), primaryKeyValue, targetConfig.getSystemName(),
                    result != null ? result.getMessage() : "无返回结果");
            } else {
                log.debug("成功通过Dubbo服务同步表 {} 的数据 {} 到系统 {}",
                    event.getTableName(), primaryKeyValue, targetConfig.getSystemName());
            }

        } catch (Exception e) {
            log.error("通过Dubbo服务同步表 {} 的数据 {} 到系统 {} 失败",
                event.getTableName(), primaryKeyValue, targetConfig.getSystemName(), e);

            // 记录错误
            SyncRecord record = createFailureRecord(event, primaryKeyValue, targetConfig, e.getMessage());
            syncRecords.add(record);
        }
    }

    /**
     * 创建失败记录
     */
    private SyncRecord createFailureRecord(DataSyncEvent event, String primaryKeyValue,
                                           SyncConfig.TargetSystemConfig targetConfig, String failReason) {
        return SyncRecord.builder()
            .tableName(event.getTableName())
            .primaryKeyValue(primaryKeyValue)
            .operationType(event.getOperationType().getCode())
            .targetSystem(targetConfig.getSystemCode())
            .targetSystemName(targetConfig.getSystemName())
            .syncMode("DUBBO")
            .apiUrl(targetConfig.getServiceId())
            .status(0) // 0-发送失败
            .syncTime(new Date())
            .failReason(failReason)
            .operator(event.getLoginUser() != null ? event.getLoginUser().getUsername() : "system")
            .businessCode(event.getBusinessCode())
            .remark(event.getRemark())
            .build();
    }
}
