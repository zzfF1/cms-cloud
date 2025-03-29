package com.sinosoft.common.sync.factory;

import com.sinosoft.common.core.utils.MapstructUtils;
import com.sinosoft.common.sync.config.TableMappingConfig;
import com.sinosoft.integration.api.core.ClientInfo;
import com.sinosoft.integration.api.core.Lis7HttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置映射工厂 - 无DTO简化版
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigDtoMappingFactory {

    private final TableMappingConfig tableMappingConfig;

    // 表名到映射配置的缓存
    private final Map<String, TableMappingConfig.TableMapping> mappingCache = new ConcurrentHashMap<>();

    // 类名到Class对象的缓存
    private final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 初始化缓存
        mappingCache.clear();
        for (TableMappingConfig.TableMapping mapping : tableMappingConfig.getTableMappings()) {
            mappingCache.put(mapping.getName().toLowerCase(), mapping);
            try {
                // 预加载Schema类
                getClass(mapping.getSchemaClass());
                log.info("加载表映射配置: {} -> {}", mapping.getName(), mapping.getSchemaClass());
            } catch (Exception e) {
                log.error("加载表映射配置失败: {}", e.getMessage(), e);
            }
        }
        log.info("共加载 {} 个表映射配置", mappingCache.size());
    }

    /**
     * 创建请求对象 - 直接使用Map替代DTO
     */
    public Lis7HttpRequest<?> createRequest(String tableName, List<?> dataList) {
        try {
            // 获取表映射配置
            TableMappingConfig.TableMapping mapping = getTableMapping(tableName);
            if (mapping == null) {
                throw new IllegalArgumentException("未找到表 " + tableName + " 的映射配置");
            }

            // 创建客户端信息
            ClientInfo clientInfo = createClientInfo(mapping.getBusinessCode());

            // 获取Schema类并转换数据
            Class<?> schemaClass = getClass(mapping.getSchemaClass());
            List<?> schemaList = MapstructUtils.convert(dataList, schemaClass);

            // 直接使用Map作为DTO
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put(mapping.getPropertyName(), schemaList);

            // 创建请求对象
            Lis7HttpRequest<Object> request = new Lis7HttpRequest<>();
            request.setClientInfo(clientInfo);
            request.setInputData(dataMap);

            return request;
        } catch (Exception e) {
            log.error("创建请求对象失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建请求对象失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取表映射配置
     */
    private TableMappingConfig.TableMapping getTableMapping(String tableName) {
        return mappingCache.get(tableName.toLowerCase());
    }

    /**
     * 根据表名获取主键名
     */
    public String getPrimaryKeyByTable(String tableName) {
        TableMappingConfig.TableMapping mapping = getTableMapping(tableName);
        if (mapping == null) {
            return null;
        }
        return mapping.getPrimaryKey();
    }

    /**
     * 创建客户端信息
     */
    private ClientInfo createClientInfo(String businessCode) {
        return ClientInfo.builder()
            .dealType("salem")
            .date(new SimpleDateFormat("yyyyMMdd").format(new Date()))
            .time(new SimpleDateFormat("HHmmss").format(new Date()))
            .seqNo(UUID.randomUUID().toString())
            .businessCode(businessCode)
            .subBusinessCode("1")
            .build();
    }

    /**
     * 获取类对象 - 使用缓存
     */
    private Class<?> getClass(String className) {
        return classCache.computeIfAbsent(className, name -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("未找到类: " + name, e);
            }
        });
    }
}
