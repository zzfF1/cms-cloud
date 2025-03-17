package com.sinosoft.common.sync.util;

import com.sinosoft.common.core.utils.SpringUtils;
import com.sinosoft.common.sync.enums.OperationType;
import com.sinosoft.common.sync.event.DataSyncEvent;
import com.sinosoft.system.api.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 数据同步助手
 * <p>
 * 提供给业务服务使用，用于发布数据同步事件
 */
@Slf4j
@Component
public class DataSyncHelper {
    /**
     * 发布数据新增同步事件
     *
     * @param tableName 表名
     * @param data      数据对象
     * @param loginUser 操作人
     */
    public void publishInsertEvent(String tableName, Object data, LoginUser loginUser) {
        publishSyncEvent(tableName, Collections.singletonList(data), null, OperationType.INSERT, loginUser, null, null);
    }

    /**
     * 发布数据修改同步事件
     *
     * @param tableName 表名
     * @param data      数据对象
     * @param loginUser 操作人
     */
    public void publishUpdateEvent(String tableName, Object data, LoginUser loginUser) {
        publishSyncEvent(tableName, Collections.singletonList(data), null, OperationType.UPDATE, loginUser, null, null);
    }

    /**
     * 发布数据删除同步事件
     *
     * @param tableName 表名
     * @param data      数据对象
     * @param loginUser 操作人
     */
    public void publishDeleteEvent(String tableName, Object data, LoginUser loginUser) {
        publishSyncEvent(tableName, Collections.singletonList(data), null, OperationType.DELETE, loginUser, null, null);
    }

    /**
     * 发布批量数据同步事件
     *
     * @param tableName     表名
     * @param dataList      数据对象列表
     * @param operationType 操作类型
     * @param loginUser     操作人
     */
    public void publishBatchEvent(String tableName, List<?> dataList, OperationType operationType, LoginUser loginUser) {
        publishSyncEvent(tableName, dataList, null, operationType, loginUser, null, null);
    }

    /**
     * 发布完整的数据同步事件
     *
     * @param tableName      表名
     * @param dataList       数据对象列表
     * @param primaryKeyName 主键字段名（如果为null，将使用配置中的主键名）
     * @param operationType  操作类型
     * @param loginUser      操作人
     * @param businessCode   业务编码
     * @param remark         备注
     */
    public void publishSyncEvent(String tableName, List<?> dataList, String primaryKeyName, OperationType operationType, LoginUser loginUser, String businessCode, String remark) {
        try {
            // 构建事件对象
            DataSyncEvent event = DataSyncEvent.builder()
                .tableName(tableName).operationType(operationType).primaryKeyName(primaryKeyName)
                .dataList(dataList).loginUser(loginUser).businessCode(businessCode)
                .remark(remark).build();
            // 发布事件
            SpringUtils.context().publishEvent(event);
            log.debug("已发布数据同步事件: 表={}, 操作={}, 数据数量={}", tableName, operationType.getDesc(), dataList.size());
        } catch (Exception e) {
            log.error("发布数据同步事件失败", e);
        }
    }
}
