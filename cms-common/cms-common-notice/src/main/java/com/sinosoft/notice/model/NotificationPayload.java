package com.sinosoft.notice.model;

import com.sinosoft.common.core.utils.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.*;

/**
 * 通知业务负载对象
 * 封装通知相关的所有业务数据
 *
 * @author zzf
 */
@Data
@Accessors(chain = true)
public class NotificationPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 通知标题参数
     */
    private Map<String, Object> titleParams = new HashMap<>();

    /**
     * 通知内容参数
     */
    private Map<String, Object> contentParams = new HashMap<>();

    /**
     * 业务数据
     */
    private Map<String, Object> businessData = new HashMap<>();

    /**
     * 通知发送人
     */
    private String sender;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 业务编码
     */
    private String businessCode;

    /**
     * 业务名称
     */
    private String businessName;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务时间
     */
    private Date businessTime;

    /**
     * 业务状态
     */
    private String businessStatus;

    /**
     * 业务URL
     */
    private String businessUrl;

    /**
     * 是否紧急
     */
    private Boolean urgent;

    /**
     * 附件信息
     */
    private List<NotificationAttachment> attachments = new ArrayList<>();

    /**
     * 操作信息
     */
    private List<NotificationAction> actions = new ArrayList<>();

    /**
     * 自定义参数
     */
    private Map<String, Object> customParams = new HashMap<>();

    /**
     * 合并策略
     */
    private String mergeStrategy;

    /**
     * 业务键模板
     */
    private String businessKeyTemplate;

    /**
     * 业务键
     */
    private String businessKey;

    /**
     * 合并标题模板
     */
    private String mergeTitleTemplate;

    /**
     * 合并内容模板
     */
    private String mergeContentTemplate;

    /**
     * 批量数据ID列表
     */
    private List<String> batchDataIds = new ArrayList<>();

    /**
     * 批量数据摘要信息
     */
    private List<Map<String, Object>> batchDataSummary = new ArrayList<>();

    /**
     * 批量数据统计信息
     */
    private Map<String, Object> batchStatistics = new HashMap<>();

    /**
     * 添加标题参数
     *
     * @param key   键
     * @param value 值
     * @return 当前对象
     */
    public NotificationPayload addTitleParam(String key, Object value) {
        if (StringUtils.isNotEmpty(key) && value != null) {
            this.titleParams.put(key, value);
        }
        return this;
    }

    /**
     * 添加内容参数
     *
     * @param key   键
     * @param value 值
     * @return 当前对象
     */
    public NotificationPayload addContentParam(String key, Object value) {
        if (StringUtils.isNotEmpty(key) && value != null) {
            this.contentParams.put(key, value);
        }
        return this;
    }

    /**
     * 添加业务数据
     *
     * @param key   键
     * @param value 值
     * @return 当前对象
     */
    public NotificationPayload addBusinessData(String key, Object value) {
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
    public NotificationPayload addAllBusinessData(Map<String, Object> data) {
        if (data != null && !data.isEmpty()) {
            this.businessData.putAll(data);
        }
        return this;
    }

    /**
     * 添加附件
     *
     * @param attachment 附件
     * @return 当前对象
     */
    public NotificationPayload addAttachment(NotificationAttachment attachment) {
        if (attachment != null) {
            this.attachments.add(attachment);
        }
        return this;
    }

    /**
     * 添加操作
     *
     * @param action 操作
     * @return 当前对象
     */
    public NotificationPayload addAction(NotificationAction action) {
        if (action != null) {
            this.actions.add(action);
        }
        return this;
    }

    /**
     * 添加自定义参数
     *
     * @param key   键
     * @param value 值
     * @return 当前对象
     */
    public NotificationPayload addCustomParam(String key, Object value) {
        if (StringUtils.isNotEmpty(key) && value != null) {
            this.customParams.put(key, value);
        }
        return this;
    }

    /**
     * 批量添加自定义参数
     *
     * @param params 参数
     * @return 当前对象
     */
    public NotificationPayload addAllCustomParams(Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            this.customParams.putAll(params);
        }
        return this;
    }

    /**
     * 添加批量数据ID
     *
     * @param dataId 数据ID
     * @return 当前对象
     */
    public NotificationPayload addBatchDataId(String dataId) {
        if (StringUtils.isNotEmpty(dataId)) {
            this.batchDataIds.add(dataId);
        }
        return this;
    }

    /**
     * 批量添加数据ID
     *
     * @param dataIds 数据ID列表
     * @return 当前对象
     */
    public NotificationPayload addAllBatchDataIds(List<String> dataIds) {
        if (dataIds != null && !dataIds.isEmpty()) {
            this.batchDataIds.addAll(dataIds);
        }
        return this;
    }

    /**
     * 添加批量数据摘要
     *
     * @param dataSummary 数据摘要
     * @return 当前对象
     */
    public NotificationPayload addBatchDataSummary(Map<String, Object> dataSummary) {
        if (dataSummary != null && !dataSummary.isEmpty()) {
            this.batchDataSummary.add(dataSummary);
        }
        return this;
    }

    /**
     * 批量添加数据摘要
     *
     * @param dataSummaryList 数据摘要列表
     * @return 当前对象
     */
    public NotificationPayload addAllBatchDataSummary(List<Map<String, Object>> dataSummaryList) {
        if (dataSummaryList != null && !dataSummaryList.isEmpty()) {
            this.batchDataSummary.addAll(dataSummaryList);
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
    public NotificationPayload addBatchStatistic(String key, Object value) {
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
    public NotificationPayload addAllBatchStatistics(Map<String, Object> statistics) {
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
        if (StringUtils.isNotEmpty(businessKey)) {
            return businessKey;
        }

        if (StringUtils.isEmpty(businessKeyTemplate)) {
            return null;
        }

        try {
            String key = businessKeyTemplate;

            // 替换业务键模板中的变量
            for (Map.Entry<String, Object> entry : titleParams.entrySet()) {
                key = key.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }

            // 替换业务类型、业务ID等基本信息
            if (StringUtils.isNotEmpty(businessType)) {
                key = key.replace("{businessType}", businessType);
            }

            if (StringUtils.isNotEmpty(businessId)) {
                key = key.replace("{businessId}", businessId);
            }

            this.businessKey = key;
            return key;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 转换为统一的模板参数Map
     *
     * @return 模板参数Map
     */
    public Map<String, Object> toTemplateParams() {
        Map<String, Object> params = new HashMap<>();

        // 添加标题参数
        params.putAll(titleParams);

        // 添加内容参数
        params.putAll(contentParams);

        // 添加基本业务信息
        if (StringUtils.isNotEmpty(businessId)) {
            params.put("businessId", businessId);
        }

        if (StringUtils.isNotEmpty(businessCode)) {
            params.put("businessCode", businessCode);
        }

        if (StringUtils.isNotEmpty(businessName)) {
            params.put("businessName", businessName);
        }

        if (StringUtils.isNotEmpty(businessType)) {
            params.put("businessType", businessType);
        }

        if (businessTime != null) {
            params.put("businessTime", businessTime);
        }

        if (StringUtils.isNotEmpty(businessStatus)) {
            params.put("businessStatus", businessStatus);
        }

        if (StringUtils.isNotEmpty(businessUrl)) {
            params.put("businessUrl", businessUrl);
        }

        if (StringUtils.isNotEmpty(sender)) {
            params.put("sender", sender);
        }

        if (urgent != null) {
            params.put("urgent", urgent);
        }

        // 添加业务数据
        params.putAll(businessData);

        // 添加自定义参数
        params.putAll(customParams);

        // 添加批量数据信息
        if (!batchDataIds.isEmpty()) {
            params.put("batchDataIds", batchDataIds);
            params.put("batchDataCount", batchDataIds.size());
        }

        if (!batchDataSummary.isEmpty()) {
            params.put("batchDataSummary", batchDataSummary);
        }

        if (!batchStatistics.isEmpty()) {
            params.putAll(batchStatistics);
        }

        return params;
    }

    /**
     * 获取附件JSON字符串
     *
     * @return 附件JSON字符串
     */
    public String getAttachmentsJson() {
        if (attachments == null || attachments.isEmpty()) {
            return null;
        }

        try {
            return com.alibaba.fastjson2.JSON.toJSONString(attachments);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取操作JSON字符串
     *
     * @return 操作JSON字符串
     */
    public String getActionsJson() {
        if (actions == null || actions.isEmpty()) {
            return null;
        }

        try {
            return com.alibaba.fastjson2.JSON.toJSONString(actions);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否为批量数据
     *
     * @return 是否为批量数据
     */
    public boolean isBatchData() {
        return batchDataIds.size() > 1;
    }

    /**
     * 获取批量数据数量
     *
     * @return 批量数据数量
     */
    public int getBatchDataCount() {
        return batchDataIds.size();
    }

    /**
     * 从Map创建通知负载对象
     *
     * @param map 数据Map
     * @return 通知负载对象
     */
    public static NotificationPayload fromMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return new NotificationPayload();
        }

        NotificationPayload payload = new NotificationPayload();

        // 提取基本业务信息
        if (map.containsKey("businessId")) {
            payload.setBusinessId(map.get("businessId").toString());
        }

        if (map.containsKey("businessCode")) {
            payload.setBusinessCode(map.get("businessCode").toString());
        }

        if (map.containsKey("businessName")) {
            payload.setBusinessName(map.get("businessName").toString());
        }

        if (map.containsKey("businessType")) {
            payload.setBusinessType(map.get("businessType").toString());
        }

        if (map.containsKey("businessTime") && map.get("businessTime") instanceof Date) {
            payload.setBusinessTime((Date) map.get("businessTime"));
        }

        if (map.containsKey("businessStatus")) {
            payload.setBusinessStatus(map.get("businessStatus").toString());
        }

        if (map.containsKey("businessUrl")) {
            payload.setBusinessUrl(map.get("businessUrl").toString());
        }

        if (map.containsKey("sender")) {
            payload.setSender(map.get("sender").toString());
        }

        if (map.containsKey("urgent") && map.get("urgent") instanceof Boolean) {
            payload.setUrgent((Boolean) map.get("urgent"));
        }

        // 提取批量数据信息
        if (map.containsKey("batchDataIds") && map.get("batchDataIds") instanceof List) {
            payload.addAllBatchDataIds((List<String>) map.get("batchDataIds"));
        }

        if (map.containsKey("batchDataSummary") && map.get("batchDataSummary") instanceof List) {
            payload.addAllBatchDataSummary((List<Map<String, Object>>) map.get("batchDataSummary"));
        }

        // 其他数据作为业务数据
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            // 跳过已处理的基本字段
            if (!key.equals("businessId") && !key.equals("businessCode") &&
                !key.equals("businessName") && !key.equals("businessType") &&
                !key.equals("businessTime") && !key.equals("businessStatus") &&
                !key.equals("businessUrl") && !key.equals("sender") &&
                !key.equals("urgent") && !key.equals("attachments") &&
                !key.equals("actions") && !key.equals("batchDataIds") &&
                !key.equals("batchDataSummary")) {
                payload.addBusinessData(key, entry.getValue());
            }
        }

        return payload;
    }
}
