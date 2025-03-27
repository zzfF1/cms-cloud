package com.sinosoft.common.sync.config;

import com.sinosoft.common.core.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class SyncConfigManager {

    private final Environment environment;
    private final Map<String, SyncConfig> configMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, String> entityTableMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void initialize() {
        // 加载配置
        loadSyncConfigurations();

        // 初始化默认类映射
        initializeDefaultMappings();
    }

    /**
     * 加载同步配置
     */
    private void loadSyncConfigurations() {
        String[] tables = environment.getProperty("sync.tables", "").split(",");

        for (String table : tables) {
            if (StringUtils.isNotEmpty(table)) {
                String prefix = "sync.config." + table.trim();

                SyncConfig config = new SyncConfig();
                config.setTableName(table.trim());
                config.setEnabled(environment.getProperty(prefix + ".enabled", Boolean.class, true));
                config.setPrimaryKey(environment.getProperty(prefix + ".primary-key", "id"));

                // 加载目标系统
                String targetSystems = environment.getProperty(prefix + ".target-systems", "");
                if (StringUtils.isNotEmpty(targetSystems)) {
                    for (String target : targetSystems.split(",")) {
                        SyncConfig.TargetSystemConfig targetConfig = loadTargetConfig(prefix, target.trim());
                        if (targetConfig != null) {
                            config.getTargetSystems().add(targetConfig);
                        }
                    }
                }

                configMap.put(table.trim(), config);
                log.info("已加载表 {} 的同步配置", table.trim());
            }
        }
    }

    /**
     * 加载目标系统配置
     */
    private SyncConfig.TargetSystemConfig loadTargetConfig(String prefix, String target) {
        String targetPrefix = prefix + ".target." + target;

        SyncConfig.TargetSystemConfig config = new SyncConfig.TargetSystemConfig();
        config.setSystemCode(target);
        config.setSystemName(environment.getProperty(targetPrefix + ".name", target));
        config.setEnabled(environment.getProperty(targetPrefix + ".enabled", Boolean.class, true));
        config.setSyncMode(environment.getProperty(targetPrefix + ".sync-mode", "MQ"));

        // 根据同步模式加载不同配置
        String syncMode = config.getSyncMode();
        if ("MQ".equalsIgnoreCase(syncMode)) {
            config.setMqType(environment.getProperty(targetPrefix + ".mq-type"));
            config.setTopic(environment.getProperty(targetPrefix + ".topic"));
            config.setQueue(environment.getProperty(targetPrefix + ".queue"));
            config.setTag(environment.getProperty(targetPrefix + ".tag"));
        } else if ("DUBBO".equalsIgnoreCase(syncMode)) {
            config.setServiceId(environment.getProperty(targetPrefix + ".service-id"));
        } else if ("HTTP".equalsIgnoreCase(syncMode)) {
            config.setApiUrl(environment.getProperty(targetPrefix + ".api-url"));
            config.setApiMethod(environment.getProperty(targetPrefix + ".api-method", "POST"));
            config.setApiToken(environment.getProperty(targetPrefix + ".api-token"));
            config.setApiTimeout(environment.getProperty(targetPrefix + ".api-timeout", Integer.class, 30000));
        }

        return config;
    }

    /**
     * 初始化默认类映射
     */
    private void initializeDefaultMappings() {
        // 从配置读取类映射
        String mappings = environment.getProperty("sync.class-mappings", "");
        if (StringUtils.isNotEmpty(mappings)) {
            for (String mapping : mappings.split(",")) {
                String[] parts = mapping.trim().split(":");
                if (parts.length == 2) {
                    try {
                        Class<?> clazz = Class.forName(parts[0].trim());
                        registerEntityMapping(clazz, parts[1].trim());
                    } catch (ClassNotFoundException e) {
                        log.warn("找不到实体类: {}", parts[0]);
                    }
                }
            }
        }

        // 注册常用实体类
        try {
            registerEntityMapping(Class.forName("com.sinosoft.common.schema.agent.domain.Laagent"), "laagent");
            registerEntityMapping(Class.forName("com.sinosoft.common.schema.agent.domain.Latree"), "latree");
            registerEntityMapping(Class.forName("com.sinosoft.common.schema.team.domain.Labranchgroup"), "labranchgroup");
            registerEntityMapping(Class.forName("com.sinosoft.common.schema.team.domain.Lacom"), "lacom");
        } catch (ClassNotFoundException e) {
            log.warn("注册默认实体映射失败", e);
        }
    }

    /**
     * 注册实体类与表名的映射
     */
    public void registerEntityMapping(Class<?> entityClass, String tableName) {
        entityTableMap.put(entityClass, tableName);
        log.debug("注册实体类映射: {} -> {}", entityClass.getName(), tableName);
    }

    /**
     * 根据实体类获取表名
     */
    public String getTableNameForEntity(Class<?> entityClass) {
        // 1. 从缓存中查找
        String tableName = entityTableMap.get(entityClass);

        if (tableName != null) {
            return tableName;
        }

        // 2. 使用命名约定 - 尝试简单类名小写
        tableName = entityClass.getSimpleName().toLowerCase();
        if (configMap.containsKey(tableName)) {
            entityTableMap.put(entityClass, tableName);
            return tableName;
        }

        // 3. 尝试la前缀 + 小写类名
        String laTableName = "la" + entityClass.getSimpleName().toLowerCase();
        if (configMap.containsKey(laTableName)) {
            entityTableMap.put(entityClass, laTableName);
            return laTableName;
        }

        log.warn("无法确定实体类 {} 的表名", entityClass.getName());
        return null;
    }

    /**
     * 根据实体类获取同步配置
     */
    public SyncConfig getConfigForEntity(Class<?> entityClass) {
        String tableName = getTableNameForEntity(entityClass);
        return tableName != null ? getConfig(tableName) : null;
    }

    /**
     * 获取实体类对应的主键名
     */
    public String getPrimaryKeyForEntity(Class<?> entityClass) {
        String tableName = getTableNameForEntity(entityClass);
        if (tableName != null) {
            SyncConfig config = getConfig(tableName);
            return config != null ? config.getPrimaryKey() : null;
        }
        return null;
    }

    /**
     * 获取表名对应的同步配置
     */
    public SyncConfig getConfig(String tableName) {
        return configMap.get(tableName);
    }
}
