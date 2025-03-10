package com.sinosoft.notice.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

import java.io.Serial;

/**
 * 通知规则对象 sys_notification_rule
 *
 * @author zzf
 * @date 2025-03-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notification_rule")
public class SysNotificationRule extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 规则ID
     */
    @TableId(value = "rule_id")
    private Long ruleId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 关联的模板ID
     */
    private Long templateId;

    /**
     * 关联菜单ID
     */
    private Long menuId;

    /**
     * 菜单URL
     */
    private String menuUrl;

    /**
     * 是否为业务待办（0否1是）
     */
    private String todoType;

    /**
     * 规则类型：sql-自定义SQL, bean-自定义Bean
     */
    private String ruleType;

    /**
     * 规则结果文本
     */
    private String ruleText;

    /**
     * 通知渠道，JSON数组["system","sms","email"]
     */
    private String channels;

    /**
     * 状态（0启用 1停用）
     */
    private String status;

    /**
     * 上次执行时间
     */
    private Date lastExecTime;

    /**
     * 备注
     */
    private String remark;
}
