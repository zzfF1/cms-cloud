package com.sinosoft.common.mq.annotation;

import com.sinosoft.common.mq.config.MqConfigProperties;
import com.sinosoft.common.mq.core.MqConsumer;
import com.sinosoft.common.mq.core.MqMessage;
import com.sinosoft.common.mq.core.MqResult;
import com.sinosoft.common.mq.core.MqType;
import com.sinosoft.common.mq.factory.MqFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/**
 * MQ监听器注解处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqListenerAnnotationBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    // 使用RequiredArgsConstructor自动注入
    private final MqFactory mqFactory;
    private final MqConfigProperties mqConfigProperties;

    // 用于收集所有需要创建的主题
    private final Set<TopicDefinition> discoveredTopics = new HashSet<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 如果是仅生产者模式，不处理注解
        if (mqConfigProperties.isProducerOnly() || !mqConfigProperties.isConsumerEnabled()) {
            return bean;
        }

        Class<?> clazz = bean.getClass();

        // 处理类中所有带有@MqListener注解的方法
        for (Method method : clazz.getDeclaredMethods()) {
            MqListener annotation = method.getAnnotation(MqListener.class);
            if (annotation != null) {
                // 收集主题信息
                collectTopicInfo(annotation);
                registerListener(bean, method, annotation);
            }
        }

        return bean;
    }

    /**
     * 收集注解中的主题信息
     */
    private void collectTopicInfo(MqListener annotation) {
        String topic = annotation.topic();
        String clusterInfo = annotation.cluster();

        if (!StringUtils.hasText(topic)) {
            return;
        }

        MqType mqType;
        String clusterName = null;

        if (StringUtils.hasText(clusterInfo)) {
            String[] parts = clusterInfo.split(":");
            mqType = parts.length > 0 ? MqType.fromString(parts[0]) : MqType.fromString(mqConfigProperties.getDefaultType());
            clusterName = parts.length > 1 ? parts[1] : null;
        } else {
            mqType = MqType.fromString(mqConfigProperties.getDefaultType());
        }

        TopicDefinition topicDef = new TopicDefinition(topic, mqType, clusterName);
        discoveredTopics.add(topicDef);
        log.debug("从注解发现主题: {}, 类型: {}, 集群: {}", topic, mqType, clusterName);
    }

    /**
     * 获取所有发现的主题
     */
    public Set<TopicDefinition> getDiscoveredTopics() {
        return Collections.unmodifiableSet(discoveredTopics);
    }

    /**
     * 主题定义内部类
     */
    @Data
    @EqualsAndHashCode
    public static class TopicDefinition {
        private final String topic;
        private final MqType mqType;
        private final String clusterName;
    }

    private void registerListener(Object bean, Method method, MqListener annotation) {
        String topic = annotation.topic();
        String tags = annotation.tags();
        String clusterInfo = annotation.cluster();

        // 获取对应集群的消费者
        MqConsumer consumer;
        if (StringUtils.hasText(clusterInfo)) {
            String[] parts = clusterInfo.split(":");
            MqType mqType = parts.length > 0 ? MqType.fromString(parts[0]) : MqType.fromString(mqConfigProperties.getDefaultType());
            String clusterName = parts.length > 1 ? parts[1] : null;
            consumer = mqFactory.getConsumer(mqType, clusterName);
            log.info("注册MQ监听器: bean={}, method={}, topic={}, tags={}, mqType={}, cluster={}",
                bean.getClass().getSimpleName(), method.getName(), topic, tags, mqType, clusterName);
        } else {
            consumer = mqFactory.getConsumer();
            log.info("注册MQ监听器: bean={}, method={}, topic={}, tags={}, 使用默认消费者",
                bean.getClass().getSimpleName(), method.getName(), topic, tags);
        }

        // 创建消息处理函数
        Function<MqMessage, MqResult<?>> handler = message -> {
            try {
                // 调用带注解的方法处理消息
                method.setAccessible(true);
                if (method.getParameterCount() == 1 && method.getParameterTypes()[0].isAssignableFrom(MqMessage.class)) {
                    Object result = method.invoke(bean, message);

                    if (result instanceof MqResult) {
                        return (MqResult<?>) result;
                    }
                    return MqResult.success(result);
                }

                log.error("消息处理方法签名无效: {}.{}", bean.getClass().getName(), method.getName());
                return MqResult.failure("INVALID-METHOD", "消息处理方法必须接受MqMessage参数");
            } catch (Exception e) {
                log.error("处理MQ消息异常: topic={}, tags={}, messageId={}",
                    topic, tags, message.getMessageId(), e);
                return MqResult.failure("INVOKE-ERROR", e.getMessage());
            }
        };

        // 订阅主题
        consumer.subscribe(topic, tags, handler);
        consumer.start();
    }
}
