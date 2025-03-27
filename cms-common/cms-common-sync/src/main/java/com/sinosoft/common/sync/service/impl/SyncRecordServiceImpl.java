package com.sinosoft.common.sync.service.impl;

import com.sinosoft.common.sync.domain.SyncRecord;
import com.sinosoft.common.sync.mapper.SyncRecordMapper;
import com.sinosoft.common.sync.service.ISyncRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        return List.of();
    }

    @Override
    public List<SyncRecord> queryRecentRecords(int limit) {
        return List.of();
    }

    @Override
    public boolean updateStatusByMessageId(String messageId, int status, Date feedbackTime, String failReason) {
        return false;
    }

    @Override
    public boolean insertBatch(List<SyncRecord> records) {
        return syncRecordMapper.insertBatch(records);
    }
}
