package com.sinosoft.notice.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.io.Serial;

/**
 * 通知发送记录对象 sys_notification_delivery
 *
 * @author zzf
 * @date 2025-03-07
 */
@Data
@TableName("sys_notification_delivery")
public class SysNotificationDelivery implements Serializable {

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
     * 发送渠道：system-系统内通知, sms-短信, email-邮件
     */
    private String channel;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 发送状态：pending-待发送, sent-已发送, failed-发送失败
     */
    private String status;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最后重试时间
     */
    private Date lastRetryTime;

    /**
     * 目标地址（手机号或邮箱）
     */
    private String targetAddress;

    private Date createTime;


}
