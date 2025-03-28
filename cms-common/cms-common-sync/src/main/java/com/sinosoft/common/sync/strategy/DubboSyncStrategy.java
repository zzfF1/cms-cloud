package com.sinosoft.common.sync.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.sync.adapter.ServiceSyncAdapter;
import com.sinosoft.common.sync.config.SyncConfig;
import com.sinosoft.common.sync.domain.SyncRecord;
import com.sinosoft.common.sync.event.DataSyncEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.BiFunction;

/**
 * Dubbo同步策略实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DubboSyncStrategy implements SyncStrategy {

    private final ServiceSyncAdapter serviceSyncAdapter;
    private final ObjectMapper objectMapper;

    // 表名与同步方法的映射
    private final Map<String, BiFunction<List<?>, String, ServiceSyncAdapter.SyncResult>> methodMappings = new HashMap<>();

    @PostConstruct
    public void initMethodMappings() {
        // 初始化表与方法的映射关系
        methodMappings.put("laagent", serviceSyncAdapter::syncAgentData);
        methodMappings.put("latree", serviceSyncAdapter::syncTreeData);
        methodMappings.put("labranchgroup", serviceSyncAdapter::syncBranchData);
        // 可以在这里添加更多的映射关系
        log.info("已初始化Dubbo同步方法映射: {}", methodMappings.keySet());
    }

    @Override
    public boolean supports(SyncConfig.TargetSystemConfig targetConfig) {
        return "DUBBO".equalsIgnoreCase(targetConfig.getSyncMode());
    }

    @Override
    public void syncData(Map<String, Object> syncMessage, SyncConfig.TargetSystemConfig targetConfig,
                         DataSyncEvent event, String primaryKeyValue, List<SyncRecord> syncRecords) {
        try {
            // 获取表名和实体数据
            String tableName = event.getTableName();
            Object data = syncMessage.get("data");

            log.debug("通过Dubbo同步表 {} 的数据, 主键值={}", tableName, primaryKeyValue);

            // 查找对应的同步方法
            BiFunction<List<?>, String, ServiceSyncAdapter.SyncResult> syncMethod =
                methodMappings.getOrDefault(tableName.toLowerCase(), this::defaultSyncMethod);

            // 准备数据列表
            List<?> dataList = (data instanceof List) ? (List<?>) data : Collections.singletonList(data);

            // 执行同步调用
            ServiceSyncAdapter.SyncResult result = syncMethod.apply(dataList, event.getBusinessCode());

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
     * 默认同步方法，处理未明确配置的表
     */
    private ServiceSyncAdapter.SyncResult defaultSyncMethod(List<?> dataList, String businessCode) {
        log.warn("未找到对应的表同步处理方法，使用默认处理: 数据条数={}, 业务编码={}",
            dataList.size(), businessCode);
        return new ServiceSyncAdapter.SyncResult(false, "未找到对应的表同步处理方法");
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
