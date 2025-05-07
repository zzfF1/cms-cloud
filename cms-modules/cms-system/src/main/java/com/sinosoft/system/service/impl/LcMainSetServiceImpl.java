package com.sinosoft.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sinosoft.common.core.utils.MapstructUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.schema.common.domain.LcMain;
import com.sinosoft.common.mapper.*;
import com.sinosoft.system.domain.bo.LcMainBo;
import com.sinosoft.system.service.ILcMainSetService;
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
public class LcMainSetServiceImpl implements ILcMainSetService {

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
    public LcMain queryById(Integer serialno) {
        return baseMapper.selectById(serialno);
    }

    /**
     * 查询流程定义列表
     */
    @Override
    public List<LcMain> queryList(LcMainBo bo) {
        return baseMapper.selectList(buildQueryWrapper(bo));
    }

    private LambdaQueryWrapper<LcMain> buildQueryWrapper(LcMainBo bo) {
        LambdaQueryWrapper<LcMain> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), LcMain::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getLcTable()), LcMain::getLcTable, bo.getLcTable());
        return lqw;
    }

    /**
     * 新增流程定义
     */
    @Override
    public Boolean insertByBo(LcMainBo bo) {
        LcMain add = MapstructUtils.convert(bo, LcMain.class);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setSerialno(add.getSerialno());
        }
        return flag;
    }

    /**
     * 修改流程定义
     */
    @Override
    public Boolean updateByBo(LcMainBo bo) {
        LcMain update = MapstructUtils.convert(bo, LcMain.class);
        return baseMapper.updateById(update) > 0;
    }


    /**
     * 批量删除流程定义
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
