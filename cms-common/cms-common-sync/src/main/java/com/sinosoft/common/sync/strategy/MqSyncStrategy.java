package com.sinosoft.common.sync.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.sync.adapter.MqAdapter;
import com.sinosoft.common.sync.config.SyncConfig;
import com.sinosoft.common.sync.domain.SyncRecord;
import com.sinosoft.common.sync.event.DataSyncEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * MQ同步策略实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqSyncStrategy implements SyncStrategy {

    private final MqAdapter mqAdapter;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(SyncConfig.TargetSystemConfig targetConfig) {
        return "MQ".equalsIgnoreCase(targetConfig.getSyncMode());
    }

    @Override
    public void syncData(Map<String, Object> syncMessage, SyncConfig.TargetSystemConfig targetConfig,
                         DataSyncEvent event, String primaryKeyValue, List<SyncRecord> syncRecords) {
        try {
            // 序列化消息
            String messageBody = objectMapper.writeValueAsString(syncMessage);

            // 消息键（用于消息查询和幂等处理）
            String keys = event.getTableName() + "_" + primaryKeyValue + "_" + targetConfig.getSystemCode();

            // 设置属性（包含必要的消息追踪信息）
            Map<String, String> properties = new HashMap<>();
            properties.put("sourceSystem", "CMS"); // 源系统标识
            properties.put("syncId", UUID.randomUUID().toString());
            properties.put("timestamp", String.valueOf(System.currentTimeMillis()));
            properties.put("tableName", event.getTableName());
            properties.put("operationType", String.valueOf(event.getOperationType().getCode()));

            // 发送消息
            String messageId = mqAdapter.sendMessageWithProperties(
                targetConfig.getTopic(),
                targetConfig.getTag(),
                messageBody,
                keys,
                properties
            );

            // 记录成功或失败
            SyncRecord record = SyncRecord.builder()
                .tableName(event.getTableName())
                .primaryKeyValue(primaryKeyValue)
                .operationType(event.getOperationType().getCode())
                .targetSystem(targetConfig.getSystemCode())
                .targetSystemName(targetConfig.getSystemName())
                .syncMode("MQ")
                .mqType(targetConfig.getMqType())
                .topic(targetConfig.getTopic())
                .queue(targetConfig.getQueue())
                .messageId(messageId)
                .status(messageId != null ? 1 : 0) // 1-发送成功，0-发送失败
                .syncTime(new Date())
                .operator(event.getLoginUser() != null ? event.getLoginUser().getUsername() : "system")
                .businessCode(event.getBusinessCode())
                .remark(event.getRemark())
                .build();

            if (messageId == null) {
                record.setStatus(0); // 发送失败
                record.setFailReason("MQ消息发送失败");
                log.error("通过MQ同步表 {} 的数据 {} 到系统 {} 失败",
                    event.getTableName(), primaryKeyValue, targetConfig.getSystemName());
            } else {
                log.debug("成功通过MQ同步表 {} 的数据 {} 到系统 {}",
                    event.getTableName(), primaryKeyValue, targetConfig.getSystemName());
            }

            syncRecords.add(record);
        } catch (Exception e) {
            log.error("通过MQ同步表 {} 的数据 {} 到系统 {} 失败",
                event.getTableName(), primaryKeyValue, targetConfig.getSystemName(), e);

            // 记录错误
            SyncRecord record = createFailureRecord(event, primaryKeyValue, targetConfig, e.getMessage());
            syncRecords.add(record);
        }
    }

    /**
     * 创建失败记录
     */
    private SyncRecord createFailureRecord(DataSyncEvent event, String primaryKeyValue,
                                           SyncConfig.TargetSystemConfig targetConfig, String failReason) {
        return SyncRecord.builder()
            .tableName(event.getTableName())
            .primaryKeyValue(primaryKeyValue)
            .operationType(event.getOperationType().getCode())
            .targetSystem(targetConfig.getSystemCode())
            .targetSystemName(targetConfig.getSystemName())
            .syncMode("MQ")
            .mqType(targetConfig.getMqType())
            .topic(targetConfig.getTopic())
            .queue(targetConfig.getQueue())
            .status(0) // 0-发送失败
            .syncTime(new Date())
            .failReason(failReason)
            .operator(event.getLoginUser() != null ? event.getLoginUser().getUsername() : "system")
            .businessCode(event.getBusinessCode())
            .remark(event.getRemark())
            .build();
    }
}
