package com.sinosoft.common.sync.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.sync.adapter.MqAdapter;
import com.sinosoft.common.sync.config.SyncConfig;
import com.sinosoft.common.sync.domain.SysDataSyncBatch;
import com.sinosoft.common.sync.domain.SysDataSyncDetail;
import com.sinosoft.common.sync.event.DataSyncEvent;
import com.sinosoft.common.sync.factory.ConfigDtoMappingFactory;
import com.sinosoft.common.sync.util.ConfigKeyExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * MQ同步策略实现 - 批次模式
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(MqAdapter.class)
public class MqSyncStrategy implements SyncStrategy {

    private final MqAdapter mqAdapter;
    private final ObjectMapper objectMapper;
    private final ConfigDtoMappingFactory configDtoMappingFactory;
    private final ConfigKeyExtractor configKeyExtractor;

    @Override
    public boolean supports(SyncConfig.TargetSystemConfig targetConfig) {
        return "MQ".equalsIgnoreCase(targetConfig.getSyncMode());
    }

    @Override
    public void syncData(List<?> dataList, SyncConfig.TargetSystemConfig targetConfig,
                         DataSyncEvent event, SysDataSyncBatch batch, List<SysDataSyncDetail> details) {
        if (dataList == null || dataList.isEmpty()) {
            log.warn("同步数据列表为空，跳过同步");
            return;
        }

        try {
            String tableName = event.getTableName();
            log.debug("通过MQ批量同步表 {} 的数据，数量: {}", tableName, dataList.size());

            // 1. 序列化消息
            String messageBody = objectMapper.writeValueAsString(dataList);

            // 2. 消息键（用于消息查询和幂等处理）
            String keys = tableName + "_batch_" + UUID.randomUUID().toString();

            // 3. 设置属性（包含必要的消息追踪信息）
            Map<String, String> properties = new HashMap<>();
            properties.put("sourceSystem", "CMS"); // 源系统标识
            properties.put("syncId", UUID.randomUUID().toString());
            properties.put("timestamp", String.valueOf(System.currentTimeMillis()));
            properties.put("tableName", tableName);
            properties.put("operationType", String.valueOf(event.getOperationType().getCode()));
            properties.put("totalCount", String.valueOf(dataList.size()));

            // 4. 发送消息
            String messageId = mqAdapter.sendMessageWithProperties(
                targetConfig.getTopic(),
                targetConfig.getTag(),
                messageBody,
                keys,
                properties
            );

            // 5. 创建批次记录
            fillBatchInfo(batch, event, targetConfig, messageId, dataList.size());

            // 6. 获取主键名并批量提取主键值
            String primaryKeyName = event.getPrimaryKeyName();
            if (primaryKeyName == null) {
                primaryKeyName = configDtoMappingFactory.getPrimaryKeyByTable(tableName);
                if (primaryKeyName == null) {
                    primaryKeyName = tableName + "_id";
                }
            }
            List<String> primaryKeyValues = configKeyExtractor.extractPrimaryKeys(dataList, tableName, primaryKeyName);

            // 7. 创建明细记录 - 无论成功还是失败都创建
            for (int i = 0; i < dataList.size(); i++) {
                String primaryKeyValue = (i < primaryKeyValues.size()) ?
                    primaryKeyValues.get(i) : "unknown-" + i;

                SysDataSyncDetail detail = new SysDataSyncDetail();
                detail.setPrimaryKeyValue(primaryKeyValue);

                // 如果成功，设置成功状态
                if (messageId != null) {
                    detail.setStatus(1L); // 成功
                    detail.setFailReason(null);
                } else {
                    detail.setStatus(0L); // 失败
                    detail.setFailReason("MQ消息发送失败");
                }

                detail.setSyncTime(new Date());
                details.add(detail);
            }

            log.debug("完成通过MQ批量同步表 {} 的数据", tableName);
        } catch (Exception e) {
            log.error("通过MQ批量同步表 {} 的数据失败: {}", event.getTableName(), e.getMessage(), e);

            // 请求异常，记录批次失败信息
            fillBatchInfo(batch, event, targetConfig, null, dataList.size());
            batch.setStatus(0L);
            batch.setFailCount((long) dataList.size());
            batch.setSuccessCount(0L);

            // 添加一个通用的错误详情记录
            SysDataSyncDetail detail = createDetail("batch-error", e.getMessage());
            details.add(detail);
        }
    }

    /**
     * 填充批次信息
     */
    private void fillBatchInfo(SysDataSyncBatch batch, DataSyncEvent event,
                               SyncConfig.TargetSystemConfig targetConfig,
                               String messageId, int totalCount) {
        batch.setTableName(event.getTableName());
        batch.setOperationType((long) event.getOperationType().getCode());
        batch.setTargetSystem(targetConfig.getSystemCode());
        batch.setTargetSystemName(targetConfig.getSystemName());
        batch.setSyncMode("MQ");
        batch.setMqType(targetConfig.getMqType());
        batch.setTopic(targetConfig.getTopic());
        batch.setQueue(targetConfig.getQueue());
        batch.setMessageId(messageId);

        // 设置成功/失败计数
        boolean isSuccess = messageId != null;
        batch.setStatus(isSuccess ? 1L : 0L);
        batch.setTotalCount((long) totalCount);
        batch.setSuccessCount(isSuccess ? (long) totalCount : 0L);
        batch.setFailCount(isSuccess ? 0L : (long) totalCount);

        // 其他信息
        batch.setSyncTime(new Date());
        batch.setOperator(event.getLoginUser() != null ? event.getLoginUser().getUsername() : "system");
        batch.setBusinessCode(event.getBusinessCode());
        batch.setRemark(event.getRemark());
    }

    /**
     * 创建详情记录
     */
    private SysDataSyncDetail createDetail(String primaryKeyValue, String failReason) {
        SysDataSyncDetail detail = new SysDataSyncDetail();
        detail.setPrimaryKeyValue(primaryKeyValue);
        detail.setStatus(0L); // 失败
        detail.setFailReason(failReason);
        detail.setSyncTime(new Date());
        return detail;
    }
}
