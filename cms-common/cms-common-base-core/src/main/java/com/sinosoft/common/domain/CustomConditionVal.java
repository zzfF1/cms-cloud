package com.sinosoft.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: cms6
 * @description: 自定义条件
 * @author: zzf
 * @create: 2024-06-24 10:14
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomConditionVal {
    /**
     * 字段类型
     * INT,LONG,DATE,STRING
     */
    private String fieldType;
    /**
     * 字段前缀
     * 如agent tree
     */
    private String fieldPrefix;
    /**
     * 别名
     */
    private String alias;
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 条件
     * eq in ne等
     */
    private String condOperator;
    /**
     * 结果
     */
    private String val;
    /**
     * 查询顺序
     */
    private int queryOrder;
    /**
     * 特殊sql代码
     */
    private String specialSqlCode;
}
