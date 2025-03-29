package com.sinosoft.common.sync.factory;

import com.sinosoft.common.core.utils.MapstructUtils;
import com.sinosoft.common.sync.config.TableMappingConfig;
import com.sinosoft.integration.api.core.ClientInfo;
import com.sinosoft.integration.api.core.Lis7HttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置DTO映射工厂 - 基于配置文件
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

    // 类到Builder方法的缓存
    private final Map<Class<?>, Method> builderMethodCache = new ConcurrentHashMap<>();

    // 构建器类到setter方法的缓存
    private final Map<Class<?>, Map<String, Method>> setterMethodCache = new ConcurrentHashMap<>();

    // 构建器类到build方法的缓存
    private final Map<Class<?>, Method> buildMethodCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 初始化缓存
        for (TableMappingConfig.TableMapping mapping : tableMappingConfig.getTableMappings()) {
            mappingCache.put(mapping.getName().toLowerCase(), mapping);
            log.info("加载表映射配置: {} -> {}", mapping.getName(), mapping.getDtoClass());
        }
        log.info("共加载 {} 个表映射配置", mappingCache.size());
    }

    /**
     * 创建请求对象
     * @param tableName 表名
     * @param dataList 数据列表
     * @return 请求对象
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

            // 创建DTO对象
            Object dto = createDtoObject(mapping, dataList);

            // 创建请求对象
            Lis7HttpRequest<Object> request = new Lis7HttpRequest<>();
            request.setClientInfo(clientInfo);
            request.setInputData(dto);

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
     * @param tableName 表名
     * @return 主键名
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
     * 创建DTO对象
     */
    private Object createDtoObject(TableMappingConfig.TableMapping mapping, List<?> dataList) throws Exception {
        // 获取DTO类
        Class<?> dtoClass = getClass(mapping.getDtoClass());

        // 获取Schema类
        Class<?> schemaClass = getClass(mapping.getSchemaClass());

        // 将数据转换为Schema对象
        List<?> schemaList = MapstructUtils.convert(dataList, schemaClass);

        // 使用反射创建DTO对象并设置属性
        return createDtoWithReflection(dtoClass, mapping.getPropertyName(), schemaList);
    }

    /**
     * 获取类对象
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

    /**
     * 使用反射创建DTO对象
     */
    private Object createDtoWithReflection(Class<?> dtoClass, String propertyName, List<?> schemaList) throws Exception {
        // 获取builder方法
        Method builderMethod = builderMethodCache.computeIfAbsent(dtoClass, cls -> {
            try {
                return cls.getMethod("builder");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("未找到类 " + cls.getName() + " 的builder方法", e);
            }
        });

        // 调用builder方法获取Builder对象
        Object builder = builderMethod.invoke(null);

        // 获取Builder类
        Class<?> builderClass = builder.getClass();

        // 获取setter方法
        Map<String, Method> setters = setterMethodCache.computeIfAbsent(builderClass, cls -> new HashMap<>());
        Method setterMethod = setters.computeIfAbsent(propertyName, propName -> {
            try {
                return builderClass.getMethod(propName, List.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("未找到类 " + builderClass.getName() + " 的方法: " + propName, e);
            }
        });

        // 调用setter方法
        setterMethod.invoke(builder, schemaList);

        // 获取build方法
        Method buildMethod = buildMethodCache.computeIfAbsent(builderClass, cls -> {
            try {
                return cls.getMethod("build");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("未找到类 " + cls.getName() + " 的build方法", e);
            }
        });

        // 调用build方法创建DTO对象
        return buildMethod.invoke(builder);
    }
}
