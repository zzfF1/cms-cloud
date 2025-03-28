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
    }

    /**
     * 加载同步配置
     */
    private void loadSyncConfigurations() {
        // 支持两种格式的配置：传统格式和简化格式
        String[] tables = environment.getProperty("sync.tables", "").split(",");

        for (String table : tables) {
            if (StringUtils.isNotEmpty(table)) {
                table = table.trim();

                // 尝试加载简化格式配置
                SyncConfig simpleConfig = loadSimpleConfigFormat(table);
                if (simpleConfig != null) {
                    configMap.put(table, simpleConfig);
                    log.info("已加载表 {} 的简化配置", table);
                    continue;
                }

                // 如果简化格式不存在，回退到传统格式
                String prefix = "sync.config." + table;
                SyncConfig config = new SyncConfig();
                config.setTableName(table);
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

                configMap.put(table, config);
                log.info("已加载表 {} 的传统配置", table);
            }
        }
    }

    /**
     * 尝试加载简化格式配置
     */
    private SyncConfig loadSimpleConfigFormat(String table) {
        String prefix = "sync.table-configs";

        // 检查是否存在简化配置
        Integer configIndex = findTableConfigIndex(table);
        if (configIndex == null) {
            return null;
        }

        SyncConfig config = new SyncConfig();
        config.setTableName(table);

        // 加载基本配置
        String configPrefix = prefix + "[" + configIndex + "]";
        config.setEnabled(environment.getProperty(configPrefix + ".enabled", Boolean.class, true));
        config.setPrimaryKey(environment.getProperty(configPrefix + ".primary-key", "id"));

        // 加载目标系统配置
        int targetCount = countTargets(configPrefix);
        for (int i = 0; i < targetCount; i++) {
            String targetPrefix = configPrefix + ".targets[" + i + "]";

            SyncConfig.TargetSystemConfig targetConfig = new SyncConfig.TargetSystemConfig();
            targetConfig.setSystemCode(environment.getProperty(targetPrefix + ".id", ""));
            targetConfig.setSystemName(environment.getProperty(targetPrefix + ".name", ""));
            targetConfig.setEnabled(environment.getProperty(targetPrefix + ".enabled", Boolean.class, true));
            targetConfig.setSyncMode(environment.getProperty(targetPrefix + ".mode", ""));

            // 根据同步模式加载特定配置
            if ("MQ".equalsIgnoreCase(targetConfig.getSyncMode())) {
                targetConfig.setMqType(environment.getProperty(targetPrefix + ".mq-type"));
                targetConfig.setTopic(environment.getProperty(targetPrefix + ".topic"));
                targetConfig.setQueue(environment.getProperty(targetPrefix + ".queue"));
                targetConfig.setTag(environment.getProperty(targetPrefix + ".tag"));
            } else if ("DUBBO".equalsIgnoreCase(targetConfig.getSyncMode())) {
                targetConfig.setServiceId(environment.getProperty(targetPrefix + ".service-id"));
            } else if ("HTTP".equalsIgnoreCase(targetConfig.getSyncMode())) {
                targetConfig.setApiUrl(environment.getProperty(targetPrefix + ".api-url"));
                targetConfig.setApiMethod(environment.getProperty(targetPrefix + ".api-method", "POST"));
                targetConfig.setApiToken(environment.getProperty(targetPrefix + ".api-token"));
                targetConfig.setApiTimeout(environment.getProperty(targetPrefix + ".api-timeout", Integer.class, 30000));
            }

            config.getTargetSystems().add(targetConfig);
        }

        return config;
    }

    /**
     * 查找表格配置的索引
     */
    private Integer findTableConfigIndex(String table) {
        int index = 0;
        String prefix = "sync.table-configs";

        while (true) {
            String name = environment.getProperty(prefix + "[" + index + "].name");
            if (name == null) {
                break;
            }

            if (table.equals(name.trim())) {
                return index;
            }

            index++;
        }

        return null;
    }

    /**
     * 计算目标系统数量
     */
    private int countTargets(String configPrefix) {
        int count = 0;
        while (environment.getProperty(configPrefix + ".targets[" + count + "].id") != null) {
            count++;
        }
        return count;
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
     * 根据实体类获取表名
     * 直接使用类名小写作为表名，无需配置映射
     */
    public String getTableNameForEntity(Class<?> entityClass) {
        // 1. 从缓存中查找
        String tableName = entityTableMap.get(entityClass);
        if (tableName != null) {
            return tableName;
        }

        // 2. 直接使用类名小写作为表名
        tableName = entityClass.getSimpleName().toLowerCase();

        // 3. 检查表配置是否存在
        if (!configMap.containsKey(tableName)) {
            log.error("实体类 {} 对应的表 {} 没有同步配置，将跳过同步处理",
                entityClass.getName(), tableName);
            return null;
        }

        // 4. 缓存并返回表名
        entityTableMap.put(entityClass, tableName);
        return tableName;
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
