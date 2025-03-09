package com.sinosoft.common.service;

import com.sinosoft.common.domain.dto.WageCalAgentDto;
import com.sinosoft.common.schema.commission.domain.WageCalculationBatch;
import com.sinosoft.common.schema.commission.domain.WageCalculationError;

import java.util.List;

/**
 * 佣金计算记录服务接口
 *
 * @author zzf
 * @date 2025-02-27
 */
public interface IWageCalculationService {
    /**
     * 创建佣金计算批次记录
     *
     * @param batch 批次对象
     * @return 是否成功
     */
    boolean createCalculationBatch(WageCalculationBatch batch);

    /**
     * 更新佣金计算批次记录
     *
     * @param batch 批次对象
     * @return 是否成功
     */
    boolean updateCalculationBatch(WageCalculationBatch batch);

    /**
     * 记录佣金计算错误
     *
     * @param error 错误对象
     * @return 错误记录ID
     */
    Long recordCalculationError(WageCalculationError error);

    /**
     * 获取当前批次信息
     *
     * @param runId 运行ID
     * @return 批次信息
     */
    WageCalculationBatch getBatchByRunId(String runId);

    /**
     * 更新批次计数 (单个任务完成时调用)
     *
     * @param runId     运行ID
     * @param status    状态
     * @param increment 增量
     * @return 是否成功
     */
    boolean updateBatchCount(String runId, String status, int increment);

    /**
     * 批量更新批次计数 (批量任务完成时调用)
     *
     * @param runId        运行ID
     * @param successCount 成功计数
     * @param failCount    失败计数
     * @param pendingCount 待处理计数
     * @return 是否成功
     */
    boolean batchUpdateCount(String runId, int successCount, int failCount, int pendingCount);

    /**
     * 设置历史批次为非当前
     *
     * @param batchId 批次ID
     * @return 是否成功
     */
    boolean setHistoricalBatchesNonCurrent(String batchId);
}
