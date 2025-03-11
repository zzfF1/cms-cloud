package com.sinosoft.notice.domain.dto;

import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.notice.model.NotificationPayload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 通知数据传输对象
 *
 * @author zzf
 */
@Data
@NoArgsConstructor
public class NotificationDTO {

    /**
     * 通知ID
     */
    private Long notificationId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 通知类型：todo-待办, alert-预警, announcement-公告, message-消息
     */
    private String type;

    /**
     * 通知标题
     */
    @NotBlank(message = "通知标题不能为空", groups = {AddGroup.class})
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知来源类型
     */
    private String sourceType;

    /**
     * 通知来源ID
     */
    private String sourceId;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 关键词搜索
     */
    private String keyword;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 接收人ID列表（发送通知时使用）
     */
    private List<Long> receiverIds;

    /**
     * 模板代码（发送通知时使用）
     */
    @NotBlank(message = "模板代码不能为空", groups = {AddGroup.class})
    private String templateCode;

    /**
     * 优先级：high-高, medium-中, low-低
     */
    private String priority;

    /**
     * 业务数据，JSON格式
     */
    private Map<String, Object> businessData;

    /**
     * 建立通知负载对象
     */
    public NotificationPayload buildPayload() {
        NotificationPayload payload = new NotificationPayload();

        // 设置标题和内容
        if (title != null) {
            payload.addTitleParam("title", title);
        }
        if (content != null) {
            payload.addContentParam("content", content);
        }

        // 设置业务数据
        if (businessData != null && !businessData.isEmpty()) {
            payload.addAllBusinessData(businessData);
        }

        // 设置优先级
        if ("high".equals(priority)) {
            payload.setUrgent(true);
        }

        // 设置发送者
        payload.setUserId(userId);

        return payload;
    }
}
