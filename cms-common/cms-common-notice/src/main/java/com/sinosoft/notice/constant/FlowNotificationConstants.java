package com.sinosoft.notice.constant;

/**
 * 流程通知常量
 *
 * @author zzf
 */
public class FlowNotificationConstants {
    /**
     * 流程节点属性：是否需要通知
     */
    public static final String PROP_NOTIFY_EXISTS = "notify_exists";

    /**
     * 流程节点属性：通知模板代码
     */
    public static final String PROP_NOTIFY_TEMPLATE = "notify_template";

    /**
     * 流程节点属性：通知合并策略
     * 可选值：none-不合并, update-更新已有, count-计数合并, list-列表合并
     */
    public static final String PROP_NOTIFY_MERGE_STRATEGY = "notify_merge_strategy";

    /**
     * 流程节点属性：通知优先级
     * 可选值：high-高, medium-中, low-低
     */
    public static final String PROP_NOTIFY_PRIORITY = "notify_priority";

    /**
     * 流程节点属性：通知通道
     * 格式：JSON数组 ["system","sms","email"]
     */
    public static final String PROP_NOTIFY_CHANNELS = "notify_channels";

    /**
     * 流程节点属性：通知接收角色
     * 格式：JSON数组 [1,2,3]
     */
    public static final String PROP_NOTIFY_ROLES = "notify_roles";

    /**
     * 流程节点属性：通知接收权限
     * 格式：JSON数组 ["system:user:list","system:role:list"]
     */
    public static final String PROP_NOTIFY_PERMS = "notify_perms";

    /**
     * 流程节点属性：通知业务键模板
     * 用于合并判断，例如："flow:{lcType}:{lcId}"
     */
    public static final String PROP_NOTIFY_BUSINESS_KEY = "notify_business_key";

    /**
     * 流程节点属性：通知有效天数
     */
    public static final String PROP_NOTIFY_VALID_DAYS = "notify_valid_days";

    /**
     * 流程通知来源类型
     */
    public static final String SOURCE_TYPE_WORKFLOW = "workflow";

    /**
     * 默认通知模板：流程审批待办
     */
    public static final String DEFAULT_TEMPLATE_TODO = "flow_todo";

    /**
     * 默认通知模板：流程审批完成
     */
    public static final String DEFAULT_TEMPLATE_DONE = "flow_done";
}
