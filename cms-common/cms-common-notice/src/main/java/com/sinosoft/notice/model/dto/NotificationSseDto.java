package com.sinosoft.notice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 通知SSE消息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 通知ID
     */
    private Long notificationId;

    /**
     * 通知类型
     */
    private String notificationType;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 额外数据
     */
    private Object data;
}
