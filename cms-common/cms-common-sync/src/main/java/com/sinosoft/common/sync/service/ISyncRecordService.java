package com.sinosoft.common.sync.service;

import com.sinosoft.common.sync.domain.SyncRecord;

import java.util.Date;
import java.util.List;

/**
 * 同步记录服务接口
 */
public interface ISyncRecordService {

    /**
     * 查询表的同步记录
     *
     * @param tableName       表名
     * @param primaryKeyValue 主键值
     * @return 同步记录列表
     */
    List<SyncRecord> queryRecords(String tableName, String primaryKeyValue);

    /**
     * 查询最近的同步记录
     *
     * @param limit 查询条数
     * @return 同步记录列表
     */
    List<SyncRecord> queryRecentRecords(int limit);

    /**
     * 根据消息ID更新状态
     *
     * @param messageId    消息ID
     * @param status       新状态
     * @param feedbackTime 反馈时间
     * @param failReason   失败原因
     * @return 是否成功
     */
    boolean updateStatusByMessageId(String messageId, int status, Date feedbackTime, String failReason);

    /**
     * 批量插入同步记录
     *
     * @param records 同步数据
     * @return 是否成功
     */
    boolean insertBatch(List<SyncRecord> records);
}
