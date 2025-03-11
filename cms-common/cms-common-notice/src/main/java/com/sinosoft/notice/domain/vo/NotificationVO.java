package com.sinosoft.notice.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import com.sinosoft.notice.domain.SysNotification;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 通知视图对象
 *
 * @author zzf
 */
@Data
@AutoMapper(target = SysNotification.class)
public class NotificationVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    private Long notificationId;

    /**
     * 关联的模板ID
     */
    private Long templateId;

    /**
     * 通知类型：todo-待办, alert-预警, announcement-公告, message-消息
     */
    private String type;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知来源类型
     */
    private String sourceType;

    /**
     * 通知来源ID
     */
    private String sourceId;

    /**
     * 优先级：high-高, medium-中, low-低
     */
    private String priority;

    /**
     * 是否已读
     */
    private Boolean read;

    /**
     * 阅读时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expirationDate;

    /**
     * 可执行操作，JSON格式
     */
    private String actions;

    /**
     * 附件信息，JSON格式
     */
    private String attachments;

    /**
     * 业务数据，JSON格式
     */
    private String businessData;

    /**
     * 通知状态（0正常 1过期 2取消）
     */
    private String status;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;
}
