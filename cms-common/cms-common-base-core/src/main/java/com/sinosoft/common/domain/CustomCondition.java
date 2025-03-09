package com.sinosoft.common.domain;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.enums.ConditionOperatorEnum;

import java.util.List;

/**
 * @program: cms6
 * @description: 自定义条件
 * @author: zzf
 * @create: 2024-01-08 15:06
 */
@AllArgsConstructor
@Data
public class CustomCondition {
    /**
     * sheet索引
     */
    private int sheetIndex;
    /**
     * 字段
     */
    private String field;
    /**
     * 操作符
     */
    private ConditionOperatorEnum operatorEnum;
    /**
     * 值
     */
    private Object value;
    /**
     * 左括号
     */
    private String leftPar;
    /**
     * 右括号
     */
    private String rightPar;

    /**
     * 构造函数
     * @param field 字段
     * @param operatorEnum 操作符
     * @param value 值
     */
    public CustomCondition(String field, ConditionOperatorEnum operatorEnum, Object value) {
        this.sheetIndex = 0;
        this.field = field;
        this.operatorEnum = operatorEnum;
        this.value = value;
    }

    /**
     * 构造函数
     * @param sheetIndex sheet索引
     * @param field 字段
     * @param operatorEnum 操作符
     * @param value 值
     */
    public CustomCondition(int sheetIndex,String field, ConditionOperatorEnum operatorEnum, Object value) {
        this.sheetIndex = sheetIndex;
        this.field = field;
        this.operatorEnum = operatorEnum;
        this.value = value;
    }

    /**
     * 自定义条件
     *
     * @param sheetIndex   sheet索引
     * @param condtions    自定义条件
     * @param queryWrapper 查询条件
     */
    public static void conditionHandler(int sheetIndex, List<CustomCondition> condtions, QueryWrapper<Object> queryWrapper) {
        //如果为空返回
        if (condtions == null || condtions.isEmpty()) {
            return;
        }
        if (queryWrapper == null) {
            return;
        }
        //循环处理
        for (CustomCondition condition : condtions) {
            String field = condition.getField();
            Object value = condition.getValue();
            if (value != null && StringUtils.isNotBlank(value.toString())) {
                if (!"null".equals(value.toString())) {
                    //不相等跳过
                    if(sheetIndex != condition.getSheetIndex()){
                        continue;
                    }
                    ConditionOperatorEnum operatorEnum = condition.getOperatorEnum();
                    switch (operatorEnum) {
                        case EQ:
                            queryWrapper.eq(field, value);
                            break;
                        case NE:
                            queryWrapper.ne(field, value);
                            break;
                        case GT:
                            queryWrapper.gt(field, value);
                            break;
                        case GE:
                            queryWrapper.ge(field, value);
                            break;
                        case LT:
                            queryWrapper.lt(field, value);
                            break;
                        case LE:
                            queryWrapper.le(field, value);
                            break;
                        case LIKE:
                            queryWrapper.like(field, value);
                            break;
                        case NOTLIKE:
                            queryWrapper.notLike(field, value);
                            break;
                        case IN:
                            if(value instanceof List){
                                queryWrapper.in(field, (List) value);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
