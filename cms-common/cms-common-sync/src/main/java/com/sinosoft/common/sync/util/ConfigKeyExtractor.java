package com.sinosoft.common.sync.util;

import cn.hutool.core.bean.BeanUtil;
import com.sinosoft.common.sync.factory.ConfigDtoMappingFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于配置的主键提取工具
 * 完全依赖配置文件，无硬编码映射
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigKeyExtractor {

    private final ConfigDtoMappingFactory configDtoMappingFactory;

    /**
     * 批量提取主键值
     *
     * @param dataList        数据列表
     * @param tableName       表名
     * @param explicitKeyName 显式指定的主键名（优先级高）
     * @return 主键值列表
     */
    public List<String> extractPrimaryKeys(List<?> dataList, String tableName, String explicitKeyName) {
        if (dataList == null || dataList.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            // 确定主键名：优先使用显式指定的，否则从配置中获取
            String primaryKeyName = explicitKeyName;
            if (primaryKeyName == null || primaryKeyName.isEmpty()) {
                primaryKeyName = configDtoMappingFactory.getPrimaryKeyByTable(tableName);
                if (primaryKeyName == null) {
                    // 如果配置中也没有，使用默认规则
                    primaryKeyName = tableName + "_id";
                    log.warn("未找到表 {} 的主键配置，使用默认主键名: {}", tableName, primaryKeyName);
                }
            }

            // 使用Hutool批量提取主键
            return extractByHutool(dataList, primaryKeyName);

        } catch (Exception e) {
            log.error("批量提取主键值失败: {}", e.getMessage(), e);
            // 构造一个默认主键列表作为回退策略
            return createFallbackKeys(dataList.size());
        }
    }

    /**
     * 使用Hutool批量提取主键
     */
    private List<String> extractByHutool(List<?> dataList, String primaryKeyName) {
        List<String> primaryKeys = new ArrayList<>(dataList.size());

        for (Object data : dataList) {
            try {
                // 使用Hutool的BeanUtil获取属性值
                Object keyValue = BeanUtil.getProperty(data, primaryKeyName);
                primaryKeys.add(keyValue != null ? String.valueOf(keyValue) : null);
            } catch (Exception e) {
                log.warn("提取对象 {} 的主键 {} 失败: {}", data.getClass().getSimpleName(), primaryKeyName, e.getMessage());
                primaryKeys.add("unknown-" + System.nanoTime());
            }
        }

        return primaryKeys;
    }

    /**
     * 创建回退主键列表（当提取失败时使用）
     */
    private List<String> createFallbackKeys(int size) {
        List<String> keys = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            keys.add("unknown-" + i + "-" + System.nanoTime());
        }
        return keys;
    }
}
