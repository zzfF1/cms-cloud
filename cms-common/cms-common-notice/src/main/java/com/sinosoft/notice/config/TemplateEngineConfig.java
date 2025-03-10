package com.sinosoft.notice.config;

import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: cms-cloud
 * @description:
 * @author: zzf
 * @create: 2025-03-08 15:28
 */
@Configuration
public class TemplateEngineConfig {

    @Bean
    public TemplateEngine templateEngine() {
        // 创建模板引擎配置
        TemplateConfig config = new TemplateConfig();
        // 这里可以根据需要配置模板引擎属性
        // 例如：config.setCharset("UTF-8");
        // 返回模板引擎实例
        return TemplateUtil.createEngine(config);
    }
}
