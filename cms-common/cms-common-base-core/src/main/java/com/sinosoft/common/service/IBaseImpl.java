package com.sinosoft.common.service;

import cn.hutool.core.date.DateUtil;
import com.sinosoft.common.mybatis.core.domain.BackEntity;
import com.sinosoft.common.mybatis.core.domain.BaseEntity2;
import com.sinosoft.common.enums.EdorTypeEnum;
import com.sinosoft.common.utils.BackUtils;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * 公共方法
 *
 * @program: cms6
 * @author: zzf
 * @create: 2024-08-26 14:00
 */
public interface IBaseImpl {

    /**
     * 初始化插入值
     * 设置operator、makedate、maketime、modifyoperator、modifydate、modifytime
     *
     * @param t        实体
     * @param operator 操作人
     * @param <S>      实体对象
     */
    default <S extends BaseEntity2> void setInsertValue(S t, String operator) {
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
    default <S extends BaseEntity2> void setUpdateValue(S t, String operator) {
        Assert.notNull(t, "需要赋值的对象信息不可以为空!");
        Assert.notNull(operator, "操作人信息不可以为空!");
        Date currDate = new Date();
        String currTime = DateUtil.formatTime(currDate);
        t.setModifyoperator(operator);
        t.setModifydate(currDate);
        t.setModifytime(currTime);
    }

    /**
     * 备份数据
     * 拷贝原数据 并且 设置备份类型、备份人、备份时间
     *
     * @param source       源数据
     * @param target       目标类型
     * @param edorTypeEnum 备份类型
     * @param operator     操作人
     * @param <S>          源数据类型
     * @param <T>          目标数据类型
     * @return 备份数据
     */
    default <S extends BaseEntity2, T extends BackEntity> T doBack(S source, Class<T> target, EdorTypeEnum edorTypeEnum, String operator) {
        Assert.notNull(source, "需要被备份的信息不可为空!");
        Assert.notNull(target, "目标对象信息不可以为空!");
        return BackUtils.doBack(source, target, edorTypeEnum, operator);
    }

    /**
     * 备份数据集合
     * 拷贝原数据 并且 设置备份类型、备份人、备份时间
     *
     * @param sourceList   源数据
     * @param target       目标类型
     * @param edorTypeEnum 备份类型
     * @param operator     操作人
     * @param <S>          源数据类型
     * @param <T>          目标数据类型
     * @return 备份数据
     */
    default <S extends BaseEntity2, T extends BackEntity> List<T> doBackList(List<S> sourceList, Class<T> target, EdorTypeEnum edorTypeEnum, String operator) {
        Assert.notNull(sourceList, "需要被备份的信息不可为空!");
        Assert.notNull(target, "目标对象信息不可以为空!");
        return sourceList.stream()
            .map(source -> BackUtils.doBack(source, target, edorTypeEnum, operator))
            .toList();
    }

    /**
     * 设置备份值
     *
     * @param t            实体
     * @param edorTypeEnum 备份类型
     * @param operator     操作人
     * @param <S>          实体对象
     */
    default <S extends BackEntity> void setBackValue(S t, EdorTypeEnum edorTypeEnum, String operator) {
        BackUtils.setBackValue(t, edorTypeEnum, operator);
    }
}
