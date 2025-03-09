package com.sinosoft.notice.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.io.Serial;

/**
 * 通知接收人对象 sys_notification_user
 *
 * @author zzf
 * @date 2025-03-07
 */
@Data
@TableName("sys_notification_user")
public class SysNotificationUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 通知ID
     */
    private Long notificationId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 是否已读（0未读 1已读）
     */
    private String isRead;

    /**
     * 阅读时间
     */
    private Date readTime;

    /**
     * 是否已处理（0未处理 1已处理）
     */
    private String isProcessed;

    /**
     * 处理时间
     */
    private Date processTime;

    /**
     * 处理备注
     */
    private String processNote;


    private Date createTime;

    private Date updateTime;

}
