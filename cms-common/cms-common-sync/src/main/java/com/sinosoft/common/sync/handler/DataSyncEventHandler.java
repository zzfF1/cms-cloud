package com.sinosoft.common.sync.handler;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.sync.adapter.HttpAdapter;
import com.sinosoft.common.sync.adapter.MqAdapter;
import com.sinosoft.common.sync.config.SyncConfigManager;
import com.sinosoft.common.sync.domain.SyncRecord;
import com.sinosoft.common.sync.event.DataSyncEvent;
import com.sinosoft.common.sync.model.SyncConfig;
import com.sinosoft.common.sync.service.ISyncRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据同步事件处理器
 * <p>
 * 监听同步事件并在事务提交后异步处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSyncEventHandler {

    private final SyncConfigManager configManager;
    private final ISyncRecordService syncRecordService;
    private final MqAdapter mqAdapter;
    private final HttpAdapter httpAdapter;
    private final ObjectMapper objectMapper;

    /**
     * 事务提交后异步处理数据同步事件
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleDataSyncEvent(DataSyncEvent event) {
        String tableName = event.getTableName();
        List<Object> dataList = (List<Object>) event.getDataList();  // 强制转换为Object列表

        log.debug("异步处理数据同步, 表: {}, 操作类型: {}, 数据数量: {}",
            tableName, event.getOperationType(), dataList.size());

        try {
            // 1. 获取表的同步配置
            SyncConfig config = configManager.getConfig(tableName);
            if (config == null || !config.getEnabled()) {
                log.warn("表 {} 未配置同步或同步未启用，跳过同步", tableName);
                return;
            }

            // 2. 检查操作类型是否支持
            if (!config.getSupportedOperations().contains(event.getOperationType().getCode())) {
                log.warn("表 {} 不支持 {} 操作的同步，跳过同步", tableName, event.getOperationType().getDesc());
                return;
            }

            // 3. 检查目标系统配置
            List<SyncConfig.TargetSystemConfig> enabledTargets = config.getTargetSystems().stream()
                .filter(SyncConfig.TargetSystemConfig::getEnabled)
                .collect(Collectors.toList());

            if (enabledTargets.isEmpty()) {
                log.warn("表 {} 未配置有效的目标系统，跳过同步", tableName);
                return;
            }

            // 4. 处理每条数据
            for (Object data : dataList) {
                processSingleData(config, event, data, enabledTargets);
            }

            log.debug("数据同步处理完成, 表: {}, 操作类型: {}", tableName, event.getOperationType());
        } catch (Exception e) {
            log.error("数据同步处理异常", e);
        }
    }

    /**
     * 处理单条数据同步
     */
    private void processSingleData(SyncConfig config, DataSyncEvent event, Object data,
                                   List<SyncConfig.TargetSystemConfig> targets) {
        try {
            // 提取主键值
            String primaryKeyValue = extractPrimaryKeyValue(data, config.getPrimaryKey());
            if (primaryKeyValue == null) {
                log.warn("无法从数据中提取主键值，跳过同步");
                return;
            }

            // 构建同步消息对象
            Map<String, Object> syncMessage = new HashMap<>();
            syncMessage.put("tableName", event.getTableName());
            syncMessage.put("primaryKey", primaryKeyValue);
            syncMessage.put("operationType", event.getOperationType().getCode());
            syncMessage.put("operationDesc", event.getOperationType().getDesc());
            syncMessage.put("syncTime", new Date());
            syncMessage.put("operator", event.getLoginUser().getUsername());
            syncMessage.put("businessCode", event.getBusinessCode());
            syncMessage.put("remark", event.getRemark());

            // 添加业务数据
            syncMessage.put("data", data);

            // 记录同步结果
            List<SyncRecord> syncRecords = new ArrayList<>();

            // 为每个目标系统发送消息
            for (SyncConfig.TargetSystemConfig targetConfig : targets) {
                try {
                    // 根据同步模式选择不同的同步方式
                    if (targetConfig.isHttpMode()) {
                        // HTTP接口方式同步
                        sendViaHttp(syncMessage, targetConfig, syncRecords, event, primaryKeyValue);
                    } else {
                        // MQ方式同步（默认）
                        sendViaMq(syncMessage, targetConfig, syncRecords, event, primaryKeyValue);
                    }
                } catch (Exception e) {
                    log.error("同步表 {} 的数据 {} 到系统 {} 失败",
                        event.getTableName(), primaryKeyValue, targetConfig.getSystemName(), e);

                    // 记录通用失败信息
                    SyncRecord record = createFailureRecord(event, primaryKeyValue, targetConfig, e.getMessage());
                    syncRecords.add(record);
                }
            }

            // 批量保存同步记录
            if (!syncRecords.isEmpty()) {
//                syncRecordService.saveBatch(syncRecords);
            }
        } catch (Exception e) {
            log.error("处理单条数据同步失败", e);
        }
    }

    /**
     * 通过MQ方式发送同步消息
     */
    private void sendViaMq(Map<String, Object> syncMessage, SyncConfig.TargetSystemConfig targetConfig,
                           List<SyncRecord> syncRecords, DataSyncEvent event, String primaryKeyValue) {
        try {
            // 序列化消息
            String messageBody = objectMapper.writeValueAsString(syncMessage);

            // 消息键（用于消息查询和幂等处理）
            String keys = event.getTableName() + "_" + primaryKeyValue + "_" + targetConfig.getSystemCode();

            // 设置属性（包含必要的消息追踪信息）
            Map<String, String> properties = new HashMap<>();
            properties.put("sourceSystem", "CMS"); // 源系统标识，根据实际情况修改
            properties.put("syncId", UUID.randomUUID().toString());
            properties.put("timestamp", String.valueOf(System.currentTimeMillis()));

            // 发送消息
            String messageId = "";
//            if (targetConfig.getTag() != null && !targetConfig.getTag().isEmpty()) {
//                messageId = mqAdapter.sendMessageWithProperties(targetConfig.getTopic(), targetConfig.getTag(), messageBody, keys, properties);
//            } else {
//                messageId = mqAdapter.sendMessageWithProperties(targetConfig.getTopic(), null, messageBody, keys, properties);
//            }

            // 记录成功
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
                .status(1) // 1-发送成功
                .syncTime(new Date())
                .operator(event.getLoginUser().getUsername())
                .businessCode(event.getBusinessCode())
                .remark(event.getRemark())
                .build();

            syncRecords.add(record);

            log.debug("成功通过MQ同步表 {} 的数据 {} 到系统 {}",
                event.getTableName(), primaryKeyValue, targetConfig.getSystemName());
        } catch (Exception e) {
            log.error("通过MQ同步表 {} 的数据 {} 到系统 {} 失败",
                event.getTableName(), primaryKeyValue, targetConfig.getSystemName(), e);
//            throw e;
        }
    }

    /**
     * 通过HTTP接口方式发送同步消息
     */
    private void sendViaHttp(Map<String, Object> syncMessage, SyncConfig.TargetSystemConfig targetConfig,
                             List<SyncRecord> syncRecords, DataSyncEvent event, String primaryKeyValue) {
        try {
            // 检查API URL是否配置
            if (targetConfig.getApiUrl() == null || targetConfig.getApiUrl().isEmpty()) {
                throw new RuntimeException("未配置API URL");
            }

            // 序列化消息
            String messageBody = objectMapper.writeValueAsString(syncMessage);

            // 准备HTTP请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            if (targetConfig.getApiToken() != null && !targetConfig.getApiToken().isEmpty()) {
                headers.put("Authorization", "Bearer " + targetConfig.getApiToken());
            }
            headers.put("X-Sync-Source", "CMS");
            headers.put("X-Sync-ID", UUID.randomUUID().toString());
            headers.put("X-Sync-Timestamp", String.valueOf(System.currentTimeMillis()));
            headers.put("X-Sync-Table", event.getTableName());
            headers.put("X-Sync-Operation", String.valueOf(event.getOperationType().getCode()));

            // 确定HTTP方法
            HttpMethod httpMethod = HttpMethod.POST; // 默认POST
            if (targetConfig.getApiMethod() != null) {
                try {
                    httpMethod = HttpMethod.valueOf(targetConfig.getApiMethod().toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.warn("不支持的HTTP方法: {}, 将使用POST", targetConfig.getApiMethod());
                }
            }

            // 超时时间（默认30秒）
            int timeout = targetConfig.getApiTimeout() != null ? targetConfig.getApiTimeout() : 30000;

            // 发送HTTP请求
            String response = "";//httpAdapter.sendRequest(targetConfig.getApiUrl(), httpMethod, messageBody, headers, timeout);

            // 记录成功
            SyncRecord record = SyncRecord.builder()
                .tableName(event.getTableName())
                .primaryKeyValue(primaryKeyValue)
                .operationType(event.getOperationType().getCode())
                .targetSystem(targetConfig.getSystemCode())
                .targetSystemName(targetConfig.getSystemName())
                .syncMode("HTTP")
                .apiUrl(targetConfig.getApiUrl())
                .apiMethod(targetConfig.getApiMethod())
                .apiResponse(response)
                .status(1) // 1-发送成功
                .syncTime(new Date())
                .operator(event.getLoginUser().getUsername())
                .businessCode(event.getBusinessCode())
                .remark(event.getRemark())
                .build();

            syncRecords.add(record);

            log.debug("成功通过HTTP接口同步表 {} 的数据 {} 到系统 {}",
                event.getTableName(), primaryKeyValue, targetConfig.getSystemName());
        } catch (Exception e) {
            log.error("通过HTTP接口同步表 {} 的数据 {} 到系统 {} 失败",
                event.getTableName(), primaryKeyValue, targetConfig.getSystemName(), e);
//            throw e;
        }
    }

    /**
     * 创建失败记录
     */
    private SyncRecord createFailureRecord(DataSyncEvent event, String primaryKeyValue,
                                           SyncConfig.TargetSystemConfig targetConfig, String failReason) {
        SyncRecord.SyncRecordBuilder builder = SyncRecord.builder()
            .tableName(event.getTableName())
            .primaryKeyValue(primaryKeyValue)
            .operationType(event.getOperationType().getCode())
            .targetSystem(targetConfig.getSystemCode())
            .targetSystemName(targetConfig.getSystemName())
            .status(0) // 0-发送失败
            .syncTime(new Date())
            .failReason(failReason)
            .operator(event.getLoginUser().getUsername())
            .businessCode(event.getBusinessCode())
            .remark(event.getRemark());

        // 根据同步模式设置相关字段
        if (targetConfig.isHttpMode()) {
            builder.syncMode("HTTP")
                .apiUrl(targetConfig.getApiUrl())
                .apiMethod(targetConfig.getApiMethod());
        } else {
            builder.syncMode("MQ")
                .mqType(targetConfig.getMqType())
                .topic(targetConfig.getTopic())
                .queue(targetConfig.getQueue());
        }

        return builder.build();
    }

    /**
     * 提取主键值
     */
    private String extractPrimaryKeyValue(Object data, String configKeyName) {
        try {
            // 使用Hutool的BeanUtil获取属性值（支持驼峰和下划线命名）
            Object value = BeanUtil.getProperty(data, configKeyName);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.error("获取主键值失败: {}", e.getMessage());
            return null;
        }
    }
}
