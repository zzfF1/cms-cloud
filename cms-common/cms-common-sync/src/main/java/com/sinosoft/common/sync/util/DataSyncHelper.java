package com.sinosoft.common.sync.util;

import com.sinosoft.common.core.utils.SpringUtils;
import com.sinosoft.common.sync.config.SyncConfigManager;
import com.sinosoft.common.sync.enums.OperationType;
import com.sinosoft.common.sync.event.DataSyncEvent;
import com.sinosoft.system.api.model.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 同步助手
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSyncHelper {

    private final SyncConfigManager configManager;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 发布数据同步事件 - 使用Class
     */
    public <T> void publishSyncEvent(Class<T> entityClass, List<T> dataList, OperationType operationType, LoginUser loginUser) {
        if (dataList == null || dataList.isEmpty()) {
            log.warn("发布同步事件时数据列表为空");
            return;
        }
        // 获取表名和主键名称
        String tableName = configManager.getTableNameForEntity(entityClass);
        if (tableName == null) {
            log.error("实体类 {} 对应的表没有同步配置，跳过同步", entityClass.getName());
            return;
        }
        String primaryKeyName = configManager.getPrimaryKeyForEntity(entityClass);
        // 发布同步事件
        publishSyncEvent(tableName, dataList, primaryKeyName, operationType, loginUser);
    }

    /**
     * 发布单个数据同步事件 - 使用Class
     */
    public <T> void publishSyncEvent(Class<T> entityClass, T data, OperationType operationType, LoginUser loginUser) {
        publishSyncEvent(entityClass, Collections.singletonList(data),
            operationType, loginUser);
    }

    /**
     * 发布数据同步事件 - 自动推断类型
     */
    public <T> void publishSyncEvent(T data, OperationType operationType, LoginUser loginUser) {
        if (data == null) {
            log.warn("发布同步事件时数据为空");
            return;
        }
        @SuppressWarnings("unchecked")
        Class<T> entityClass = (Class<T>) data.getClass();
        publishSyncEvent(entityClass, data, operationType, loginUser);
    }

    /**
     * 批量发布数据同步事件 - 自动推断类型
     */
    public <T> void publishSyncEvents(List<T> dataList, OperationType operationType, LoginUser loginUser) {
        if (dataList == null || dataList.isEmpty()) {
            log.warn("发布同步事件时数据列表为空");
            return;
        }
        @SuppressWarnings("unchecked")
        Class<T> entityClass = (Class<T>) dataList.get(0).getClass();
        publishSyncEvent(entityClass, dataList, operationType, loginUser);
    }

    /**
     * 原始方法 - 保留兼容性
     */
    protected void publishSyncEvent(String tableName, List<?> dataList, String primaryKeyName, OperationType operationType, LoginUser loginUser) {
        try {
            // 构建事件对象
            DataSyncEvent event = DataSyncEvent.builder().tableName(tableName).operationType(operationType).primaryKeyName(primaryKeyName).
                dataList(dataList).loginUser(loginUser).businessCode("").remark("").build();
            // 发布事件
            eventPublisher.publishEvent(event);
            log.debug("已发布数据同步事件: 表={}, 操作={}, 数据数量={}",
                tableName, operationType.getDesc(), dataList.size());
        } catch (Exception e) {
            log.error("发布数据同步事件失败", e);
        }
    }
}
