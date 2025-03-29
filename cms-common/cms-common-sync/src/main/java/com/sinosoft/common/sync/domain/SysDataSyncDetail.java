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
 * 数据同步详情对象 sys_data_sync_detail
 *
 * @author zzf
 * @date 2025-03-28
 */
@Data
@TableName("sys_data_sync_detail")
public class SysDataSyncDetail  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 批次ID
     */
    private Long batchId;

    /**
     * 主键值
     */
    private String primaryKeyValue;

    /**
     * 同步状态：0-发送失败，1-成功
     */
    private Long status;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 同步时间
     */
    private Date syncTime;


}
