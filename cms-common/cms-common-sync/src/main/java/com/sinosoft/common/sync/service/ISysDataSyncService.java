package com.sinosoft.common.sync.service;

import com.sinosoft.common.sync.domain.SysDataSyncBatch;
import com.sinosoft.common.sync.domain.SysDataSyncDetail;

import java.util.List;

/**
 * 批次同步服务接口
 */
public interface ISysDataSyncService {

    /**
     * 保存批次数据和详情记录
     *
     * @param batch 批次记录
     * @param details 详情记录列表
     */
    void saveBatchAndDetails(SysDataSyncBatch batch, List<SysDataSyncDetail> details);

    /**
     * 查询批次信息
     *
     * @param batchId 批次ID
     * @return 批次信息
     */
    SysDataSyncBatch getBatchById(Long batchId);

    /**
     * 查询批次下的详情记录
     *
     * @param batchId 批次ID
     * @return 详情记录列表
     */
    List<SysDataSyncDetail> getDetailsByBatchId(Long batchId);

    /**
     * 分页查询批次信息
     *
     * @param condition 查询条件
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 批次信息列表
     */
    List<SysDataSyncBatch> queryBatchesByPage(SysDataSyncBatch condition, int pageNum, int pageSize);

    /**
     * 更新批次状态
     *
     * @param batchId 批次ID
     * @param status 状态（2-接收方确认成功，3-接收方处理失败）
     * @param feedbackMessage 反馈信息
     * @return 是否更新成功
     */
    boolean updateBatchStatus(Long batchId, Long status, String feedbackMessage);
}
