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
     * 转为JSON
     */
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
