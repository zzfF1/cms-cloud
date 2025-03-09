package com.sinosoft.notice.core.aggregation;

import lombok.Data;
import java.util.List;

/**
 * 通知聚合规则
 */
@Data
public class NotificationAggregationRule {

    /**
     * 聚合规则ID
     */
    private String ruleId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 通知类型列表（为空表示所有类型）
     */
    private List<String> notificationTypes;

    /**
     * 来源类型列表（为空表示所有来源）
     */
    private List<String> sourceTypes;

    /**
     * 最小聚合数量（达到该数量触发聚合）
     */
    private Integer minAggregationCount;

    /**
     * 最大聚合数量（超过该数量将分批聚合）
     */
    private Integer maxAggregationCount;

    /**
     * 聚合时间窗口（分钟）
     */
    private Integer timeWindowMinutes;

    /**
     * 聚合标题模板
     */
    private String aggregatedTitleTemplate;

    /**
     * 聚合内容模板
     */
    private String aggregatedContentTemplate;

    /**
     * 开关状态（是否启用）
     */
    private Boolean enabled;
}
