package com.sinosoft.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("system-info")
public class SystemConfig {
    /**
     * 一个用户只可分配一个角色 true是
     */
    private Boolean onlyRole = false;
    /**
     * 权限互斥
     */
    private Boolean authExclusive = false;
    /**
     * 管理员账户ID
     */
    private List<Long> systemUsers;

}
