package com.sinosoft.common.enums;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Getter;
import com.sinosoft.common.core.exception.ServiceException;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: cms6
 * @description: 条件枚举
 * @author: zzf
 * @create: 2024-06-24 21:46
 */
@Getter
public enum ConditionEnum {
    EQ("EQ") {
        @Override
        public <T> void apply(QueryWrapper<T> wrapper, String fieldName, Object value) {
            wrapper.eq(fieldName, value);
        }
    },
    IN("IN") {
        @Override
        public <T> void apply(QueryWrapper<T> wrapper, String fieldName, Object value) {
            wrapper.in(fieldName, (Object[]) value);
        }
    },
    NE("NE") {
        @Override
        public <T> void apply(QueryWrapper<T> wrapper, String fieldName, Object value) {
            wrapper.ne(fieldName, value);
        }
    },
    GT("GT") {
        @Override
        public <T> void apply(QueryWrapper<T> wrapper, String fieldName, Object value) {
            wrapper.gt(fieldName, value);
        }
    },
    GE("GE") {
        @Override
        public <T> void apply(QueryWrapper<T> wrapper, String fieldName, Object value) {
            wrapper.ge(fieldName, value);
        }
    },
    LT("LT") {
        @Override
        public <T> void apply(QueryWrapper<T> wrapper, String fieldName, Object value) {
            wrapper.lt(fieldName, value);
        }
    },
    LE("LE") {
        @Override
        public <T> void apply(QueryWrapper<T> wrapper, String fieldName, Object value) {
            wrapper.le(fieldName, value);
        }
    },
    LIKE("LIKE") {
        @Override
        public <T> void apply(QueryWrapper<T> wrapper, String fieldName, Object value) {
            wrapper.like(fieldName, value);
        }
    },
    LIKE_RIGHT("LIKE_RIGHT") {
        @Override
        public <T> void apply(QueryWrapper<T> wrapper, String fieldName, Object value) {
            wrapper.likeRight(fieldName, value);
        }
    },
    NOT_LIKE("NOT LIKE") {
        @Override
        public <T> void apply(QueryWrapper<T> wrapper, String fieldName, Object value) {
            wrapper.notLike(fieldName, value);
        }
    },
    NOT_IN("NOT IN") {
        @Override
        public <T> void apply(QueryWrapper<T> wrapper, String fieldName, Object value) {
            wrapper.notIn(fieldName, (Object[]) value);
        }
    };

    /**
     * 根据操作符获取枚举
     */
    private final static Map<String, ConditionEnum> NAME_MAP = Arrays.stream(ConditionEnum.values()).collect(Collectors.toMap(ConditionEnum::getOperator, Function.identity()));
    /**
     * 操作符
     */
    private final String operator;

    /**
     * 构造方法
     *
     * @param operator 操作符
     */
    ConditionEnum(String operator) {
        this.operator = operator;
    }

    /**
     * 根据操作符获取枚举
     *
     * @param operator 操作符
     * @return 枚举
     */
    public static ConditionEnum fromString(String operator) {
        if (NAME_MAP.containsKey(operator)) {
            return NAME_MAP.get(operator);
        } else {
            throw new ServiceException("没有找到条件" + operator + ",请检查条件是否正确");
        }
    }

    /**
     * 应用操作符
     *
     * @param wrapper   查询条件
     * @param fieldName 字段名
     * @param value     值
     * @param <T>       泛型
     */
    public abstract <T> void apply(QueryWrapper<T> wrapper, String fieldName, Object value);
}
