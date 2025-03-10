package com.sinosoft.notice.model;

import com.sinosoft.common.core.utils.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.*;

/**
 * 通知业务负载对象 (简化版)
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
     * 发起人
     */
    private Long userId;

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
     * 业务键 (用于识别相同业务通知)
     */
    private String businessKey;

    /**
     * 业务键模板 (用于生成业务键)
     */
    private String businessKeyTemplate;

    /**
     * 添加标题参数
     */
    public NotificationPayload addTitleParam(String key, Object value) {
        if (StringUtils.isNotEmpty(key) && value != null) {
            this.titleParams.put(key, value);
        }
        return this;
    }

    /**
     * 添加内容参数
     */
    public NotificationPayload addContentParam(String key, Object value) {
        if (StringUtils.isNotEmpty(key) && value != null) {
            this.contentParams.put(key, value);
        }
        return this;
    }

    /**
     * 添加业务数据
     */
    public NotificationPayload addBusinessData(String key, Object value) {
        if (StringUtils.isNotEmpty(key) && value != null) {
            this.businessData.put(key, value);
        }
        return this;
    }

    /**
     * 批量添加业务数据
     */
    public NotificationPayload addAllBusinessData(Map<String, Object> data) {
        if (data != null && !data.isEmpty()) {
            this.businessData.putAll(data);
        }
        return this;
    }

    /**
     * 添加附件
     */
    public NotificationPayload addAttachment(NotificationAttachment attachment) {
        if (attachment != null) {
            this.attachments.add(attachment);
        }
        return this;
    }

    /**
     * 添加操作
     */
    public NotificationPayload addAction(NotificationAction action) {
        if (action != null) {
            this.actions.add(action);
        }
        return this;
    }

    /**
     * 添加自定义参数
     */
    public NotificationPayload addCustomParam(String key, Object value) {
        if (StringUtils.isNotEmpty(key) && value != null) {
            this.customParams.put(key, value);
        }
        return this;
    }

    /**
     * 批量添加自定义参数
     */
    public NotificationPayload addAllCustomParams(Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            this.customParams.putAll(params);
        }
        return this;
    }

    /**
     * 转换为统一的模板参数Map
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

        return params;
    }

    /**
     * 获取附件JSON字符串
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
}
