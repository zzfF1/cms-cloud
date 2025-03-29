package com.sinosoft.common.sync.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class SyncConfig {
    private String tableName;
    private boolean enabled = true;
    private String primaryKey = "id";
    private List<TargetSystemConfig> targetSystems = new ArrayList<>();

    @Data
    public static class TargetSystemConfig {
        private String systemCode;
        private String systemName;
        private boolean enabled = true;
        private String syncMode = "MQ"; // MQ, HTTP, DUBBO
        private String serviceId; // Dubbo服务ID

        // MQ配置
        private String mqType;
        private String topic;
        private String queue;
        private String tag;

        // HTTP配置
        private String apiUrl;
        private String apiMethod;
        private String apiToken;
        private Integer apiTimeout;

        // 辅助方法
        public boolean isHttpMode() {
            return "HTTP".equalsIgnoreCase(syncMode);
        }

    }
}
