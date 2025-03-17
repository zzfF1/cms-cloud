package com.sinosoft.common.mq.init;

import com.sinosoft.common.mq.config.MqConfigProperties;
import com.sinosoft.common.mq.core.MqCluster;
import com.sinosoft.common.mq.factory.MqFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * MQ资源自动初始化器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqResourceInitializer implements ApplicationRunner {

    private final MqFactory mqFactory;
    private final MqConfigProperties mqConfigProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 如果启用了自动创建
        if (mqConfigProperties.getAutoCreate().isEnabled()) {
            log.info("开始初始化MQ资源...");

            // 初始化预定义主题
            if (!CollectionUtils.isEmpty(mqConfigProperties.getAutoCreate().getTopics())) {
                for (MqConfigProperties.TopicConfig topicConfig : mqConfigProperties.getAutoCreate().getTopics()) {
                    log.info("初始化主题: {}, 集群: {}", topicConfig.getName(), "Kafka默认集群");

                    // 使用默认Kafka集群初始化主题
                    mqFactory.createTopic(topicConfig.getName(), MqCluster.defaultKafka());
                }
            }

            log.info("MQ资源初始化完成");
        }
    }
}
