package com.sinosoft.common.sync.config;

import com.sinosoft.common.sync.model.SyncConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 同步配置管理
 * <p>
 * 负责从Nacos配置中心读取并管理同步配置
 */
@Slf4j
@Component
@RefreshScope
public class SyncConfigManager {

    @Autowired
    private Environment environment;

    /**
     * 表名 -> 配置
     */
    private final Map<String, SyncConfig> configMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        loadConfig();
    }

    /**
     * 加载配置
     */
    public void loadConfig() {
        log.info("开始加载数据同步配置...");
        try {
            // 从环境变量加载配置
            String tables = environment.getProperty("sync.tables");
            if (tables == null || tables.isEmpty()) {
                log.warn("未配置同步表信息，同步功能将不可用");
                return;
            }

            String[] tableNames = tables.split(",");
            for (String tableName : tableNames) {
                SyncConfig config = loadTableConfig(tableName.trim());
                if (config != null && config.getEnabled()) {
                    configMap.put(tableName.trim(), config);
                }
            }

            log.info("数据同步配置加载完成，共 {} 张表", configMap.size());
        } catch (Exception e) {
            log.error("加载数据同步配置失败", e);
        }
    }

    /**
     * 加载单个表的配置
     */
    private SyncConfig loadTableConfig(String tableName) {
        try {
            String prefix = "sync.config." + tableName;

            // 基本配置
            Boolean enabled = Boolean.valueOf(environment.getProperty(prefix + ".enabled", "false"));
            String primaryKey = environment.getProperty(prefix + ".primary-key");
            String operations = environment.getProperty(prefix + ".operations");
            Boolean feedbackEnabled = Boolean.valueOf(environment.getProperty(prefix + ".feedback-enabled", "false"));
            Integer feedbackTimeout = environment.getProperty(prefix + ".feedback-timeout", Integer.class);

            if (primaryKey == null || operations == null) {
                log.warn("表 {} 缺少必要配置，已跳过", tableName);
                return null;
            }

            // 解析操作类型
            List<Integer> operationList = new ArrayList<>();
            for (String op : operations.split(",")) {
                operationList.add(Integer.parseInt(op.trim()));
            }

            // 创建基本配置
            SyncConfig config = new SyncConfig();
            config.setTableName(tableName);
            config.setPrimaryKey(primaryKey);
            config.setSupportedOperations(operationList.stream().collect(Collectors.toSet()));
            config.setEnabled(enabled);
            config.setFeedbackEnabled(feedbackEnabled);
            config.setFeedbackTimeout(feedbackTimeout);

            // 加载目标系统配置
            String targetSystemsStr = environment.getProperty(prefix + ".target-systems");
            if (targetSystemsStr == null || targetSystemsStr.isEmpty()) {
                log.warn("表 {} 未配置目标系统，同步将不可用", tableName);
                return null;
            }

            String[] targetSystemCodes = targetSystemsStr.split(",");
            List<SyncConfig.TargetSystemConfig> targetSystems = new ArrayList<>();

            for (String systemCode : targetSystemCodes) {
                systemCode = systemCode.trim();
                String targetPrefix = prefix + ".target." + systemCode;

                String systemName = environment.getProperty(targetPrefix + ".name");
                String syncMode = environment.getProperty(targetPrefix + ".sync-mode", "MQ");
                Boolean targetEnabled = Boolean.valueOf(environment.getProperty(targetPrefix + ".enabled", "false"));

                // 检查基本配置
                if (systemName == null) {
                    log.warn("表 {} 的目标系统 {} 缺少系统名称，已跳过", tableName, systemCode);
                    continue;
                }

                SyncConfig.TargetSystemConfig targetConfig = new SyncConfig.TargetSystemConfig();
                targetConfig.setSystemCode(systemCode);
                targetConfig.setSystemName(systemName);
                targetConfig.setSyncMode(syncMode);
                targetConfig.setEnabled(targetEnabled);

                // 根据同步模式加载不同的配置
                if ("HTTP".equalsIgnoreCase(syncMode)) {
                    // HTTP模式配置
                    String apiUrl = environment.getProperty(targetPrefix + ".api-url");
                    String apiMethod = environment.getProperty(targetPrefix + ".api-method", "POST");
                    String apiToken = environment.getProperty(targetPrefix + ".api-token");
                    Integer apiTimeout = environment.getProperty(targetPrefix + ".api-timeout", Integer.class);

                    if (apiUrl == null || apiUrl.isEmpty()) {
                        log.warn("表 {} 的目标系统 {} 使用HTTP模式但未配置API URL，已跳过", tableName, systemCode);
                        continue;
                    }

                    targetConfig.setApiUrl(apiUrl);
                    targetConfig.setApiMethod(apiMethod);
                    targetConfig.setApiToken(apiToken);
                    targetConfig.setApiTimeout(apiTimeout != null ? apiTimeout : 30000);
                } else {
                    // MQ模式配置
                    String mqType = environment.getProperty(targetPrefix + ".mq-type");
                    String topic = environment.getProperty(targetPrefix + ".topic");
                    String queue = environment.getProperty(targetPrefix + ".queue");
                    String tag = environment.getProperty(targetPrefix + ".tag");
                    String feedbackTopic = environment.getProperty(targetPrefix + ".feedback-topic");
                    String feedbackQueue = environment.getProperty(targetPrefix + ".feedback-queue");
                    String feedbackTag = environment.getProperty(targetPrefix + ".feedback-tag");

                    if (mqType == null || topic == null || queue == null) {
                        log.warn("表 {} 的目标系统 {} 使用MQ模式但配置不完整，已跳过", tableName, systemCode);
                        continue;
                    }

                    targetConfig.setMqType(mqType);
                    targetConfig.setTopic(topic);
                    targetConfig.setQueue(queue);
                    targetConfig.setTag(tag);
                    targetConfig.setFeedbackTopic(feedbackTopic);
                    targetConfig.setFeedbackQueue(feedbackQueue);
                    targetConfig.setFeedbackTag(feedbackTag);
                }

                targetSystems.add(targetConfig);
            }

            if (targetSystems.isEmpty()) {
                log.warn("表 {} 未配置有效的目标系统，同步将不可用", tableName);
                return null;
            }

            config.setTargetSystems(targetSystems);
            return config;
        } catch (Exception e) {
            log.error("加载表 {} 的同步配置失败", tableName, e);
            return null;
        }
    }

    /**
     * 获取表的同步配置
     */
    public SyncConfig getConfig(String tableName) {
        return configMap.get(tableName);
    }

    /**
     * 检查表是否配置了同步
     */
    public boolean isTableConfigured(String tableName) {
        return configMap.containsKey(tableName);
    }

    /**
     * 检查表是否支持指定操作类型的同步
     */
    public boolean isSupportOperation(String tableName, int operationType) {
        SyncConfig config = configMap.get(tableName);
        if (config == null) {
            return false;
        }
        return config.getSupportedOperations().contains(operationType);
    }

    /**
     * 获取所有已配置的表名
     */
    public List<String> getAllConfiguredTables() {
        return new ArrayList<>(configMap.keySet());
    }

    /**
     * 获取所有配置
     */
    public Map<String, SyncConfig> getAllConfigs() {
        return new HashMap<>(configMap);
    }
}
