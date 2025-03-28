package com.sinosoft.common.sync.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 同步结果反馈模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncFeedback implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 原消息ID
     */
    private String originalMessageId;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 操作类型代码
     */
    private Integer operationType;

    /**
     * 主键值
     */
    private String primaryKeyValue;

    /**
     * 来源系统
     */
    private String sourceSystem;

    /**
     * 处理结果（true-成功，false-失败）
     */
    private Boolean success;

    /**
     * 处理时间
     */
    private Date processTime;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 目标系统代码
     */
    private String targetSystem;

    /**
     * 业务编码
     */
    private String businessCode;

    /**
     * 扩展信息
     */
    private Map<String, Object> extensions;
}
