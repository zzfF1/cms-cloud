package com.sinosoft.common.forest.config;

import com.sinosoft.common.core.factory.YmlPropertySourceFactory;
import com.sinosoft.common.forest.handler.ForestExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

/**
 * Forest 配置属性
 *
 * @author AprilWind
 */
@Slf4j
@AutoConfiguration
@PropertySource(value = "classpath:common-forest.yml", factory = YmlPropertySourceFactory.class)
public class ForestConfiguration {

    /**
     * Forest异常处理器
     */
    @Bean
    public ForestExceptionHandler forestExceptionHandler() {
        return new ForestExceptionHandler();
    }

}
