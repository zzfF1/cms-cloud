package com.sinosoft.common.mq.config;

import com.sinosoft.common.mq.core.MqConsumer;
import com.sinosoft.common.mq.core.MqProducer;
import com.sinosoft.common.mq.factory.MqFactory;
import com.sinosoft.common.mq.fallback.NoOpMqConsumer;
import com.sinosoft.common.mq.fallback.NoOpMqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQ禁用时的配置类，提供必要的空实现Bean
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "mq", name = "enabled", havingValue = "false")
public class MqDisabledConfiguration {

    /**
     * 当MQ禁用时提供MqFactory的空实现
     */
    @Bean
    @ConditionalOnMissingBean
    public MqFactory mqFactory() {
        log.info("MQ功能已禁用，创建MqFactory空实现");
        // 创建一个禁用状态的MqFactory
        MqConfigProperties properties = new MqConfigProperties();
        properties.setEnabled(false);
        return new MqFactory(properties);
    }

    /**
     * 当MQ禁用时提供MqProducer的空实现
     */
    @Bean(name = "defaultMqProducer")
    @ConditionalOnMissingBean(name = "defaultMqProducer")
    public MqProducer defaultMqProducer(MqFactory mqFactory) {
        return new NoOpMqProducer();
    }

    /**
     * 当MQ禁用时提供MqConsumer的空实现
     */
    @Bean(name = "defaultMqConsumer")
    @ConditionalOnMissingBean(name = "defaultMqConsumer")
    public MqConsumer defaultMqConsumer(MqFactory mqFactory) {
        return new NoOpMqConsumer();
    }
}
