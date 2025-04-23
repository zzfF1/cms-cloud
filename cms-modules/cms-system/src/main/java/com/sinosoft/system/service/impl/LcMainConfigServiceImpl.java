package com.sinosoft.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sinosoft.common.core.utils.MapstructUtils;
import com.sinosoft.common.schema.common.domain.LcCheck;
import com.sinosoft.common.schema.common.domain.LcDefine;
import com.sinosoft.common.schema.common.domain.LcDz;
import com.sinosoft.common.schema.common.domain.LcTz;
import com.sinosoft.common.mapper.*;
import com.sinosoft.system.domain.bo.LcDefineBo;
import com.sinosoft.system.domain.vo.LcCheckVo;
import com.sinosoft.system.domain.vo.LcDefineVo;
import com.sinosoft.system.domain.vo.LcDzVo;
import com.sinosoft.system.domain.vo.LcTzVo;
import com.sinosoft.system.service.ILcMainConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 流程定义Service业务层处理
 *
 * @author zzf
 * @date 2023-11-12
 */
@RequiredArgsConstructor
@Service
public class LcMainConfigServiceImpl implements ILcMainConfigService {

    private final LcMainMapper baseMapper;
    private final LcDefineMapper lcDefineMapper;
    private final LcTzMapper lcTzMapper;
    private final LcDzMapper lcDzMapper;
    private final LcCheckMapper lcCheckMapper;
    private final LcPropertyMapper lcPropertyMapper;

    /**
     * 查询流程定义
     */
    @Override
    public LcDefineVo queryById(Integer id) {
        LcDefine lcDefine = lcDefineMapper.selectById(id);
        return MapstructUtils.convert(lcDefine, LcDefineVo.class);
    }

    /**
     * 查询流程定义列表
     */
    @Override
    public List<LcDefineVo> queryList(Integer lcSerialNo) {
        LambdaQueryWrapper<LcDefine> lqw = Wrappers.lambdaQuery();
        lqw.eq(LcDefine::getLcSerialno, lcSerialNo);
        lqw.orderByAsc(LcDefine::getRecno);
        List<LcDefine> defineList = lcDefineMapper.selectList(lqw);
        List<LcDefineVo> lcDefineVos = MapstructUtils.convert(defineList, LcDefineVo.class);
        if (CollUtil.isNotEmpty(lcDefineVos)) {
            for (LcDefineVo lcDefineVo : lcDefineVos) {
                lcDefineVo.setActions(MapstructUtils.convert(lcDzMapper.selectList(Wrappers.lambdaQuery(LcDz.class).eq(LcDz::getLcId, lcDefineVo.getId()).orderByAsc(LcDz::getRecno)), LcDzVo.class));
                lcDefineVo.setChecks(MapstructUtils.convert(lcCheckMapper.selectList(Wrappers.lambdaQuery(LcCheck.class).eq(LcCheck::getLcId, lcDefineVo.getId()).orderByAsc(LcCheck::getRecno)), LcCheckVo.class));
                lcDefineVo.setJumps(MapstructUtils.convert(lcTzMapper.selectList(Wrappers.lambdaQuery(LcTz.class).eq(LcTz::getLcId, lcDefineVo.getId()).orderByAsc(LcTz::getRecno)), LcTzVo.class));
            }
        }
        return lcDefineVos;
    }

    /**
     * 新增流程定义
     */
    @Override
    public Boolean insertByBo(LcDefineBo bo) {
        LcDefine add = MapstructUtils.convert(bo, LcDefine.class);
        boolean flag = lcDefineMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改流程定义
     */
    @Override
    public Boolean updateByBo(LcDefineBo bo) {
        LcDefine update = MapstructUtils.convert(bo, LcDefine.class);
        return lcDefineMapper.updateById(update) > 0;
    }


    /**
     * 批量删除流程定义
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return lcDefineMapper.deleteBatchIds(ids) > 0;
    }
}
