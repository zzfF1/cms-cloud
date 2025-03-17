package com.sinosoft.common.mq.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MQ消息监听器注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MqListener {
    /**
     * 要订阅的主题
     */
    String topic();

    /**
     * 消息标签，用于过滤消息
     */
    String tags() default "";

    /**
     * 集群名称，为空则使用默认集群
     * 格式：类型:集群名，例如 kafka:cluster1
     */
    String cluster() default "";
}
