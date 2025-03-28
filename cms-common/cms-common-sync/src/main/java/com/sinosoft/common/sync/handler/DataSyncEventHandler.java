package com.sinosoft.common.sync.handler;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.sync.adapter.HttpAdapter;
import com.sinosoft.common.sync.adapter.MqAdapter;
import com.sinosoft.common.sync.config.SyncConfigManager;
import com.sinosoft.common.sync.domain.SyncRecord;
import com.sinosoft.common.sync.event.DataSyncEvent;
import com.sinosoft.common.sync.config.SyncConfig;
import com.sinosoft.common.sync.service.ISyncRecordService;
import com.sinosoft.common.sync.strategy.SyncStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据同步事件处理器
 * <p>
 * 监听同步事件并在事务提交后异步处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "sync", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DataSyncEventHandler {

    private final SyncConfigManager configManager;
    private final ISyncRecordService syncRecordService;
    private final ObjectMapper objectMapper;
    private final List<SyncStrategy> syncStrategies;

    /**
     * 事务提交后异步处理数据同步事件
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleDataSyncEvent(DataSyncEvent event) {
        String tableName = event.getTableName();
        List<Object> dataList = (List<Object>) event.getDataList();
        log.debug("异步处理数据同步, 表: {}, 操作类型: {}, 数据数量: {}", tableName, event.getOperationType(), dataList.size());
        try {
            // 1. 获取表的同步配置
            SyncConfig config = configManager.getConfig(tableName);
            if (config == null || !config.isEnabled()) {
                log.warn("表 {} 未配置同步或同步未启用，跳过同步", tableName);
                return;
            }
            // 3. 检查目标系统配置
            List<SyncConfig.TargetSystemConfig> enabledTargets = config.getTargetSystems().stream().filter(SyncConfig.TargetSystemConfig::isEnabled).collect(Collectors.toList());
            if (enabledTargets.isEmpty()) {
                log.warn("表 {} 未配置有效的目标系统，跳过同步", tableName);
                return;
            }
            // 4. 处理每条数据
            for (Object data : dataList) {
                processSingleData(config, event, data, enabledTargets);
            }
            log.debug("数据同步处理完成, 表: {}, 操作类型: {}", tableName, event.getOperationType());
        } catch (Exception e) {
            log.error("数据同步处理异常", e);
        }
    }

    /**
     * 处理单条数据同步
     */
    private void processSingleData(SyncConfig config, DataSyncEvent event, Object data,
                                   List<SyncConfig.TargetSystemConfig> targets) {
        try {
            // 提取主键值
            String primaryKeyValue = extractPrimaryKeyValue(data, event.getPrimaryKeyName() != null ? event.getPrimaryKeyName() : config.getPrimaryKey());

            if (primaryKeyValue == null) {
                log.warn("无法从数据中提取主键值，跳过同步");
                return;
            }
            // 构建同步消息对象
            Map<String, Object> syncMessage = new HashMap<>();
            syncMessage.put("tableName", event.getTableName());
            syncMessage.put("primaryKey", primaryKeyValue);
            syncMessage.put("operationType", event.getOperationType().getCode());
            syncMessage.put("operationDesc", event.getOperationType().getDesc());
            syncMessage.put("syncTime", new Date());
            syncMessage.put("operator", event.getLoginUser() != null ? event.getLoginUser().getUsername() : "system");
            syncMessage.put("businessCode", event.getBusinessCode());
            syncMessage.put("remark", event.getRemark());
            // 添加业务数据
            syncMessage.put("data", data);
            // 记录同步结果
            List<SyncRecord> syncRecords = new ArrayList<>();
            // 为每个目标系统发送消息
            for (SyncConfig.TargetSystemConfig targetConfig : targets) {
                try {
                    // 查找支持该目标系统的策略
                    SyncStrategy strategy = findSupportedStrategy(targetConfig);
                    // 执行同步
                    strategy.syncData(syncMessage, targetConfig, event, primaryKeyValue, syncRecords);
                } catch (Exception e) {
                    log.error("同步表 {} 的数据 {} 到系统 {} 失败", event.getTableName(), primaryKeyValue, targetConfig.getSystemName(), e);
                    // 记录通用失败信息
                    SyncRecord record = createFailureRecord(event, primaryKeyValue, targetConfig, e.getMessage());
                    syncRecords.add(record);
                }
            }
            // 批量保存同步记录
            if (!syncRecords.isEmpty()) {
                syncRecordService.insertBatch(syncRecords);
            }
        } catch (Exception e) {
            log.error("处理单条数据同步失败", e);
        }
    }

    /**
     * 查找支持目标系统的同步策略
     */
    private SyncStrategy findSupportedStrategy(SyncConfig.TargetSystemConfig targetConfig) {
        return syncStrategies.stream()
            .filter(strategy -> strategy.supports(targetConfig))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("未找到支持的同步策略，同步模式: " + targetConfig.getSyncMode()));
    }

    /**
     * 创建失败记录
     */
    private SyncRecord createFailureRecord(DataSyncEvent event, String primaryKeyValue,
                                           SyncConfig.TargetSystemConfig targetConfig, String failReason) {
        SyncRecord.SyncRecordBuilder builder = SyncRecord.builder()
            .tableName(event.getTableName())
            .primaryKeyValue(primaryKeyValue)
            .operationType(event.getOperationType().getCode())
            .targetSystem(targetConfig.getSystemCode())
            .targetSystemName(targetConfig.getSystemName())
            .status(0) // 0-发送失败
            .syncTime(new Date())
            .failReason(failReason)
            .operator(event.getLoginUser() != null ? event.getLoginUser().getUsername() : "system")
            .businessCode(event.getBusinessCode())
            .remark(event.getRemark());

        // 根据同步模式设置相关字段
        String syncMode = targetConfig.getSyncMode();
        if ("HTTP".equalsIgnoreCase(syncMode)) {
            builder.syncMode("HTTP").apiUrl(targetConfig.getApiUrl()).apiMethod(targetConfig.getApiMethod());
        } else if ("DUBBO".equalsIgnoreCase(syncMode)) {
            builder.syncMode("DUBBO").apiUrl(targetConfig.getServiceId());
        } else {
            builder.syncMode("MQ").mqType(targetConfig.getMqType()).topic(targetConfig.getTopic()).queue(targetConfig.getQueue());
        }
        return builder.build();
    }

    /**
     * 提取主键值
     */
    private String extractPrimaryKeyValue(Object data, String keyName) {
        try {
            // 使用Hutool的BeanUtil获取属性值（支持驼峰和下划线命名）
            Object value = BeanUtil.getProperty(data, keyName);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.error("获取主键值失败: {}", e.getMessage());
            return null;
        }
    }
}
