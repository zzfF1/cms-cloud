package com.sinosoft.common.sync.config;

import com.dtflys.forest.config.ForestConfiguration;
import com.sinosoft.common.sync.client.Lis7SyncClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Forest客户端配置
 */
@Configuration
public class Lis7SyncClientConfig {

    /**
     * 创建 Lis7SyncClient 实例
     */
    @Bean
    public Lis7SyncClient lis7SyncClient(ForestConfiguration forestConfiguration) {
        return forestConfiguration.createInstance(Lis7SyncClient.class);
    }
}
