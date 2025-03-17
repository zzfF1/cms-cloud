package com.sinosoft.common.sync.model;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 同步配置
 */
@Data
public class SyncConfig {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 主键字段
     */
    private String primaryKey;

    /**
     * 支持的操作类型（1-新增，2-修改，3-删除）
     */
    private Set<Integer> supportedOperations;

    /**
     * 目标系统配置
     */
    private List<TargetSystemConfig> targetSystems;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 是否启用反馈
     */
    private Boolean feedbackEnabled;

    /**
     * 反馈超时时间（秒）
     */
    private Integer feedbackTimeout;

    /**
     * 目标系统配置
     */
    @Data
    public static class TargetSystemConfig {

        /**
         * 目标系统代码
         */
        private String systemCode;

        /**
         * 目标系统名称
         */
        private String systemName;

        /**
         * 同步模式（MQ或HTTP）
         */
        private String syncMode;

        /**
         * 是否启用
         */
        private Boolean enabled;

        // ========== MQ相关配置 ==========

        /**
         * MQ类型（如：ROCKETMQ, KAFKA, RABBITMQ）
         */
        private String mqType;

        /**
         * 主题或交换机
         */
        private String topic;

        /**
         * 队列或路由键
         */
        private String queue;

        /**
         * 消息标签（RocketMQ专用）
         */
        private String tag;

        /**
         * 反馈主题
         */
        private String feedbackTopic;

        /**
         * 反馈队列
         */
        private String feedbackQueue;

        /**
         * 反馈标签
         */
        private String feedbackTag;

        // ========== HTTP接口相关配置 ==========

        /**
         * API接口URL
         */
        private String apiUrl;

        /**
         * API接口方法（GET, POST, PUT, DELETE等）
         */
        private String apiMethod;

        /**
         * API认证Token
         */
        private String apiToken;

        /**
         * API调用超时时间（毫秒）
         */
        private Integer apiTimeout;

        /**
         * 获取同步模式
         *
         * @return 同步模式（默认为MQ）
         */
        public String getSyncMode() {
            return syncMode != null ? syncMode : "MQ";
        }

        /**
         * 检查是否使用MQ模式
         */
        public boolean isMqMode() {
            return "MQ".equalsIgnoreCase(getSyncMode());
        }

        /**
         * 检查是否使用HTTP模式
         */
        public boolean isHttpMode() {
            return "HTTP".equalsIgnoreCase(getSyncMode());
        }
    }
}
