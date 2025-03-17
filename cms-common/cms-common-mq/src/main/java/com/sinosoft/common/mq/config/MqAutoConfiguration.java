package com.sinosoft.common.mq.config;

import com.sinosoft.common.mq.annotation.MqListenerAnnotationBeanPostProcessor;
import com.sinosoft.common.mq.core.MqConsumer;
import com.sinosoft.common.mq.core.MqProducer;
import com.sinosoft.common.mq.core.MqType;
import com.sinosoft.common.mq.factory.MqFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.stream.Collectors;

/**
 * MQ自动配置类
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MqConfigProperties.class)
@ConditionalOnProperty(prefix = "mq", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MqAutoConfiguration {

    @Autowired
    private MqConfigProperties mqConfigProperties;

    @Autowired
    private Environment environment;

    /**
     * 配置MQ工厂
     */
    @Bean
    @ConditionalOnMissingBean
    @RefreshScope
    public MqFactory mqFactory() {
        // 添加配置内容的详细输出
        log.info("初始化MQ工厂，配置详情：");
        log.info("- 默认类型: {}", mqConfigProperties.getDefaultType());
        log.info("- 懒加载: {}", mqConfigProperties.isLazyInit());
        log.info("- 降级功能: {}", mqConfigProperties.isFallbackEnabled());

        // Kafka配置详情
        MqConfigProperties.KafkaProperties kafka = mqConfigProperties.getKafka();
        log.info("- Kafka配置：default-cluster={}, 集群列表={}",
            kafka.getDefaultCluster(),
            kafka.getClusters() != null ? kafka.getClusters().stream()
                .map(c -> c.getName() + ":" + c.getBootstrapServers())
                .collect(Collectors.joining(",")) : "null");

        MqFactory factory = new MqFactory(mqConfigProperties);
        log.info("MQ工厂初始化完成，默认类型: {}，懒加载: {}，降级功能: {}",
            mqConfigProperties.getDefaultType(),
            mqConfigProperties.isLazyInit(),
            mqConfigProperties.isFallbackEnabled());
        return factory;
    }

    /**
     * 配置默认生产者
     */
    @Bean(name = "defaultMqProducer")
    @ConditionalOnMissingBean(name = "defaultMqProducer")
    @ConditionalOnProperty(prefix = "mq", name = "enabled", havingValue = "true", matchIfMissing = true)
    @RefreshScope
    public MqProducer defaultMqProducer(MqFactory mqFactory) {
        log.info("创建默认MQ生产者，类型: {}", mqConfigProperties.getDefaultType());
        return mqFactory.getProducer();
    }

    /**
     * 配置默认消费者
     */
    @Bean(name = "defaultMqConsumer")
    @ConditionalOnMissingBean(name = "defaultMqConsumer")
    @ConditionalOnProperty(prefix = "mq", name = "enabled", havingValue = "true", matchIfMissing = true)
    @RefreshScope
    public MqConsumer defaultMqConsumer(MqFactory mqFactory) {
        log.info("创建默认MQ消费者，类型: {}", mqConfigProperties.getDefaultType());
        return mqFactory.getConsumer();
    }

    /**
     * 配置Kafka生产者
     */
    @Bean(name = "kafkaMqProducer")
    @ConditionalOnProperty(prefix = "mq.kafka", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean(name = "kafkaMqProducer")
    @RefreshScope
    public MqProducer kafkaMqProducer(MqFactory mqFactory) {
        log.info("创建Kafka生产者");
        return mqFactory.getProducer(MqType.KAFKA, mqConfigProperties.getKafka().getDefaultCluster());
    }

    /**
     * 配置RabbitMQ生产者
     */
    @Bean(name = "rabbitMqProducer")
    @ConditionalOnProperty(prefix = "mq.rabbit", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean(name = "rabbitMqProducer")
    @RefreshScope
    public MqProducer rabbitMqProducer(MqFactory mqFactory) {
        log.info("创建RabbitMQ生产者");
        return mqFactory.getProducer(MqType.RABBIT_MQ, mqConfigProperties.getRabbit().getDefaultCluster());
    }

    /**
     * 配置RocketMQ生产者
     */
    @Bean(name = "rocketMqProducer")
    @ConditionalOnProperty(prefix = "mq.rocket", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean(name = "rocketMqProducer")
    @RefreshScope
    public MqProducer rocketMqProducer(MqFactory mqFactory) {
        log.info("创建RocketMQ生产者");
        return mqFactory.getProducer(MqType.ROCKET_MQ, mqConfigProperties.getRocket().getDefaultCluster());
    }

    /**
     * 配置MQ监听器注解处理器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "mq", name = {"enabled", "consumerEnabled"}, havingValue = "true", matchIfMissing = true)
    public MqListenerAnnotationBeanPostProcessor mqListenerAnnotationBeanPostProcessor(
        MqFactory mqFactory, MqConfigProperties mqConfigProperties) {
        return new MqListenerAnnotationBeanPostProcessor(mqFactory, mqConfigProperties);
    }
}
