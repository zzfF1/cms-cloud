package com.sinosoft.common.sync.config;

import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.sinosoft.common.sync.factory.ConfigDtoMappingFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SyncConfigListener {

    private final SyncConfigManager syncConfigManager;
    private final TableMappingConfig tableMappingConfig;
    private final ConfigDtoMappingFactory configDtoMappingFactory;

    @NacosConfigListener(dataId = "cms-sync.yml", groupId = "DEFAULT_GROUP")
    public void onSyncConfigChange(String newConfig) {
        log.info("检测到同步配置变更，重新加载配置");
        try {
            // 重新初始化配置
            syncConfigManager.initialize();
            configDtoMappingFactory.init();
            log.info("同步配置重新加载完成");
        } catch (Exception e) {
            log.error("重新加载同步配置失败", e);
        }
    }
}
