package com.sinosoft.notice.model;

import com.alibaba.fastjson2.JSON;
import com.sinosoft.common.core.utils.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.*;

/**
 * 流程通知上下文
 * 封装流程通知所需的业务数据
 *
 * @author zzf
 */
@Data
@Accessors(chain = true)
public class FlowNotificationContext implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 流程节点ID
     */
    private Integer lcId;

    /**
     * 流程类型ID
     */
    private Long lcType;

    /**
     * 流程节点名称
     */
    private String lcName;

    /**
     * 流程类型名称
     */
    private String businessTypeName;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作时间
     */
    private Date operationTime = new Date();

    /**
     * 操作类型（1-提交，-1-退回，0-保存）
     */
    private Integer operationType;

    /**
     * 操作意见
     */
    private String opinion;

    /**
     * 主数据ID（当涉及批量数据时，选取第一条）
     */
    private String dataId;

    /**
     * 所有数据ID列表（批量场景）
     */
    private List<String> dataIds = new ArrayList<>();

    /**
     * 业务对象数据
     */
    private Map<String, Object> businessData = new HashMap<>();

    /**
     * 通知模板代码
     */
    private String templateCode;

    /**
     * 通知优先级
     */
    private String priority = "medium";

    /**
     * 通知合并策略
     */
    private String mergeStrategy;

    /**
     * 通知接收角色ID列表
     */
    private List<Long> roleIds;

    /**
     * 业务键模板
     */
    private String businessKeyTemplate;

    /**
     * 合并标题模板
     */
    private String mergeTitleTemplate;

    /**
     * 合并内容模板
     */
    private String mergeContentTemplate;

    /**
     * 批量数据统计信息
     */
    private Map<String, Object> batchStatistics = new HashMap<>();

    /**
     * 获取数据条数
     */
    public int getDataCount() {
        return dataIds.size();
    }

    /**
     * 是否为批量数据
     */
    public boolean isBatchData() {
        return dataIds.size() > 1;
    }

    /**
     * 添加业务数据
     *
     * @param key   键
     * @param value 值
     * @return 当前对象
     */
    public FlowNotificationContext addBusinessData(String key, Object value) {
        if (StringUtils.isNotEmpty(key) && value != null) {
            this.businessData.put(key, value);
        }
        return this;
    }

    /**
     * 批量添加业务数据
     *
     * @param data 业务数据
     * @return 当前对象
     */
    public FlowNotificationContext addAllBusinessData(Map<String, Object> data) {
        if (data != null && !data.isEmpty()) {
            this.businessData.putAll(data);
        }
        return this;
    }

    /**
     * 添加数据ID
     *
     * @param dataId 数据ID
     * @return 当前对象
     */
    public FlowNotificationContext addDataId(String dataId) {
        if (StringUtils.isNotEmpty(dataId)) {
            this.dataIds.add(dataId);

            // 如果是第一条数据，同时设置为主数据ID
            if (this.dataIds.size() == 1) {
                this.dataId = dataId;
            }
        }
        return this;
    }

    /**
     * 批量添加数据ID
     *
     * @param dataIds 数据ID列表
     * @return 当前对象
     */
    public FlowNotificationContext addAllDataIds(List<String> dataIds) {
        if (dataIds != null && !dataIds.isEmpty()) {
            this.dataIds.addAll(dataIds);

            // 如果是第一批数据，设置第一条为主数据ID
            if (this.dataId == null && !this.dataIds.isEmpty()) {
                this.dataId = this.dataIds.get(0);
            }
        }
        return this;
    }

    /**
     * 添加批量统计信息
     *
     * @param key   键
     * @param value 值
     * @return 当前对象
     */
    public FlowNotificationContext addBatchStatistic(String key, Object value) {
        if (StringUtils.isNotEmpty(key) && value != null) {
            this.batchStatistics.put(key, value);
        }
        return this;
    }

    /**
     * 批量添加统计信息
     *
     * @param statistics 统计信息
     * @return 当前对象
     */
    public FlowNotificationContext addAllBatchStatistics(Map<String, Object> statistics) {
        if (statistics != null && !statistics.isEmpty()) {
            this.batchStatistics.putAll(statistics);
        }
        return this;
    }

    /**
     * 生成业务键
     *
     * @return 业务键
     */
    public String generateBusinessKey() {
        if (StringUtils.isEmpty(businessKeyTemplate)) {
            // 使用默认业务键模板
            return String.format("flow:%s:%s:%s", lcType, dataIds.isEmpty() ? "0" : lcId,
                isBatchData() ? "batch" : dataId);
        }

        try {
            String template = businessKeyTemplate;

            // 替换变量
            template = template.replace("{lcType}", String.valueOf(lcType));
            template = template.replace("{lcId}", String.valueOf(lcId));
            template = template.replace("{dataId}", dataId);

            if (StringUtils.isNotEmpty(businessTypeName)) {
                template = template.replace("{businessTypeName}", businessTypeName);
            }

            return template;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 转换为Map对象
     *
     * @return Map对象
     */
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>(businessData);

        // 添加基本信息
        result.put("lcId", lcId);
        result.put("lcType", lcType);
        result.put("operator", operator);
        result.put("operationTime", operationTime);
        result.put("operationType", operationType);

        if (StringUtils.isNotEmpty(lcName)) {
            result.put("lcName", lcName);
        }

        if (StringUtils.isNotEmpty(businessTypeName)) {
            result.put("businessTypeName", businessTypeName);
        }

        if (StringUtils.isNotEmpty(opinion)) {
            result.put("opinion", opinion);
        }

        if (StringUtils.isNotEmpty(dataId)) {
            result.put("dataId", dataId);
        }

        if (!dataIds.isEmpty()) {
            result.put("dataCount", dataIds.size());
            result.put("allDataIds", dataIds);
        }

        // 添加批量统计信息
        if (!batchStatistics.isEmpty()) {
            result.putAll(batchStatistics);
        }

        return result;
    }

    /**
     * 转换为通知业务负载对象
     *
     * @return 通知业务负载对象
     */
    public NotificationPayload toNotificationPayload() {
        NotificationPayload payload = new NotificationPayload();

        // 设置业务信息
        payload.setBusinessId(dataId)
            .setBusinessName(lcName)
            .setBusinessType(businessTypeName)
            .setSender(operator)
            .setBusinessTime(operationTime)
            .setMergeStrategy(mergeStrategy)
            .setBusinessKeyTemplate(businessKeyTemplate)
            .setMergeTitleTemplate(mergeTitleTemplate)
            .setMergeContentTemplate(mergeContentTemplate);

        // 添加标题参数
        payload.addTitleParam("lcId", lcId)
            .addTitleParam("lcName", lcName)
            .addTitleParam("businessTypeName", businessTypeName)
            .addTitleParam("dataId", dataId)
            .addTitleParam("dataCount", getDataCount());

        // 添加内容参数
        payload.addContentParam("lcId", lcId)
            .addContentParam("lcName", lcName)
            .addContentParam("businessTypeName", businessTypeName)
            .addContentParam("dataId", dataId)
            .addContentParam("dataCount", getDataCount())
            .addContentParam("operator", operator)
            .addContentParam("operationTime", operationTime);

        if (StringUtils.isNotEmpty(opinion)) {
            payload.addContentParam("opinion", opinion);
        }

        // 添加业务数据
        payload.addAllBusinessData(businessData);

        // 添加批量统计数据
        if (!batchStatistics.isEmpty()) {
            payload.addAllBatchStatistics(batchStatistics);
        }

        // 设置批量数据
        if (isBatchData()) {
            payload.addAllBatchDataIds(dataIds);
        }

        // 如果是紧急通知，设置紧急标志
        if (priority != null && "high".equals(priority)) {
            payload.setUrgent(true);
        }

        // 设置操作
        List<NotificationAction> actions = new ArrayList<>();
        if (!isBatchData()) {
            // 单条数据，添加查看操作
            NotificationAction viewAction = new NotificationAction()
                .setId("view")
                .setName("查看详情")
                .setType("link")
                .setUrl("/workflow/detail?dataId=" + dataId + "&lcType=" + lcType);
            actions.add(viewAction);
        } else {
            // 多条数据，添加查看列表操作
            NotificationAction listAction = new NotificationAction()
                .setId("list")
                .setName("查看列表")
                .setType("link")
                .setUrl("/workflow/list?lcType=" + lcType);
            actions.add(listAction);
        }

        // 添加操作
        for (NotificationAction action : actions) {
            payload.addAction(action);
        }

        return payload;
    }

    /**
     * 从Map创建通知上下文
     *
     * @param map 数据Map
     * @return 通知上下文
     */
    public static FlowNotificationContext fromMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return new FlowNotificationContext();
        }

        FlowNotificationContext context = new FlowNotificationContext();

        // 设置基本信息
        if (map.containsKey("lcId")) {
            context.setLcId((Integer) map.get("lcId"));
        }

        if (map.containsKey("lcType")) {
            Object lcType = map.get("lcType");
            if (lcType instanceof Integer) {
                context.setLcType(((Integer) lcType).longValue());
            } else if (lcType instanceof Long) {
                context.setLcType((Long) lcType);
            }
        }

        if (map.containsKey("lcName")) {
            context.setLcName((String) map.get("lcName"));
        }

        if (map.containsKey("businessTypeName")) {
            context.setBusinessTypeName((String) map.get("businessTypeName"));
        }

        if (map.containsKey("operator")) {
            context.setOperator((String) map.get("operator"));
        }

        if (map.containsKey("operationTime")) {
            context.setOperationTime((Date) map.get("operationTime"));
        }

        if (map.containsKey("operationType")) {
            context.setOperationType((Integer) map.get("operationType"));
        }

        if (map.containsKey("opinion")) {
            context.setOpinion((String) map.get("opinion"));
        }

        if (map.containsKey("dataId")) {
            context.setDataId((String) map.get("dataId"));
        }

        if (map.containsKey("allDataIds")) {
            Object dataIds = map.get("allDataIds");
            if (dataIds instanceof List) {
                context.setDataIds((List<String>) dataIds);
            }
        }

        // 复制所有业务数据
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // 跳过已处理的基本字段
            if (!Arrays.asList("lcId", "lcType", "lcName", "businessTypeName",
                "operator", "operationTime", "operationType",
                "opinion", "dataId", "allDataIds", "dataCount").contains(entry.getKey())) {
                context.addBusinessData(entry.getKey(), entry.getValue());
            }
        }

        return context;
    }

    /**
     * 生成简单摘要
     *
     * @return 简单摘要
     */
    public String generateSummary() {
        StringBuilder summary = new StringBuilder();

        if (StringUtils.isNotEmpty(businessTypeName)) {
            summary.append(businessTypeName);
        } else {
            summary.append("流程数据");
        }

        if (isBatchData()) {
            summary.append(" ").append(getDataCount()).append("条");
        }

        return summary.toString();
    }

    /**
     * 转为JSON
     */
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
