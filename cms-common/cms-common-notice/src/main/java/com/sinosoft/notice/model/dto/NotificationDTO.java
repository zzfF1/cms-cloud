package com.sinosoft.notice.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * 通知结果DTO
 */
@Data
public class NotificationDTO {
    /** 通知ID */
    private Long notificationId;

    /** 通知类型 */
    private String type;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 是否已读 */
    private Boolean isRead;

    /** 阅读时间 */
    private Date readTime;

    /** 发送时间 */
    private Date createTime;

    /** 优先级 */
    private String priority;

    /** 来源类型 */
    private String sourceType;

    /** 来源ID */
    private String sourceId;

    /** 附件信息 */
    private String attachments;

    /** 业务数据 */
    private String businessData;

    /** 消息子类型 */
    private String messageSubtype;
}
