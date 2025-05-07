package com.sinosoft.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sinosoft.common.schema.common.domain.vo.SysPageConfigTabVo;
import com.sinosoft.common.service.IBaseImpl;
import com.sinosoft.system.domain.bo.SysPageConfigBo;
import com.sinosoft.system.domain.bo.SysPageConfigQueryBo;
import com.sinosoft.system.domain.bo.SysPageConfigTabBo;
import com.sinosoft.system.domain.vo.SysPageConfigQueryVo;
import com.sinosoft.system.domain.vo.SysPageConfigVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.core.utils.MapstructUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.schema.common.domain.SysPageConfig;
import com.sinosoft.common.schema.common.domain.SysPageConfigQuery;
import com.sinosoft.common.schema.common.domain.SysPageConfigTab;
import com.sinosoft.common.schema.common.mapper.SysPageConfigMapper;
import com.sinosoft.common.schema.common.mapper.SysPageConfigQueryMapper;
import com.sinosoft.common.schema.common.mapper.SysPageConfigTabMapper;
import com.sinosoft.system.service.ISysPageConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 界面配置Service业务层处理
 *
 * @author zzf
 * @date 2024-07-02
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysPageConfigServiceImpl implements ISysPageConfigService, IBaseImpl {

    private final SysPageConfigMapper baseMapper;
    private final SysPageConfigQueryMapper queryMapper;
    private final SysPageConfigTabMapper tabMapper;

    /**
     * 查询界面配置
     */
    @Override
    public SysPageConfigVo queryById(Long id) {
        SysPageConfigVo pageConfigVo = baseMapper.selectVoById(id, SysPageConfigVo.class);
        if (pageConfigVo != null) {
            // 查询关联的查询条件
            LambdaQueryWrapper<SysPageConfigQuery> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(SysPageConfigQuery::getPageId, id);
            List<SysPageConfigQueryVo> queryList = queryMapper.selectVoList(queryWrapper, SysPageConfigQueryVo.class);
            pageConfigVo.setQueryList(queryList);

            // 查询关联的表格列
            LambdaQueryWrapper<SysPageConfigTab> tabWrapper = Wrappers.lambdaQuery();
            tabWrapper.eq(SysPageConfigTab::getPageId, id);
            tabWrapper.orderByAsc(SysPageConfigTab::getSort);
            List<SysPageConfigTabVo> tabList = tabMapper.selectVoList(tabWrapper, SysPageConfigTabVo.class);
            pageConfigVo.setTableList(tabList);
        }
        return pageConfigVo;
    }

    /**
     * 查询界面配置列表
     */
    @Override
    public TableDataInfo<SysPageConfigVo> queryPageList(SysPageConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysPageConfig> lqw = buildQueryWrapper(bo);
        Page<SysPageConfigVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw, SysPageConfigVo.class);
        return TableDataInfo.build(result);
    }

    /**
     * 查询界面配置列表
     */
    @Override
    public List<SysPageConfigVo> queryList(SysPageConfigBo bo) {
        LambdaQueryWrapper<SysPageConfig> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw, SysPageConfigVo.class);
    }

    private LambdaQueryWrapper<SysPageConfig> buildQueryWrapper(SysPageConfigBo bo) {
        LambdaQueryWrapper<SysPageConfig> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getCode()), SysPageConfig::getCode, bo.getCode());
        lqw.like(StringUtils.isNotBlank(bo.getName()), SysPageConfig::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getBranchType()), SysPageConfig::getBranchType, bo.getBranchType());
        lqw.orderByAsc(SysPageConfig::getSort);
        return lqw;
    }

    /**
     * 新增界面配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(SysPageConfigBo bo) {
        SysPageConfig add = MapstructUtils.convert(bo, SysPageConfig.class);
        validEntityBeforeSave(add);

        // 设置创建信息
        Date now = new Date();
        add.setMakedate(now);
        add.setMaketime(formatTime(now));
        add.setOperator("admin"); // 应从登录用户获取

        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());

            // 保存查询条件
            if (bo.getQueryList() != null && !bo.getQueryList().isEmpty()) {
                insertQueryList(bo.getId(), bo.getQueryList());
            }

            // 保存表格列
            if (bo.getTableList() != null && !bo.getTableList().isEmpty()) {
                insertTabList(bo.getId(), bo.getTableList());
            }
        }
        return flag;
    }

    /**
     * 修改界面配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(SysPageConfigBo bo) {
        SysPageConfig update = MapstructUtils.convert(bo, SysPageConfig.class);
        validEntityBeforeSave(update);

        // 设置修改信息
        Date now = new Date();
        update.setModifydate(now);
        update.setModifytime(formatTime(now));
        update.setModifyoperator("admin"); // 应从登录用户获取

        boolean flag = baseMapper.updateById(update) > 0;
        if (flag) {
            // 删除旧的查询条件
            LambdaQueryWrapper<SysPageConfigQuery> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(SysPageConfigQuery::getPageId, bo.getId());
            queryMapper.delete(queryWrapper);

            // 保存新的查询条件
            if (bo.getQueryList() != null && !bo.getQueryList().isEmpty()) {
                insertQueryList(bo.getId(), bo.getQueryList());
            }

            // 删除旧的表格列
            LambdaQueryWrapper<SysPageConfigTab> tabWrapper = Wrappers.lambdaQuery();
            tabWrapper.eq(SysPageConfigTab::getPageId, bo.getId());
            tabMapper.delete(tabWrapper);

            // 保存新的表格列
            if (bo.getTableList() != null && !bo.getTableList().isEmpty()) {
                insertTabList(bo.getId(), bo.getTableList());
            }
        }
        return flag;
    }

    /**
     * 保存查询条件
     */
    private void insertQueryList(Long pageId, List<SysPageConfigQueryBo> queryList) {
        List<SysPageConfigQuery> list = new ArrayList<>();
        Date now = new Date();
        String timeStr = formatTime(now);

        for (SysPageConfigQueryBo query : queryList) {
            SysPageConfigQuery entity = MapstructUtils.convert(query, SysPageConfigQuery.class);
            entity.setPageId(pageId);
            entity.setMakedate(now);
            entity.setMaketime(timeStr);
            entity.setOperator("admin"); // 应从登录用户获取
            list.add(entity);
        }

        for (SysPageConfigQuery entity : list) {
            queryMapper.insert(entity);
        }
    }

    /**
     * 保存表格列
     */
    private void insertTabList(Long pageId, List<SysPageConfigTabBo> tabList) {
        List<SysPageConfigTab> list = new ArrayList<>();
        Date now = new Date();
        String timeStr = formatTime(now);

        for (SysPageConfigTabBo tab : tabList) {
            SysPageConfigTab entity = MapstructUtils.convert(tab, SysPageConfigTab.class);
            entity.setPageId(pageId);
            entity.setMakedate(now);
            entity.setMaketime(timeStr);
            entity.setOperator("admin"); // 应从登录用户获取
            list.add(entity);
        }

        for (SysPageConfigTab entity : list) {
            tabMapper.insert(entity);
        }
    }

    /**
     * 格式化时间为HH:mm:ss
     */
    private String formatTime(Date date) {
        return date.toString().substring(11, 19);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysPageConfig entity) {
        // TODO 进行数据校验,如唯一约束等
    }

    /**
     * 批量删除界面配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // TODO 进行业务校验,判断是否可以删除
        }

        // 删除关联的查询条件
        LambdaQueryWrapper<SysPageConfigQuery> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(SysPageConfigQuery::getPageId, ids);
        queryMapper.delete(queryWrapper);

        // 删除关联的表格列
        LambdaQueryWrapper<SysPageConfigTab> tabWrapper = Wrappers.lambdaQuery();
        tabWrapper.in(SysPageConfigTab::getPageId, ids);
        tabMapper.delete(tabWrapper);

        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
