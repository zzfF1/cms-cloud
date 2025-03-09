package com.sinosoft.common.utils;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.mybatis.core.domain.BackEntity;
import com.sinosoft.common.mybatis.core.domain.BaseEntity2;
import com.sinosoft.common.enums.EdorTypeEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对象备份
 *
 * @program: ncms
 * @author: zzf
 * @create: 2023-06-15 15:47
 */
@Slf4j
public abstract class BackUtils {

    /**
     * 备份数据
     *
     * @param source       源数据
     * @param target       目标数据
     * @param edorTypeEnum 备份类型
     * @param operator     操作人
     * @param <S>          源数据类型
     * @param <T>          目标数据类型
     * @return 备份数据
     */
    public static <S extends BaseEntity2, T extends BackEntity> T doBack(S source, Class<T> target, EdorTypeEnum edorTypeEnum, String operator) {
        Assert.notNull(source, "A表信息不能为空！");
        Assert.notNull(target, "B表对象信息不能为空!");
        try {
            T t = target.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, t);
            t.setEdortype(edorTypeEnum.getCode());
            t.setLastoperator(operator);
            t.setLastmakedatetime(new Date());
            return t;
        } catch (Exception e) {
            log.error("{},实例化异常！", target, e);
            return null;
        }
    }

    /**
     * 备份数据
     *
     * @param source       源数据
     * @param target       目标数据
     * @param edorTypeEnum 备份类型
     * @param operator     操作人
     * @param <S>          源数据类型
     * @param <T>          目标数据类型
     * @return 备份数据
     */
    public static <S extends BaseEntity2, T extends BackEntity> T doBack(S source, Class<T> target, EdorTypeEnum edorTypeEnum, long operator) {
        Assert.notNull(source, "A表信息不能为空！");
        Assert.notNull(target, "B表对象信息不能为空!");
        try {
            T t = target.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, t);
            t.setEdortype(edorTypeEnum.getCode());
            t.setLastoperator(String.valueOf(operator));
            t.setLastmakedatetime(new Date());
            return t;
        } catch (Exception e) {
            log.error("{},实例化异常！", target, e);
            return null;
        }
    }

    /**
     * 批量备份数据
     *
     * @param sourceList   源数据
     * @param target       目标数据
     * @param edorTypeEnum 备份类型
     * @param operator     操作人
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S extends BaseEntity2, T extends BackEntity> List<T> doBackList(List<S> sourceList, Class<T> target, EdorTypeEnum edorTypeEnum, long operator) {
        Assert.notNull(sourceList, "A表信息不能为空！");
        Assert.notNull(target, "B表对象信息不能为空!");

        return sourceList.stream()
            .map(source -> doBack(source, target, edorTypeEnum, operator))
            .collect(Collectors.toList());
    }

    /**
     * 初始化插入值
     * 设置operator、makedate、maketime、modifyoperator、modifydate、modifytime
     *
     * @param t        实体
     * @param operator 操作人
     * @param <S>      实体对象
     */
    public static <S extends BaseEntity2> void setInsertValue(S t, String operator) {
        Assert.notNull(t, "需要赋值的对象信息不可以为空!");
        Assert.notNull(operator, "操作人信息不可以为空!");
        Date currDate = new Date();
        String currTime = DateUtil.formatTime(currDate);
        t.setOperator(operator);
        t.setMakedate(currDate);
        t.setMaketime(currTime);
        t.setModifyoperator(operator);
        t.setModifydate(currDate);
        t.setModifytime(currTime);
    }

    /**
     * 更新
     * 设置modifyoperator、modifydate、modifytime
     *
     * @param t        实体
     * @param operator 操作人
     * @param <S>      实体对象
     */
    public static <S extends BaseEntity2> void setUpdateValue(S t, String operator) {
        Assert.notNull(t, "需要赋值的对象信息不可以为空!");
        Assert.notNull(operator, "操作人信息不可以为空!");
        Date currDate = new Date();
        String currTime = DateUtil.formatTime(currDate);
        t.setModifyoperator(operator);
        t.setModifydate(currDate);
        t.setModifytime(currTime);
    }

    /**
     * 设置备份字段
     *
     * @param t        实体
     * @param operator 操作人
     * @param <S>      实体对象
     */
    public static <S extends BackEntity> void setBackValue(S t, String operator) {
        setBackValue(t, EdorTypeEnum.ED99, operator);
    }

    /**
     * 设置备份字段
     *
     * @param t            实体
     * @param edorTypeEnum 备份类型
     * @param operator     操作人
     * @param <S>          实体对象
     */
    public static <S extends BackEntity> void setBackValue(S t, EdorTypeEnum edorTypeEnum, String operator) {
        Assert.notNull(t, "需要赋值的对象信息不可以为空!");
        Assert.notNull(operator, "操作人信息不可以为空!");
        t.setEdortype(edorTypeEnum.getCode());
        t.setLastoperator(operator);
        t.setLastmakedatetime(new Date());
    }
}
