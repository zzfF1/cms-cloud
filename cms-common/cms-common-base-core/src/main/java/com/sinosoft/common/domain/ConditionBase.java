package com.sinosoft.common.domain;

import lombok.Data;
import com.sinosoft.common.enums.ConditionEnum;
import com.sinosoft.common.enums.FieldTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: cms6
 * @description: excel配置对象
 * @author: zzf
 * @create: 2024-01-06 17:02
 */
@Data
public class ConditionBase {
    /**
     * 自定义条件
     */
    private List<CustomConditionVal> conditions;
    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 当前页数
     */
    private Integer pageNum;

    /**
     * 添加查询条件
     *
     * @param fieldType      字段类型
     * @param fieldPrefix    字段前缀
     * @param fieldName      字段名
     * @param condOperator   条件操作符
     * @param val            值
     * @param queryOrder     查询顺序
     * @param specialSqlCode 特殊sql代码
     */
    public void addCondition(FieldTypeEnum fieldType, String fieldPrefix, String fieldName, ConditionEnum condOperator, String val, Integer queryOrder, String specialSqlCode) {
        //如果为空初始化
        if (conditions == null) {
            conditions = new ArrayList<>();
        }
        conditions.add(new CustomConditionVal(fieldType.name(), fieldPrefix, "", fieldName, condOperator.name(), val, queryOrder, specialSqlCode));
    }

    /**
     * 添加查询条件
     *
     * @param fieldPrefix  字段前缀
     * @param fieldName    字段名
     * @param condOperator 条件操作符
     * @param val          值
     * @param queryOrder   查询顺序
     */
    public void addCondition(String fieldPrefix, String fieldName, ConditionEnum condOperator, String val, Integer queryOrder) {
        this.addCondition(FieldTypeEnum.STR, fieldPrefix, fieldName, condOperator, val, queryOrder, "");
    }

    /**
     * 添加查询条件
     *
     * @param fieldPrefix  字段前缀
     * @param fieldName    字段名
     * @param condOperator 条件操作符
     * @param val          值
     */
    public void addCondition(String fieldPrefix, String fieldName, ConditionEnum condOperator, String val) {
        this.addCondition(FieldTypeEnum.STR, fieldPrefix, fieldName, condOperator, val, 0, "");
    }
}
