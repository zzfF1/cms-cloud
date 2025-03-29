package com.sinosoft.common.sync.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sinosoft.common.sync.domain.SysDataSyncBatch;
import com.sinosoft.common.sync.domain.SysDataSyncDetail;
import com.sinosoft.common.sync.mapper.SysDataSyncBatchMapper;
import com.sinosoft.common.sync.mapper.SysDataSyncDetailMapper;
import com.sinosoft.common.sync.service.ISysDataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 批次同步服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDataSyncServiceImpl implements ISysDataSyncService {

    private final SysDataSyncBatchMapper batchMapper;
    private final SysDataSyncDetailMapper detailMapper;

    /**
     * 保存批次数据和详情记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatchAndDetails(SysDataSyncBatch batch, List<SysDataSyncDetail> details) {
        if (batch == null) {
            log.warn("没有需要保存的批次记录");
            return;
        }

        try {
            // 1. 插入批次记录
            batchMapper.insert(batch);
            Long batchId = batch.getId();

            // 2. 如果有详情记录，设置批次ID并批量插入
            if (details != null && !details.isEmpty()) {
                for (SysDataSyncDetail detail : details) {
                    detail.setBatchId(batchId);
                }
                // 使用MyBatis Plus的批量插入
                for (SysDataSyncDetail detail : details) {
                    detailMapper.insert(detail);
                }
            }

            log.info("成功保存批次同步记录，批次ID: {}, 总记录数: {}, 成功: {}, 失败: {}",
                batchId, batch.getTotalCount(), batch.getSuccessCount(), batch.getFailCount());
        } catch (Exception e) {
            log.error("保存批次同步记录失败", e);
            throw e;
        }
    }

    /**
     * 查询批次信息
     */
    @Override
    public SysDataSyncBatch getBatchById(Long batchId) {
        return batchMapper.selectById(batchId);
    }

    /**
     * 查询批次下的详情记录
     */
    @Override
    public List<SysDataSyncDetail> getDetailsByBatchId(Long batchId) {
        LambdaQueryWrapper<SysDataSyncDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysDataSyncDetail::getBatchId, batchId);
        return detailMapper.selectList(wrapper);
    }

    /**
     * 分页查询批次信息
     */
    @Override
    public List<SysDataSyncBatch> queryBatchesByPage(SysDataSyncBatch condition, int pageNum, int pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<SysDataSyncBatch> wrapper = Wrappers.lambdaQuery();

        if (condition != null) {
            if (condition.getTableName() != null && !condition.getTableName().isEmpty()) {
                wrapper.eq(SysDataSyncBatch::getTableName, condition.getTableName());
            }
            if (condition.getTargetSystem() != null && !condition.getTargetSystem().isEmpty()) {
                wrapper.eq(SysDataSyncBatch::getTargetSystem, condition.getTargetSystem());
            }
            if (condition.getSyncMode() != null && !condition.getSyncMode().isEmpty()) {
                wrapper.eq(SysDataSyncBatch::getSyncMode, condition.getSyncMode());
            }
            if (condition.getStatus() != null) {
                wrapper.eq(SysDataSyncBatch::getStatus, condition.getStatus());
            }
            if (condition.getOperationType() != null) {
                wrapper.eq(SysDataSyncBatch::getOperationType, condition.getOperationType());
            }
            if (condition.getBusinessCode() != null && !condition.getBusinessCode().isEmpty()) {
                wrapper.eq(SysDataSyncBatch::getBusinessCode, condition.getBusinessCode());
            }
        }

        // 按ID降序排序
        wrapper.orderByDesc(SysDataSyncBatch::getId);

        // 计算分页参数
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysDataSyncBatch> page =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);

        return batchMapper.selectPage(page, wrapper).getRecords();
    }

    /**
     * 更新批次状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchStatus(Long batchId, Long status, String feedbackMessage) {
        try {
            SysDataSyncBatch batch = batchMapper.selectById(batchId);
            if (batch == null) {
                log.warn("未找到批次记录: {}", batchId);
                return false;
            }

            batch.setStatus(status);
            batch.setFeedbackTime(new Date());
            batch.setRemark(feedbackMessage);

            // 如果是成功反馈，更新统计数据
            if (status == 2L) { // 2-接收方确认成功
                batch.setSuccessCount(batch.getTotalCount());
                batch.setFailCount(0L);
            } else if (status == 3L) { // 3-接收方处理失败
                batch.setSuccessCount(0L);
                batch.setFailCount(batch.getTotalCount());
            }

            batchMapper.updateById(batch);
            return true;
        } catch (Exception e) {
            log.error("更新批次状态失败", e);
            return false;
        }
    }
}
