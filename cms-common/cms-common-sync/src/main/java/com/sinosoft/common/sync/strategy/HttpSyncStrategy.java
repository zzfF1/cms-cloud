package com.sinosoft.common.sync.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.sync.adapter.HttpAdapter;
import com.sinosoft.common.sync.config.SyncConfig;
import com.sinosoft.common.sync.domain.SyncRecord;
import com.sinosoft.common.sync.event.DataSyncEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * HTTP同步策略实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpSyncStrategy implements SyncStrategy {

    private final HttpAdapter httpAdapter;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(SyncConfig.TargetSystemConfig targetConfig) {
        return "HTTP".equalsIgnoreCase(targetConfig.getSyncMode());
    }

    @Override
    public void syncData(Map<String, Object> syncMessage, SyncConfig.TargetSystemConfig targetConfig,
                         DataSyncEvent event, String primaryKeyValue, List<SyncRecord> syncRecords) {
//        try {
//            // 检查API URL是否配置
//            if (targetConfig.getApiUrl() == null || targetConfig.getApiUrl().isEmpty()) {
//                throw new RuntimeException("未配置API URL");
//            }
//
//            // 序列化消息
//            String messageBody = objectMapper.writeValueAsString(syncMessage);
//
//            // 准备HTTP请求头
//            Map<String, String> headers = new HashMap<>();
//            headers.put("Content-Type", "application/json");
//            if (targetConfig.getApiToken() != null && !targetConfig.getApiToken().isEmpty()) {
//                headers.put("Authorization", "Bearer " + targetConfig.getApiToken());
//            }
//            headers.put("X-Sync-Source", "CMS");
//            headers.put("X-Sync-ID", UUID.randomUUID().toString());
//            headers.put("X-Sync-Timestamp", String.valueOf(System.currentTimeMillis()));
//            headers.put("X-Sync-Table", event.getTableName());
//            headers.put("X-Sync-Operation", String.valueOf(event.getOperationType().getCode()));
//
//            // 确定HTTP方法
//            HttpMethod httpMethod = HttpMethod.POST; // 默认POST
//            if (targetConfig.getApiMethod() != null) {
//                try {
//                    httpMethod = HttpMethod.valueOf(targetConfig.getApiMethod().toUpperCase());
//                } catch (IllegalArgumentException e) {
//                    log.warn("不支持的HTTP方法: {}, 将使用POST", targetConfig.getApiMethod());
//                }
//            }
//
//            // 超时时间（默认30秒）
//            int timeout = targetConfig.getApiTimeout() != null ? targetConfig.getApiTimeout() : 30000;
//
//            // 发送HTTP请求
//            String response = httpAdapter.sendRequest(targetConfig.getApiUrl(), httpMethod, messageBody, headers, timeout);
//
//            // 记录成功
//            SyncRecord record = SyncRecord.builder()
//                .tableName(event.getTableName())
//                .primaryKeyValue(primaryKeyValue)
//                .operationType(event.getOperationType().getCode())
//                .targetSystem(targetConfig.getSystemCode())
//                .targetSystemName(targetConfig.getSystemName())
//                .syncMode("HTTP")
//                .apiUrl(targetConfig.getApiUrl())
//                .apiMethod(targetConfig.getApiMethod())
//                .apiResponse(response)
//                .status(1) // 1-发送成功
//                .syncTime(new Date())
//                .operator(event.getLoginUser() != null ? event.getLoginUser().getUsername() : "system")
//                .businessCode(event.getBusinessCode())
//                .remark(event.getRemark())
//                .build();
//
//            syncRecords.add(record);
//
//            log.debug("成功通过HTTP接口同步表 {} 的数据 {} 到系统 {}",
//                event.getTableName(), primaryKeyValue, targetConfig.getSystemName());
//        } catch (Exception e) {
//            log.error("通过HTTP接口同步表 {} 的数据 {} 到系统 {} 失败",
//                event.getTableName(), primaryKeyValue, targetConfig.getSystemName(), e);
//
//            // 记录错误
//            SyncRecord record = createFailureRecord(event, primaryKeyValue, targetConfig, e.getMessage());
//            syncRecords.add(record);
//        }
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
            .syncMode("HTTP")
            .apiUrl(targetConfig.getApiUrl())
            .apiMethod(targetConfig.getApiMethod())
            .status(0) // 0-发送失败
            .syncTime(new Date())
            .failReason(failReason)
            .operator(event.getLoginUser() != null ? event.getLoginUser().getUsername() : "system")
            .businessCode(event.getBusinessCode())
            .remark(event.getRemark())
            .build();
    }
}
