package com.sinosoft.notice.core.callback;

import lombok.Data;
import java.util.Date;
import java.util.Map;

/**
 * 通知回调事件
 */
public class NotificationCallbackEvent {

    /**
     * 通知创建事件
     */
    @Data
    public static class Created {
        /**
         * 通知ID
         */
        private Long notificationId;

        /**
         * 模板ID
         */
        private Long templateId;

        /**
         * 模板代码
         */
        private String templateCode;

        /**
         * 通知类型
         */
        private String type;

        /**
         * 业务键
         */
        private String businessKey;

        /**
         * 业务数据
         */
        private Map<String, Object> businessData;

        /**
         * 接收人数量
         */
        private Integer recipientCount;

        /**
         * 创建时间
         */
        private Date createTime;
    }

    /**
     * 通知读取事件
     */
    @Data
    public static class Read {
        /**
         * 通知ID
         */
        private Long notificationId;

        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 通知类型
         */
        private String type;

        /**
         * 业务键
         */
        private String businessKey;

        /**
         * 读取时间
         */
        private Date readTime;
    }

    /**
     * 通知处理事件
     */
    @Data
    public static class Processed {
        /**
         * 通知ID
         */
        private Long notificationId;

        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 通知类型
         */
        private String type;

        /**
         * 业务键
         */
        private String businessKey;

        /**
         * 处理时间
         */
        private Date processTime;

        /**
         * 处理备注
         */
        private String processNote;

        /**
         * 处理结果
         */
        private String processResult;
    }

    /**
     * 通知发送事件
     */
    @Data
    public static class Sent {
        /**
         * 通知ID
         */
        private Long notificationId;

        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 发送渠道
         */
        private String channel;

        /**
         * 目标地址
         */
        private String targetAddress;

        /**
         * 发送时间
         */
        private Date sendTime;

        /**
         * 发送记录ID
         */
        private Long deliveryId;
    }

    /**
     * 通知发送失败事件
     */
    @Data
    public static class SendFailed {
        /**
         * 通知ID
         */
        private Long notificationId;

        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 发送渠道
         */
        private String channel;

        /**
         * 目标地址
         */
        private String targetAddress;

        /**
         * 失败时间
         */
        private Date failTime;

        /**
         * 错误信息
         */
        private String errorMessage;

        /**
         * 重试次数
         */
        private Integer retryCount;

        /**
         * 发送记录ID
         */
        private Long deliveryId;
    }

    /**
     * 通知过期事件
     */
    @Data
    public static class Expired {
        /**
         * 通知ID
         */
        private Long notificationId;

        /**
         * 通知类型
         */
        private String type;

        /**
         * 业务键
         */
        private String businessKey;

        /**
         * 过期时间
         */
        private Date expireTime;

        /**
         * 未读用户数
         */
        private Integer unreadCount;

        /**
         * 未处理用户数
         */
        private Integer unprocessedCount;
    }

    /**
     * 通知取消事件
     */
    @Data
    public static class Cancelled {
        /**
         * 通知ID
         */
        private Long notificationId;

        /**
         * 通知类型
         */
        private String type;

        /**
         * 业务键
         */
        private String businessKey;

        /**
         * 取消时间
         */
        private Date cancelTime;

        /**
         * 取消原因
         */
        private String cancelReason;

        /**
         * 取消用户
         */
        private Long cancelUser;
    }
}
