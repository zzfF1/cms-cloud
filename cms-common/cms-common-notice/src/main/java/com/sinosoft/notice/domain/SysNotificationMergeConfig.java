package com.sinosoft.notice.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 通知合并配置对象 sys_notification_merge_config
 *
 * @author zzf
 * @date 2025-03-07
 */
@Data
@TableName("sys_notification_merge_config")
public class SysNotificationMergeConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 模板代码
     */
    private String templateCode;

    /**
     * 合并时间窗口（小时）
     */
    private Integer mergeWithinHours;

    /**
     * 最大合并数量
     */
    private Integer maxMergeCount;

    /**
     * 是否按用户合并（0否 1是）
     */
    private String mergeByUser;

    private Date createTime;

    private Date updateTime;

}
