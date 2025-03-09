package com.sinosoft.notice.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.io.Serial;

/**
 * 通知设置对象 sys_notification_setting
 *
 * @author zzf
 * @date 2025-03-07
 */
@Data
@TableName("sys_notification_setting")
public class SysNotificationSetting implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 待办通知-系统通知开关（0关闭 1开启）
     */
    private String todoNotifySystem;

    /**
     * 待办通知-短信通知开关（0关闭 1开启）
     */
    private String todoNotifySms;

    /**
     * 待办通知-邮件通知开关（0关闭 1开启）
     */
    private String todoNotifyEmail;

    /**
     * 预警通知-系统通知开关（0关闭 1开启）
     */
    private String alertNotifySystem;

    /**
     * 预警通知-短信通知开关（0关闭 1开启）
     */
    private String alertNotifySms;

    /**
     * 预警通知-邮件通知开关（0关闭 1开启）
     */
    private String alertNotifyEmail;

    /**
     * 公告通知-系统通知开关（0关闭 1开启）
     */
    private String announceNotifySystem;

    /**
     * 公告通知-邮件通知开关（0关闭 1开启）
     */
    private String announceNotifyEmail;

    /**
     * 免打扰开始时间
     */
    private Date doNotDisturbStart;

    /**
     * 免打扰结束时间
     */
    private Date doNotDisturbEnd;


    private Date createTime;

    private Date updateTime;
}
