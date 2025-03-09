package com.sinosoft.notice.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.io.Serial;

/**
 * 通知合并详情对象 sys_notification_merge
 *
 * @author zzf
 * @date 2025-03-07
 */
@Data
@TableName("sys_notification_merge")
public class SysNotificationMerge implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 父通知ID
     */
    private Long parentId;

    /**
     * 子通知ID
     */
    private Long childId;

    /**
     * 合并时间
     */
    private Date mergedAt;

    /**
     * 显示顺序
     */
    private Integer displayOrder;


}
