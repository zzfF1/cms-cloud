package com.sinosoft.common.enums;

import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.core.utils.DateUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 自定义条件字段枚举
 */
public enum FieldTypeEnum {
    INT {
        @Override
        public Object convertValue(String value) {
            return Integer.parseInt(value);
        }

        @Override
        public Object[] convertValues(String[] values) {
            Integer[] result = new Integer[values.length];
            for (int i = 0; i < values.length; i++) {
                result[i] = Integer.parseInt(values[i]);
            }
            return result;
        }
    },
    LONG {
        @Override
        public Object convertValue(String value) {
            return Long.parseLong(value);
        }

        @Override
        public Object[] convertValues(String[] values) {
            Long[] result = new Long[values.length];
            for (int i = 0; i < values.length; i++) {
                result[i] = Long.parseLong(values[i]);
            }
            return result;
        }
    },
    DATE {
        @Override
        public Object convertValue(String value) {
            // 假设日期格式为 yyyy-MM-dd，您可以根据需要调整日期格式
            return DateUtils.parseDate(value);
        }

        @Override
        public Object[] convertValues(String[] values) {
            Date[] result = new Date[values.length];
            for (int i = 0; i < values.length; i++) {
                result[i] = DateUtils.parseDate(values[i]);
            }
            return result;
        }
    },
    STR {
        @Override
        public Object convertValue(String value) {
            return value;
        }

        @Override
        public Object[] convertValues(String[] values) {
            return values;
        }
    };

    /**
     * 根据类型获取枚举
     */
    private final static Map<String, FieldTypeEnum> NAME_MAP = Arrays.stream(FieldTypeEnum.values()).collect(Collectors.toMap(FieldTypeEnum::name, Function.identity()));

    /**
     * 根据类型获取枚举
     *
     * @param fieldType 字段类型
     * @return 枚举
     */
    public static FieldTypeEnum fromString(String fieldType) {
        if (NAME_MAP.containsKey(fieldType)) {
            return NAME_MAP.get(fieldType);
        } else {
            throw new ServiceException("没有找到条件" + fieldType + ",请检查条件是否正确");
        }
    }

    public abstract Object convertValue(String value);

    public abstract Object[] convertValues(String[] values);
}
