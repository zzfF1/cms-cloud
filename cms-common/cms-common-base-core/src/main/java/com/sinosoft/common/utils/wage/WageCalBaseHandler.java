package com.sinosoft.common.utils.wage;

import com.sinosoft.common.core.utils.reflect.ReflectUtils;

import java.util.Map;

/**
 * @program: cms6
 * @description: 佣金计算结果设置基础实现
 * @author: zzf
 * @create: 2023-07-21 17:10
 */
public abstract class WageCalBaseHandler {

    /**
     * 设置结果
     *
     * @param obj     对象
     * @param dataMap 数据
     */
    public void setFields(Object obj, Map<String, WageCalRedisResult> dataMap) {
        for (Map.Entry<String, WageCalRedisResult> entry : dataMap.entrySet()) {
            WageCalRedisResult redisResult = entry.getValue();
            String fieldName = redisResult.getTableColName();
            Object fieldValue = getFieldValue(redisResult);
            ReflectUtils.setFieldValue(obj, fieldName, fieldValue);
        }
    }

    /**
     * 设置结果
     *
     * @param redisResult 结果数据
     * @return 结果
     */
    private Object getFieldValue(WageCalRedisResult redisResult) {
        String strVal = String.valueOf(redisResult.getResultVal());
        if ("2".equals(redisResult.getDataType())) {
            return Double.valueOf(strVal);
        } else if ("1".equals(redisResult.getDataType())) {
            return Integer.valueOf(strVal);
        } else {
            return strVal;
        }
    }
}
