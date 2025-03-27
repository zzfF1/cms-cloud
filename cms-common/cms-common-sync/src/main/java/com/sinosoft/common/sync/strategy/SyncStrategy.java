package com.sinosoft.common.sync.strategy;

import com.sinosoft.common.sync.config.SyncConfig;
import com.sinosoft.common.sync.domain.SyncRecord;
import com.sinosoft.common.sync.event.DataSyncEvent;

import java.util.List;
import java.util.Map;

/**
 * 同步策略接口
 * 定义不同同步方式的实现策略
 */
public interface SyncStrategy {
    /**
     * 是否支持该目标系统配置
     */
    boolean supports(SyncConfig.TargetSystemConfig targetConfig);

    /**
     * 执行同步操作
     */
    void syncData(Map<String, Object> syncMessage, SyncConfig.TargetSystemConfig targetConfig,
                  DataSyncEvent event, String primaryKeyValue, List<SyncRecord> syncRecords);
}
