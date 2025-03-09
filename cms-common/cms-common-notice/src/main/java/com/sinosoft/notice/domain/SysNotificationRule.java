package com.sinosoft.notice.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

import java.io.Serial;

/**
 * 通知规则对象 sys_notification_rule
 *
 * @author zzf
 * @date 2025-03-07
 */
@Data
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
     * 规则类型：schedule-定时, event-事件, condition-条件
     */
    private String ruleType;

    /**
     * 关联的模板ID
     */
    private Long templateId;

    /**
     * 触发条件，JSON格式或表达式
     */
    private String triggerCondition;

    /**
     * CRON表达式，用于定时规则
     */
    private String triggerSchedule;

    /**
     * 数据源配置，JSON格式
     */
    private String dataSource;

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
