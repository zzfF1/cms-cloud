package com.sinosoft.common.sync.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 数据同步记录
 */
@Data
@Builder
@TableName("sys_data_sync_record")
public class SyncRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 主键值
     */
    private String primaryKeyValue;

    /**
     * 操作类型
     */
    private Integer operationType;

    /**
     * 目标系统代码
     */
    private String targetSystem;

    /**
     * 目标系统名称
     */
    private String targetSystemName;

    /**
     * 同步模式（MQ或HTTP）
     */
    private String syncMode;

    // ========== MQ相关字段 ==========

    /**
     * MQ类型
     */
    private String mqType;

    /**
     * 主题
     */
    private String topic;

    /**
     * 队列
     */
    private String queue;

    /**
     * 消息ID
     */
    private String messageId;

    // ========== HTTP接口相关字段 ==========

    /**
     * API接口URL
     */
    private String apiUrl;

    /**
     * API接口方法
     */
    private String apiMethod;

    /**
     * API响应结果（可选）
     */
    private String apiResponse;

    // ========== 共用字段 ==========

    /**
     * 同步状态
     * 0-发送失败，1-发送成功，2-接收方确认成功，3-接收方处理失败
     */
    private Integer status;

    /**
     * 同步时间
     */
    private Date syncTime;

    /**
     * 反馈时间
     */
    private Date feedbackTime;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 业务编码
     */
    private String businessCode;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
