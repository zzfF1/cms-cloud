package com.sinosoft.common.mq.init;

import com.sinosoft.common.mq.annotation.MqListenerAnnotationBeanPostProcessor;
import com.sinosoft.common.mq.config.MqConfigProperties;
import com.sinosoft.common.mq.factory.MqFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * MQ主题创建器
 * 扫描注解中的主题信息并自动创建
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqTopicCreator implements ApplicationListener<ContextRefreshedEvent> {

    private final MqListenerAnnotationBeanPostProcessor beanPostProcessor;
    private final MqConfigProperties mqConfigProperties;
    private final MqFactory mqFactory;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!mqConfigProperties.isEnabled()) {
            return;
        }

        // 从配置中检查是否启用自动创建
        boolean autoCreateEnabled = mqConfigProperties.getAutoCreate().isEnabled();

        if (!autoCreateEnabled) {
            log.info("主题自动创建功能已禁用");
            return;
        }

        log.info("开始从注解创建MQ主题");
        Set<MqListenerAnnotationBeanPostProcessor.TopicDefinition> topics =
            beanPostProcessor.getDiscoveredTopics();

        if (topics.isEmpty()) {
            log.info("未发现需要创建的主题");
            return;
        }

        log.info("发现 {} 个需要创建的主题", topics.size());

        // 创建每个主题
        for (MqListenerAnnotationBeanPostProcessor.TopicDefinition def : topics) {
            try {
                mqFactory.createTopic(def.getTopic(), def.getMqType(), def.getClusterName());
            } catch (Exception e) {
                log.error("创建主题失败: {}, 类型: {}, 集群: {}, 错误: {}",
                    def.getTopic(), def.getMqType(), def.getClusterName(), e.getMessage());
            }
        }
    }
}
