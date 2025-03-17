package com.sinosoft.common.mq.config;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.mq.factory.MqFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * MQ Nacos配置监听器
 */
@Slf4j
public class MqNacosConfigListener implements InitializingBean {

    private final ConfigService configService;
    private final MqFactory mqFactory;
    private final Environment environment;
    private final String dataId;
    private final String group;
    private final long timeout;

    public MqNacosConfigListener(ConfigService configService, MqFactory mqFactory,
                                 Environment environment, String dataId, String group, long timeout) {
        this.configService = configService;
        this.mqFactory = mqFactory;
        this.environment = environment;
        this.dataId = dataId;
        this.group = group;
        this.timeout = timeout;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化时获取一次配置
        String content = configService.getConfig(dataId, group, timeout);
        if (StringUtils.hasText(content)) {
            processConfig(content);
        }

        // 添加配置监听
        configService.addListener(dataId, group, new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                if (StringUtils.hasText(configInfo)) {
                    try {
                        processConfig(configInfo);
                    } catch (Exception e) {
                        log.error("Failed to process MQ config update", e);
                    }
                }
            }
        });

        log.info("MQ Nacos config listener initialized for dataId: {}, group: {}", dataId, group);
    }

    /**
     * 处理配置更新
     */
    private void processConfig(String content) throws Exception {
        // 判断是JSON还是YAML
        if (content.trim().startsWith("{")) {
            // JSON格式
            processJsonConfig(content);
        } else {
            // YAML格式
            processYamlConfig(content);
        }
    }

    /**
     * 处理JSON格式配置
     */
    private void processJsonConfig(String content) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> configs = mapper.readValue(content, Map.class);

        // 转换为属性源
        MapConfigurationPropertySource source = new MapConfigurationPropertySource(configs);

        // 绑定属性
        MqConfigProperties configProperties = Binder.get(environment)
            .bind("mq", MqConfigProperties.class)
            .orElse(new MqConfigProperties());

        // 更新到工厂
        mqFactory.updateConfig(configProperties);

        log.info("MQ config updated from JSON");
    }

    /**
     * 处理YAML格式配置
     */
    private void processYamlConfig(String content) throws Exception {
        Yaml yaml = new Yaml();
        Map<String, Object> configs = yaml.load(content);

        // 只取mq节点下的配置
        Map<String, Object> mqConfigs = (Map<String, Object>) configs.get("mq");
        if (mqConfigs == null) {
            log.warn("No 'mq' config found in YAML");
            return;
        }

        // 转换为属性
        Properties properties = new Properties();
        flattenMap(properties, mqConfigs, "mq");

        // 创建属性源
        PropertiesPropertySource propertySource = new PropertiesPropertySource("nacosConfig", properties);

        // 添加到环境中
        MutablePropertySources propertySources = ((org.springframework.core.env.ConfigurableEnvironment) environment).getPropertySources();
        if (propertySources.contains("nacosConfig")) {
            propertySources.replace("nacosConfig", propertySource);
        } else {
            propertySources.addFirst(propertySource);
        }

        // 绑定属性
        MqConfigProperties configProperties = Binder.get(environment)
            .bind("mq", MqConfigProperties.class)
            .orElse(new MqConfigProperties());

        // 更新到工厂
        mqFactory.updateConfig(configProperties);

        log.info("MQ config updated from YAML");
    }

    /**
     * 扁平化Map为Properties
     */
    private void flattenMap(Properties properties, Map<String, Object> map, String prefix) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix + "." + entry.getKey();
            if (entry.getValue() instanceof Map) {
                flattenMap(properties, (Map<String, Object>) entry.getValue(), key);
            } else if (entry.getValue() instanceof Iterable) {
                int i = 0;
                for (Object item : (Iterable<?>) entry.getValue()) {
                    if (item instanceof Map) {
                        flattenMap(properties, (Map<String, Object>) item, key + "[" + i + "]");
                    } else {
                        properties.put(key + "[" + i + "]", item.toString());
                    }
                    i++;
                }
            } else if (entry.getValue() != null) {
                properties.put(key, entry.getValue().toString());
            }
        }
    }
}
