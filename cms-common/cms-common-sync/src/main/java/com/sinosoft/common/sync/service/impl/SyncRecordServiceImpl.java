package com.sinosoft.common.sync.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sinosoft.common.sync.domain.SyncRecord;
import com.sinosoft.common.sync.mapper.SyncRecordMapper;
import com.sinosoft.common.sync.service.ISyncRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 同步记录服务实现
 *
 * @author: zzf
 * @create: 2025-03-17 05:45
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SyncRecordServiceImpl implements ISyncRecordService {

    private final SyncRecordMapper syncRecordMapper;

    @Override
    public List<SyncRecord> queryRecords(String tableName, String primaryKeyValue) {
        log.debug("查询同步记录: 表={}, 主键值={}", tableName, primaryKeyValue);
        LambdaQueryWrapper<SyncRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SyncRecord::getTableName, tableName)
            .eq(SyncRecord::getPrimaryKeyValue, primaryKeyValue)
            .orderByDesc(SyncRecord::getSyncTime);
        return syncRecordMapper.selectList(wrapper);
    }

    @Override
    public List<SyncRecord> queryRecentRecords(int limit) {
        log.debug("查询最近同步记录: limit={}", limit);
        LambdaQueryWrapper<SyncRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SyncRecord::getSyncTime)
            .last("LIMIT " + limit);
        return syncRecordMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public boolean updateStatusByMessageId(String messageId, int status, Date feedbackTime, String failReason) {
        log.debug("更新同步记录状态: messageId={}, status={}", messageId, status);
        if (messageId == null || messageId.isEmpty()) {
            log.warn("消息ID为空，无法更新状态");
            return false;
        }

        LambdaUpdateWrapper<SyncRecord> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SyncRecord::getMessageId, messageId)
            .set(SyncRecord::getStatus, status)
            .set(SyncRecord::getFeedbackTime, feedbackTime != null ? feedbackTime : new Date());

        if (failReason != null && !failReason.isEmpty()) {
            wrapper.set(SyncRecord::getFailReason, failReason);
        }

        int rows = syncRecordMapper.update(null, wrapper);
        return rows > 0;
    }

    @Override
    @Transactional
    public boolean insertBatch(List<SyncRecord> records) {
        if (records == null || records.isEmpty()) {
            log.warn("同步记录列表为空，跳过批量插入");
            return false;
        }

        log.debug("批量插入同步记录: 记录数={}", records.size());
        // 设置创建时间
        Date now = new Date();
        records.forEach(record -> {
            if (record.getCreateTime() == null) {
                record.setCreateTime(now);
            }
            if (record.getUpdateTime() == null) {
                record.setUpdateTime(now);
            }
        });

        return syncRecordMapper.insertBatch(records);
    }
}
