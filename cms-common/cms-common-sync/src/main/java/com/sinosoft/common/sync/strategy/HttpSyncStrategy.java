package com.sinosoft.common.sync.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.core.domain.GlobalResponse;
import com.sinosoft.common.sync.client.Lis7SyncClient;
import com.sinosoft.common.sync.config.SyncConfig;
import com.sinosoft.common.sync.domain.SysDataSyncBatch;
import com.sinosoft.common.sync.domain.SysDataSyncDetail;
import com.sinosoft.common.sync.event.DataSyncEvent;
import com.sinosoft.common.sync.factory.ConfigDtoMappingFactory;
import com.sinosoft.common.sync.util.ConfigKeyExtractor;
import com.sinosoft.integration.api.core.Lis7HttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * HTTP同步策略实现 - 批次模式
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpSyncStrategy implements SyncStrategy {

    private final ObjectMapper objectMapper;
    private final Lis7SyncClient lis7SyncClient;
    private final ConfigDtoMappingFactory configDtoMappingFactory;
    private final ConfigKeyExtractor configKeyExtractor; // 使用新的提取工具


    @Override
    public boolean supports(SyncConfig.TargetSystemConfig targetConfig) {
        return "HTTP".equalsIgnoreCase(targetConfig.getSyncMode());
    }

    @Override
    public void syncData(List<?> dataList, SyncConfig.TargetSystemConfig targetConfig,
                         DataSyncEvent event, SysDataSyncBatch batch, List<SysDataSyncDetail> details) {
        if (dataList == null || dataList.isEmpty()) {
            log.warn("同步数据列表为空，跳过同步");
            return;
        }

        try {
            String tableName = event.getTableName();
            log.debug("通过HTTP批量同步表 {} 的数据，数量: {}", tableName, dataList.size());

            // 1. 使用工厂创建请求对象
            Lis7HttpRequest<?> request = configDtoMappingFactory.createRequest(tableName, dataList);

            // 2. 发送请求
            GlobalResponse<?> response = lis7SyncClient.sendRequest(request);

            // 3. 获取主键名并批量提取主键值
            String primaryKeyName = event.getPrimaryKeyName();
            if (primaryKeyName == null) {
                primaryKeyName = configDtoMappingFactory.getPrimaryKeyByTable(tableName);
                if (primaryKeyName == null) {
                    primaryKeyName = tableName + "_id";
                }
            }
            List<String> primaryKeyValues = configKeyExtractor.extractPrimaryKeys(dataList, tableName, primaryKeyName);

            // 4. 创建批次记录
            fillBatchInfo(batch, event, targetConfig, response, dataList.size());

            // 5. 如果请求失败，创建详情记录
            if (!response.isSuccess()) {
                for (int i = 0; i < dataList.size(); i++) {
                    String primaryKeyValue = (i < primaryKeyValues.size()) ?
                        primaryKeyValues.get(i) : "unknown-" + i;

                    SysDataSyncDetail detail = createDetail(primaryKeyValue, response.getMessage());
                    details.add(detail);
                }
                log.error("通过HTTP批量同步表 {} 的数据失败: {}", tableName, response.getMessage());
            }

            log.debug("完成通过HTTP批量同步表 {} 的数据", tableName);
        } catch (Exception e) {
            log.error("通过HTTP批量同步表 {} 的数据失败: {}", event.getTableName(), e.getMessage(), e);

            // 请求异常，记录批次失败信息
            fillBatchInfo(batch, event, targetConfig, null, dataList.size());
            batch.setStatus(0L);
            batch.setFailCount((long) dataList.size());
            batch.setSuccessCount(0L);

            // 添加一个通用的错误详情记录
            SysDataSyncDetail detail = createDetail("batch-error", e.getMessage());
            details.add(detail);
        }
    }

    /**
     * 填充批次信息
     */
    private void fillBatchInfo(SysDataSyncBatch batch, DataSyncEvent event,
                               SyncConfig.TargetSystemConfig targetConfig,
                               GlobalResponse<?> response, int totalCount) {
        batch.setTableName(event.getTableName());
        batch.setOperationType((long) event.getOperationType().getCode());
        batch.setTargetSystem(targetConfig.getSystemCode());
        batch.setTargetSystemName(targetConfig.getSystemName());
        batch.setSyncMode("HTTP");
        batch.setApiUrl(targetConfig.getApiUrl());
        batch.setApiMethod(targetConfig.getApiMethod());

        // API响应
        if (response != null) {
            try {
                batch.setApiResponse(objectMapper.writeValueAsString(response));
                // 设置成功/失败计数
                boolean isSuccess = response.isSuccess();
                batch.setStatus(isSuccess ? 1L : 0L);
                batch.setTotalCount((long) totalCount);
                batch.setSuccessCount(isSuccess ? (long) totalCount : 0L);
                batch.setFailCount(isSuccess ? 0L : (long) totalCount);
            } catch (Exception e) {
                log.error("序列化API响应失败", e);
                batch.setApiResponse("序列化失败: " + e.getMessage());
                batch.setStatus(0L);
                batch.setFailCount((long) totalCount);
            }
        } else {
            batch.setStatus(0L);
            batch.setTotalCount((long) totalCount);
            batch.setFailCount((long) totalCount);
            batch.setSuccessCount(0L);
        }

        // 其他信息
        batch.setSyncTime(new Date());
        batch.setOperator(event.getLoginUser() != null ? event.getLoginUser().getUsername() : "system");
        batch.setBusinessCode(event.getBusinessCode());
        batch.setRemark(event.getRemark());
    }

    /**
     * 创建详情记录
     */
    private SysDataSyncDetail createDetail(String primaryKeyValue, String failReason) {
        SysDataSyncDetail detail = new SysDataSyncDetail();
        detail.setPrimaryKeyValue(primaryKeyValue);
        detail.setStatus(0L); // 失败
        detail.setFailReason(failReason);
        detail.setSyncTime(new Date());
        return detail;
    }
}
