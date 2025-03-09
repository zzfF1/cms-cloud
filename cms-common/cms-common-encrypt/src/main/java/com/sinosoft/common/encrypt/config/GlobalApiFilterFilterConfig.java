package com.sinosoft.common.encrypt.config;

import com.sinosoft.common.encrypt.filter.GlobalApiFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * 统一接入层全局过滤器配置
 *
 * @author: zzf
 * @create: 2025-02-12 16:34
 */
@AutoConfiguration
public class GlobalApiFilterFilterConfig {

    @Bean
    public GlobalApiFilter globalApiFilter() {
        return new GlobalApiFilter();  // 通过构造函数注入
    }

    @Bean
    public FilterRegistrationBean<GlobalApiFilter> globalApiFilterRegistration(GlobalApiFilter globalApiFilter) {
        FilterRegistrationBean<GlobalApiFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(globalApiFilter);
        registration.addUrlPatterns("/*");
        registration.setName("globalApiFilter");
        registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
        return registration;
    }
}
