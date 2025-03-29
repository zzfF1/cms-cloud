package com.sinosoft.common.sync.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 表映射配置类 - 简化版
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sync")
@RefreshScope
public class TableMappingConfig {

    /**
     * 表映射配置列表
     */
    private List<TableMapping> tableMappings = new ArrayList<>();

    /**
     * 表映射配置
     */
    @Data
    public static class TableMapping {
        /**
         * 表名
         */
        private String name;

        /**
         * 业务代码
         */
        private String businessCode;

        /**
         * Schema类全名
         */
        private String schemaClass;

        /**
         * 属性名(在请求中的JSON字段名)
         */
        private String propertyName;

        /**
         * 主键属性名
         */
        private String primaryKey;

        /**
         * 描述
         */
        private String description;
    }
}
