package com.sinosoft.common.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sinosoft.common.domain.*;
import com.sinosoft.common.mapper.*;
import com.sinosoft.common.event.LcProcTrackEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.enums.LcCzTypeEnum;
import com.sinosoft.common.enums.LcTypeSerNoEnum;
import com.sinosoft.common.service.ILcMainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 流程类型定义Service业务层处理
 *
 * @author zzf
 * @date 2023-07-02
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class LcMainServiceImpl implements ILcMainService {

    private final LcMainMapper baseMapper;
    private final LcDefineMapper lcDefineMapper;
    private final LcCheckMapper lcCheckMapper;
    private final LcTzMapper lcTzMapper;
    private final LcDzMapper lcDzMapper;
    private final LcPropertyMapper lcPropertyMapper;
    private final LcProcesstrackMapper lcProcesstrackMapper;

    @Override
    public LcMain selectLcMainById(Long serialno) {
        return baseMapper.selectById(serialno);
    }

    @Override
    public LcDefine selectLcDefineById(Long serialno) {
        return lcDefineMapper.selectById(serialno);
    }

    @Override
    public List<String> getDataCurLcId(String querySql) {
        return baseMapper.getDataCurLcId(querySql);
    }

    @Override
    public List<String> getDataIdByLcIdAndTj(Integer lcId, String table, String idField, String lcField, String tj, String subIds) {
        //查询SQL
        String sqlbuf = "SELECT " + " " + idField + " FROM " + table + " WHERE 1=1 " +
            " and " + idField + " in (" + subIds + ") " +
            " and " + lcField + " = " + lcId + " " +
            tj;
        return getDataCurLcId(sqlbuf);
    }

    @Override
    public List<String> execSqlSelect(String sql) {
        return baseMapper.execSqlSelect(sql);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateDataLc(Integer lcId, Integer oldLcId, LcMain lcMain, LcCzTypeEnum nCzType, List<String> ids, String msg) {
        QueryWrapper<Object> queryWrapper = Wrappers.query();
        if (ids != null && !ids.isEmpty()) {
            queryWrapper.in(lcMain.getIdField(), ids);
        } else {
            //如果没有条件,则设置一个不可能存在的值
            queryWrapper.eq(lcMain.getIdField(), "");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(lcMain.getLcField()).append("=").append(lcId);
        //如果是驳回
        if (nCzType.getOperatorType() == LcCzTypeEnum.REJECT.getOperatorType()) {
            //如果有驳回字段
            if (StringUtils.isNotBlank(lcMain.getRejectField())) {
                //记录驳回的流程
                builder.append(",").append(lcMain.getRejectField()).append("=").append(oldLcId);
            }
            //驳回原因字段
            if (StringUtils.isNotBlank(lcMain.getRejectField2())) {
                //记录驳回的流程
                builder.append(",").append(lcMain.getRejectField2()).append("='").append(msg).append("'");
            }
        } else {
            //不是退回
            //如果有驳回字段
            if (StringUtils.isNotBlank(lcMain.getRejectField())) {
                //记录驳回的流程
                builder.append(",").append(lcMain.getRejectField()).append("=''");
            }
            //驳回原因字段
            if (StringUtils.isNotBlank(lcMain.getRejectField2())) {
                //记录驳回的流程
                builder.append(",").append(lcMain.getRejectField2()).append("=''");
            }
        }
        return baseMapper.execUpdateByWrapper(lcMain.getLcTable(), builder.toString(), queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer execSqlUpdate(String sql) {
        return baseMapper.execUpdate(sql);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateLcProcess(Integer lcId, LcTypeSerNoEnum lcSerialNo, Integer nNextLcId, boolean bSave, List<String> ids) {
        //修改条件
        LambdaUpdateWrapper<LcProcesstrack> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(LcProcesstrack::getLastFlag, 0);
        updateWrapper.in(LcProcesstrack::getDataId, ids);
        updateWrapper.eq(LcProcesstrack::getLcSerialNo, lcSerialNo.getCode());
        updateWrapper.ne(LcProcesstrack::getCzType, 0);
        if (nNextLcId != lcSerialNo.getFirstLc()) {
            updateWrapper.ne(LcProcesstrack::getLcId, lcId);
        }
        return lcProcesstrackMapper.update(null, updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertLcProcess(LcProcTrackEvent trackEvent) {
        if (ObjectUtil.isNotNull(trackEvent)) {
            List<String> ids = trackEvent.getIdsList();
            Date curDate = new Date();
            int lastFlag = trackEvent.getCzType() == -1 ? 0 : 1;
            List<LcProcesstrack> addList = new ArrayList<>();
            //循环新增过程轨迹
            for (String id : ids) {
                LcProcesstrack lcProcesstrack = new LcProcesstrack();
                lcProcesstrack.setLcSerialNo(trackEvent.getLcSerialNo());
                lcProcesstrack.setLastFlag(lastFlag);
                lcProcesstrack.setCzType(trackEvent.getCzType());
                lcProcesstrack.setDataId(id);
                lcProcesstrack.setLcId(trackEvent.getLcId());
                lcProcesstrack.setOperator(trackEvent.getOperator());
                lcProcesstrack.setMakeDate(curDate);
                lcProcesstrack.setYj(trackEvent.getSm());
                addList.add(lcProcesstrack);
            }
            if (!addList.isEmpty()) {
                lcProcesstrackMapper.insertBatch(addList);
            }
        }
    }

    @Override
    public List<LcCheck> getLcCheckListByLcId(Integer lcId, int type) {
        LambdaQueryWrapper<LcCheck> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LcCheck::getLcId, lcId);
        queryWrapper.eq(LcCheck::getType, type);
        queryWrapper.orderByAsc(LcCheck::getRecno);
        return lcCheckMapper.selectList(queryWrapper);
    }

    @Override
    public List<LcDz> getLcDzListByLcId(Integer lcId, int type) {
        LambdaQueryWrapper<LcDz> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LcDz::getLcId, lcId);
        queryWrapper.eq(LcDz::getType, type);
        queryWrapper.orderByAsc(LcDz::getRecno);
        return lcDzMapper.selectList(queryWrapper);
    }


    @Override
    public List<LcTz> getLcTzListByLcId(Integer lcId, boolean isTh) {
        return lcTzMapper.selectLcTzByLcId((long) lcId, isTh);
    }

    @Override
    public Integer getLcPreviousOrTheNext(boolean type, LcMain lcMain, LcDefine lcDefine) {
        try {
            //如果是退回
            if (type) {
                //查询是否有跳转节点
                LambdaQueryWrapper<LcDefine> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.select(LcDefine::getId);
                queryWrapper.eq(LcDefine::getNextId, lcDefine.getId());
                queryWrapper.orderByAsc(LcDefine::getRecno);
                LcDefine previousLcDefine = lcDefineMapper.selectOne(queryWrapper);
                //不为空代表有跳转节点
                if (previousLcDefine != null) {
                    return previousLcDefine.getId();
                }
            }
            LambdaQueryWrapper<LcDefine> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(LcDefine::getLcSerialno, lcMain.getSerialno());
            //如果是退回
            if (type) {
                queryWrapper.le(LcDefine::getRecno, lcDefine.getRecno());
                queryWrapper.orderByDesc(LcDefine::getRecno);
            } else {
                queryWrapper.ge(LcDefine::getRecno, lcDefine.getRecno());
                queryWrapper.orderByAsc(LcDefine::getRecno);
            }
            //返回两行
            queryWrapper.last("limit 2");
            //获取数据
            List<LcDefine> queryList = lcDefineMapper.selectList(queryWrapper);
            //如果有值
            if (!queryList.isEmpty()) {
                if (queryList.size() == 1) {
                    log.error("数据已经是{}}流程,不能{}}!", type ? "第一个" : "最后一个", type ? "退回" : "提交");
                    return -1;
                }
                //获取下一个节点
                int nextId = queryList.get(1).getId();
                //如果是退回就返回
                if (type) {
                    return nextId;
                }
                if (queryList.get(0).getNextId() != 0) {
                    return queryList.get(0).getNextId();
                } else {
                    return nextId;
                }
            }
            return -1;
        } catch (Exception e) {
            log.error("获取流程上一步或下一步失败,错误信息:{}", e.getMessage());
            return -1;
        }
    }

    @Override
    public Integer getFirstLcByType(Integer lcSerialno) {
        LambdaQueryWrapper<LcDefine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(LcDefine::getId);
        queryWrapper.eq(LcDefine::getLcSerialno, lcSerialno);
        queryWrapper.orderByAsc(LcDefine::getRecno);
        queryWrapper.last("limit 1");
        LcDefine lcDefine = lcDefineMapper.selectOne(queryWrapper);
        return lcDefine == null ? -1 : lcDefine.getId();
    }

//    /**
//     * 记录流程轨迹
//     *
//     * @param trackEvent 流程轨迹事件
//     */
//    @Async
//    @EventListener
//    public void recordProcess(LcProcTrackEvent trackEvent) {
////        //对象不为空
////        if (trackEvent != null) {
////            List<LcProcesstrack> addList = new ArrayList<>();
////            List<String> ids = trackEvent.getIdsList();
////            Date curDate = new Date();
////            int lastFlag = trackEvent.getCzType() == -1 ? 0 : 1;
////            //循环新增过程轨迹
////            for (String id : ids) {
////                LcProcesstrack lcProcesstrack = new LcProcesstrack();
////                lcProcesstrack.setLcSerialNo(trackEvent.getLcSerialNo());
////                lcProcesstrack.setLastFlag(lastFlag);
////                lcProcesstrack.setCzType(trackEvent.getCzType());
////                lcProcesstrack.setDataId(id);
////                lcProcesstrack.setLcId(trackEvent.getLcId());
////                lcProcesstrack.setOperator(trackEvent.getOperator());
////                lcProcesstrack.setMakeDate(curDate);
////                lcProcesstrack.setYj(trackEvent.getSm());
////                addList.add(lcProcesstrack);
////            }
////
////            this.insertLcProcess(addList);
////        }
//    }

    /**
     * 通过属性名和属性值查询流程定义
     *
     * @param attrName 属性名
     * @param val      值
     * @return 流程定义主键集合
     */
    @Override
    public List<Integer> selectLcPropertyByAttr(String attrName, String val) {
        LambdaQueryWrapper<LcProperty> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(LcProperty::getAttrName, attrName);
        queryWrapper.eq(LcProperty::getVal, val);
        List<LcProperty> dataList = lcPropertyMapper.selectList(queryWrapper);
        if (dataList.isEmpty()) {
            return Collections.emptyList();
        }
        return dataList.stream().map(LcProperty::getLcId).toList();
    }

}
