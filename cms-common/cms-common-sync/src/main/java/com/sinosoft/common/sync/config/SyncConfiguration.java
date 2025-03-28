package com.sinosoft.common.sync.config;

import com.sinosoft.common.sync.adapter.ServiceSyncAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SyncConfiguration {

    /**
     * 默认服务同步适配器（空实现）
     * 仅当没有其他实现时使用
     */
    @Bean
    @ConditionalOnMissingBean(ServiceSyncAdapter.class)
    public ServiceSyncAdapter defaultServiceSyncAdapter() {
        return new ServiceSyncAdapter.NoOpAdapter();
    }
}
