package com.sinosoft.common.sync.handler;

import com.sinosoft.common.sync.config.SyncConfigManager;
import com.sinosoft.common.sync.domain.SysDataSyncBatch;
import com.sinosoft.common.sync.domain.SysDataSyncDetail;
import com.sinosoft.common.sync.event.DataSyncEvent;
import com.sinosoft.common.sync.config.SyncConfig;
import com.sinosoft.common.sync.service.ISysDataSyncService;
import com.sinosoft.common.sync.strategy.SyncStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据同步事件处理器 - 批次模式
 * <p>
 * 监听同步事件并在事务提交后异步处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "sync", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DataSyncEventHandler {

    private final SyncConfigManager configManager;
    private final ISysDataSyncService sysDataSyncService;
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

            // 2. 检查目标系统配置
            List<SyncConfig.TargetSystemConfig> enabledTargets = config.getTargetSystems().stream()
                .filter(SyncConfig.TargetSystemConfig::isEnabled)
                .collect(Collectors.toList());

            if (enabledTargets.isEmpty()) {
                log.warn("表 {} 未配置有效的目标系统，跳过同步", tableName);
                return;
            }

            // 3. 为每个目标系统进行批量同步
            for (SyncConfig.TargetSystemConfig targetConfig : enabledTargets) {
                try {
                    // 查找支持该目标系统的策略
                    SyncStrategy strategy = findSupportedStrategy(targetConfig);

                    // 创建批次对象和详情列表
                    SysDataSyncBatch batch = new SysDataSyncBatch();
                    List<SysDataSyncDetail> details = new ArrayList<>();

                    // 执行批量同步
                    strategy.syncData(dataList, targetConfig, event, batch, details);

                    // 保存批次和详情记录
                    sysDataSyncService.saveBatchAndDetails(batch, details);

                } catch (Exception e) {
                    log.error("同步表 {} 数据到系统 {} 失败", tableName, targetConfig.getSystemName(), e);

                    // 记录通用失败批次
                    SysDataSyncBatch errorBatch = createErrorBatch(event, targetConfig, dataList.size(), e.getMessage());
                    SysDataSyncDetail errorDetail = createErrorDetail(e.getMessage());

                    sysDataSyncService.saveBatchAndDetails(errorBatch, Collections.singletonList(errorDetail));
                }
            }

            log.debug("数据同步处理完成, 表: {}, 操作类型: {}", tableName, event.getOperationType());
        } catch (Exception e) {
            log.error("数据同步处理异常", e);
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
     * 创建错误批次
     */
    private SysDataSyncBatch createErrorBatch(DataSyncEvent event, SyncConfig.TargetSystemConfig targetConfig,
                                              int totalCount, String errorMessage) {
        SysDataSyncBatch batch = new SysDataSyncBatch();
        batch.setTableName(event.getTableName());
        batch.setOperationType((long) event.getOperationType().getCode());
        batch.setTargetSystem(targetConfig.getSystemCode());
        batch.setTargetSystemName(targetConfig.getSystemName());
        batch.setStatus(0L); // 0-发送失败
        batch.setTotalCount((long) totalCount);
        batch.setSuccessCount(0L);
        batch.setFailCount((long) totalCount);
        batch.setSyncTime(new Date());
        batch.setOperator(event.getLoginUser() != null ? event.getLoginUser().getUsername() : "system");
        batch.setBusinessCode(event.getBusinessCode());
        batch.setRemark("同步处理异常: " + errorMessage);

        // 根据同步模式设置相关字段
        String syncMode = targetConfig.getSyncMode();
        if ("HTTP".equalsIgnoreCase(syncMode)) {
            batch.setSyncMode("HTTP");
            batch.setApiUrl(targetConfig.getApiUrl());
            batch.setApiMethod(targetConfig.getApiMethod());
        } else {
            batch.setSyncMode("MQ");
            batch.setMqType(targetConfig.getMqType());
            batch.setTopic(targetConfig.getTopic());
            batch.setQueue(targetConfig.getQueue());
        }
        return batch;
    }

    /**
     * 创建错误详情记录
     */
    private SysDataSyncDetail createErrorDetail(String errorMessage) {
        SysDataSyncDetail detail = new SysDataSyncDetail();
        detail.setPrimaryKeyValue("batch-error");
        detail.setStatus(0L); // 失败
        detail.setFailReason(errorMessage);
        detail.setSyncTime(new Date());
        return detail;
    }
}
