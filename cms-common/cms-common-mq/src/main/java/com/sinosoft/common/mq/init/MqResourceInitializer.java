package com.sinosoft.common.mq.init;

import com.sinosoft.common.mq.config.MqConfigProperties;
import com.sinosoft.common.mq.core.MqType;
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

            // 获取默认MQ类型
            MqType defaultType = MqType.fromString(mqConfigProperties.getDefaultType());

            // 初始化预定义主题
            if (!CollectionUtils.isEmpty(mqConfigProperties.getAutoCreate().getTopics())) {
                for (MqConfigProperties.TopicConfig topicConfig : mqConfigProperties.getAutoCreate().getTopics()) {
                    log.info("初始化主题: {}, 类型: {}", topicConfig.getName(), defaultType);

                    // 这里可以添加特定代码来创建主题，但实际上这个工作已经在适配器的初始化阶段完成了
                    // 所以我们只需要确保对应类型的生产者被初始化即可
                    mqFactory.getProducer(defaultType, null);
                }
            }

            log.info("MQ资源初始化完成");
        }
    }
}
