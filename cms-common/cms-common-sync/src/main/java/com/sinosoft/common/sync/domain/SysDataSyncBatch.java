package com.sinosoft.common.sync.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 数据同步批次对象 sys_data_sync_batch
 *
 * @author zzf
 * @date 2025-03-28
 */
@Data
@TableName("sys_data_sync_batch")
public class SysDataSyncBatch  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 批次ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 操作类型：1-新增，2-修改，3-删除
     */
    private Long operationType;

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

    /**
     * MQ类型
     */
    private String mqType;

    /**
     * 主题/交换机
     */
    private String topic;

    /**
     * 队列/路由键
     */
    private String queue;

    /**
     * API接口URL
     */
    private String apiUrl;

    /**
     * API接口方法
     */
    private String apiMethod;

    /**
     * API响应结果
     */
    private String apiResponse;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 同步记录总数
     */
    private Long totalCount;

    /**
     * 成功记录数
     */
    private Long successCount;

    /**
     * 失败记录数
     */
    private Long failCount;

    /**
     * 批次状态：0-发送失败，1-发送成功，2-接收方确认成功，3-接收方处理失败
     */
    private Long status;

    /**
     * 同步时间
     */
    private Date syncTime;

    /**
     * 反馈时间
     */
    private Date feedbackTime;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 业务编码
     */
    private String businessCode;

    /**
     * 备注
     */
    private String remark;


}
