package com.sinosoft.common.mq.config;

import com.sinosoft.common.mq.init.MqTopicInitializer;
import com.sinosoft.common.mq.manager.MqManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQ自动配置类
 * 负责自动配置MQ组件，只有在mq.enabled=true时生效
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MqProperties.class)
@ConditionalOnProperty(prefix = "mq", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MqAutoConfiguration {

    /**
     * 配置MQ管理器
     *
     * @param properties MQ配置属性
     * @return MQ管理器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public MqManager mqManager(MqProperties properties) {
        log.info("初始化MQ管理器，全局启用状态: {}", properties.isEnabled());
        return new MqManager(properties);
    }

    /**
     * 配置MQ主题初始化器
     *
     * @param properties MQ配置属性
     * @return MQ主题初始化器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public MqTopicInitializer mqTopicInitializer(MqProperties properties) {
        return new MqTopicInitializer(properties);
    }
}
