package com.sinosoft.notice.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import lombok.Data;

import java.io.Serial;

/**
 * 通知模板对象 sys_notification_template
 *
 * @author zzf
 * @date 2025-03-07
 */
@Data
@TableName("sys_notification_template")
public class SysNotificationTemplate extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    @TableId(value = "template_id")
    private Long templateId;

    /**
     * 模板代码，唯一标识
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 通知类型：todo-待办, alert-预警, announcement-公告
     */
    private String type;

    /**
     * 标题模板，支持变量占位符
     */
    private String titleTemplate;

    /**
     * 内容模板，支持变量占位符
     */
    private String contentTemplate;

    /**
     * 短信模板，支持变量占位符
     */
    private String smsTemplate;

    /**
     * 邮件主题模板
     */
    private String emailSubject;

    /**
     * 邮件内容模板，支持HTML
     */
    private String emailContent;

    /**
     * 优先级：high-高, medium-中, low-低
     */
    private String priority;

    /**
     * 角色ID列表，JSON数组格式
     */
    private String roleIds;

    /**
     * 菜单权限列表，JSON数组格式
     */
    private String menuPerms;

    /**
     * 数据权限条件，JSON格式
     */
    private String dataScope;

    /**
     * 通知有效天数
     */
    private Integer validDays;

    /**
     * 可执行操作，JSON格式
     */
    private String actions;

    /**
     * 合并策略: none-不合并, update-更新已有, count-计数合并, list-列表合并
     */
    private String mergeStrategy;

    /**
     * 业务键模板，用于识别重复通知
     */
    private String businessKeyTpl;

    /**
     * 合并后的标题模板
     */
    private String mergeTitleTpl;

    /**
     * 合并后的内容模板
     */
    private String mergeContentTpl;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;


}
