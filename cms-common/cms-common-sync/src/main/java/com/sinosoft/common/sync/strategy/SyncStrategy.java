package com.sinosoft.common.sync.strategy;

import com.sinosoft.common.sync.config.SyncConfig;
import com.sinosoft.common.sync.domain.SysDataSyncBatch;
import com.sinosoft.common.sync.domain.SysDataSyncDetail;
import com.sinosoft.common.sync.event.DataSyncEvent;

import java.util.List;

/**
 * 同步策略接口 - 批次模式
 * 定义不同同步方式的实现策略
 */
public interface SyncStrategy {
    /**
     * 是否支持该目标系统配置
     */
    boolean supports(SyncConfig.TargetSystemConfig targetConfig);

    /**
     * 执行同步操作 - 批量处理
     * @param dataList 数据列表
     * @param targetConfig 目标系统配置
     * @param event 同步事件
     * @param batch 返回的批次记录，方法内构建并填充
     * @param details 返回的详情记录列表，方法内构建并填充
     */
    void syncData(List<?> dataList, SyncConfig.TargetSystemConfig targetConfig,
                  DataSyncEvent event, SysDataSyncBatch batch, List<SysDataSyncDetail> details);
}
