package com.sinosoft.common.sync.event;

import com.sinosoft.common.sync.enums.OperationType;
import com.sinosoft.system.api.model.LoginUser;
import lombok.Builder;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;
import java.util.List;

/**
 * 数据同步事件（泛型版）
 * <p>
 * 用于在事务提交后触发数据同步
 */
@Data
@Builder
public class DataSyncEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 操作类型：新增、修改、删除
     */
    private OperationType operationType;

    /**
     * 主键字段名
     */
    private String primaryKeyName;

    /**
     * 同步数据列表
     */
    private List<?> dataList;

    /**
     * 操作人
     */
    private LoginUser loginUser;


    /**
     * 业务编码（可选，用于标识特定业务场景）
     */
    private String businessCode;

    /**
     * 备注说明
     */
    private String remark;
}
