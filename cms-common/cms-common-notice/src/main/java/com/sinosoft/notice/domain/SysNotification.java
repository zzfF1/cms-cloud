package com.sinosoft.notice.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

import java.io.Serial;

/**
 * 通知主对象 sys_notification (简化版)
 */
@Data
@TableName("sys_notification")
public class SysNotification extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    @TableId(value = "notification_id")
    private Long notificationId;

    /**
     * 关联的模板ID
     */
    private Long templateId;

    /**
     * 通知类型：todo-待办, alert-预警, announcement-公告
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
     * 通知来源类型，如contract, policy, performance等
     */
    private String sourceType;

    /**
     * 通知来源ID，关联到业务ID
     */
    private String sourceId;

    /**
     * 优先级：high-高, medium-中, low-低
     */
    private String priority;

    /**
     * 指定接收角色ID列表，JSON数组格式
     */
    private String roleIds;

    /**
     * 数据范围SQL条件
     */
    private String dataScopeSql;

    /**
     * 所需菜单权限，JSON数组格式
     */
    private String menuPerms;

    /**
     * 业务键，用于识别重复通知
     */
    private String businessKey;

    /**
     * 过期时间
     */
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
     * 业务数据，JSON格式，用于动态展示或操作
     */
    private String businessData;

    /**
     * 通知状态（0正常 1过期 2取消）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;
}
