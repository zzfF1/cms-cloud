package com.sinosoft.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sinosoft.common.domain.dto.WageCalAgentDto;
import com.sinosoft.common.schema.commission.domain.WageCalculationBatch;
import com.sinosoft.common.schema.commission.domain.WageCalculationError;
import com.sinosoft.common.schema.commission.mapper.WageCalculationBatchMapper;
import com.sinosoft.common.schema.commission.mapper.WageCalculationErrorMapper;
import com.sinosoft.common.service.IWageCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 佣金计算记录服务实现类
 *
 * @author zzf
 * @date 2025-02-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WageCalculationServiceImpl implements IWageCalculationService {
    private final WageCalculationBatchMapper batchMapper;
    private final WageCalculationErrorMapper errorMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createCalculationBatch(WageCalculationBatch batch) {
        try {
            // 设置历史批次为非当前
            setHistoricalBatchesNonCurrent(batch.getBatchId());

            // 插入新批次
            return batchMapper.insert(batch) > 0;
        } catch (Exception e) {
            log.error("创建佣金计算批次失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean updateCalculationBatch(WageCalculationBatch batch) {
        try {
            return batchMapper.updateById(batch) > 0;
        } catch (Exception e) {
            log.error("更新佣金计算批次失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Long recordCalculationError(WageCalculationError error) {
        try {
            errorMapper.insert(error);
            return error.getId();
        } catch (Exception e) {
            log.error("记录佣金计算错误失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public WageCalculationBatch getBatchByRunId(String runId) {
        try {
            LambdaQueryWrapper<WageCalculationBatch> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WageCalculationBatch::getRunId, runId)
                .eq(WageCalculationBatch::getIsCurrent, 1);
            return batchMapper.selectOne(wrapper);
        } catch (Exception e) {
            log.error("获取批次信息失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchCount(String runId, String status, int increment) {
        try {
            WageCalculationBatch batch = getBatchByRunId(runId);
            if (batch == null) {
                log.error("更新批次计数失败: 找不到批次信息, runId={}", runId);
                return false;
            }

            // 根据状态更新相应的计数
            if ("PENDING".equals(status)) {
                batch.setPendingCount(batch.getPendingCount() + increment);
            } else if ("SUCCESS".equals(status)) {
                batch.setSuccessCount(batch.getSuccessCount() + increment);
                batch.setPendingCount(Math.max(0, batch.getPendingCount() - increment));
            } else if ("FAILED".equals(status)) {
                batch.setFailCount(batch.getFailCount() + increment);
                batch.setPendingCount(Math.max(0, batch.getPendingCount() - increment));
            }

            return batchMapper.updateById(batch) > 0;
        } catch (Exception e) {
            log.error("更新批次计数失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean setHistoricalBatchesNonCurrent(String batchId) {
        try {
            LambdaUpdateWrapper<WageCalculationBatch> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(WageCalculationBatch::getBatchId, batchId)
                .eq(WageCalculationBatch::getIsCurrent, 1)
                .set(WageCalculationBatch::getIsCurrent, 0);
            return batchMapper.update(null, wrapper) >= 0;
        } catch (Exception e) {
            log.error("设置历史批次为非当前失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 批量更新批次状态
     * 此方法直接从Redis获取统计结果并一次性更新到数据库，减少数据库操作次数
     *
     * @param runId 运行ID
     * @param successCount 成功计数
     * @param failCount 失败计数
     * @param pendingCount 待处理计数
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateCount(String runId, int successCount, int failCount, int pendingCount) {
        try {
            WageCalculationBatch batch = getBatchByRunId(runId);
            if (batch == null) {
                log.error("批量更新计数失败: 找不到批次信息, runId={}", runId);
                return false;
            }

            // 直接设置计数值
            batch.setSuccessCount(successCount);
            batch.setFailCount(failCount);
            batch.setPendingCount(pendingCount);

            // 如果没有待处理任务，且有成功或失败任务，则可以设置完成时间
            if (pendingCount == 0 && (successCount > 0 || failCount > 0)) {
                if (batch.getFinishTime() == null) {
                    batch.setFinishTime(new Date());
                }
            }

            return batchMapper.updateById(batch) > 0;
        } catch (Exception e) {
            log.error("批量更新计数失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
