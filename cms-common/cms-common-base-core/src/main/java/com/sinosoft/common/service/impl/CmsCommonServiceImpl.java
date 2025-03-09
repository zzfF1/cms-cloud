package com.sinosoft.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sinosoft.common.schema.common.domain.*;
import com.sinosoft.common.schema.common.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.schema.common.domain.vo.SysPageConfigTabVo;

import com.sinosoft.common.service.ICmsCommonService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统界面配置Service业务层处理
 *
 * @author zzf
 * @date 2023-07-13
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CmsCommonServiceImpl implements ICmsCommonService {

    private final SysPageConfigMapper pageMapper;
    private final SysPageConfigQueryMapper pageQueryMapper;
    private final SysPageConfigTabMapper pageTabMapper;
    private final SysExportConfigMapper sysExportConfigMapper;
    private final SysExportConfigSheetMapper sysExportConfigSheetMapper;
    private final SysExportConfigItemMapper sysExportConfigItemMapper;
    private final SysImportConfigMapper sysImportConfigMapper;
    private final SysImportConfigItemMapper sysImportConfigItemMapper;

    /**
     * 根据界面代码查询界面配置
     *
     * @param pageCode 界面代码
     * @return 界面配置
     */
//    @Cacheable(cacheNames = CacheNames.PAGE_CONFIG, key = "#pageCode")
    @Override
    public SysPageConfig selectPageConfigByCode(String pageCode) {
        LambdaQueryWrapper<SysPageConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysPageConfig::getCode, pageCode);
        return pageMapper.selectOne(lqw);
    }

    /**
     * 根据界面代码查询界面查询配置
     *
     * @param pageId 界面主键
     * @return 界面查询配置
     */
//    @Cacheable(cacheNames = CacheNames.PAGE_CONFIG_QUERY, key = "#pageId")
    @Override
    public List<SysPageConfigQuery> selectPageConfigQueryByPageId(Long pageId) {
        LambdaQueryWrapper<SysPageConfigQuery> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysPageConfigQuery::getPageId, pageId);
        lqw.orderByAsc(SysPageConfigQuery::getQueryOrder);
        return pageQueryMapper.selectList(lqw);
    }

    /**
     * 根据界面代码查询界面标签配置
     *
     * @param pageId 界面主键
     * @return 界面标签配置
     */
//    @Cacheable(cacheNames = CacheNames.PAGE_CONFIG_TABLE, key = "#pageId")
    @Override
    public List<SysPageConfigTabVo> selectPageConfigTabByPageId(Long pageId) {
        LambdaQueryWrapper<SysPageConfigTab> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysPageConfigTab::getPageId, pageId);
        lqw.orderByAsc(SysPageConfigTab::getSort);
        return pageTabMapper.selectVoList(lqw,SysPageConfigTabVo.class);
    }

    /**
     * 根据界面代码查询界面配置
     *
     * @param exportCode 导出代码
     * @return 界面配置
     */
//    @Cacheable(cacheNames = CacheNames.SYS_EXPORT_CONFIG, key = "#exportCode")
    @Override
    public SysExportConfig selectExportConfigByCode(String exportCode) {
        LambdaQueryWrapper<SysExportConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysExportConfig::getCode, exportCode);
        return sysExportConfigMapper.selectOne(lqw);
    }

    /**
     * 根据界面代码查询界面查询配置
     *
     * @param configId 配置主键
     * @return 界面查询配置
     */
//    @Cacheable(cacheNames = CacheNames.SYS_EXPORT_SHEET, key = "#configId")
    @Override
    public List<SysExportConfigSheet> selectExportSheetByConfigId(Long configId) {
        LambdaQueryWrapper<SysExportConfigSheet> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysExportConfigSheet::getConfigId, configId);
        lqw.orderByAsc(SysExportConfigSheet::getSheetIndex);
        return sysExportConfigSheetMapper.selectList(lqw);
    }

    /**
     * 根据界面代码查询界面标签配置
     *
     * @param configId 配置主键
     * @return 界面标签配置
     */
//    @Cacheable(cacheNames = CacheNames.SYS_EXPORT_ITEM, key = "#configId")
    @Override
    public List<SysExportConfigItem> selectExportItemByConfigId(Long configId) {
        LambdaQueryWrapper<SysExportConfigItem> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysExportConfigItem::getConfigId, configId);
        lqw.orderByAsc(SysExportConfigItem::getSort);
        return sysExportConfigItemMapper.selectList(lqw);
    }

    /**
     * 根据界面代码查询界面配置
     *
     * @param importCode 导入代码
     * @return 界面配置
     */
//    @Cacheable(cacheNames = CacheNames.SYS_IMPORT_CONFIG, key = "#importCode")
    @Override
    public SysImportConfig selectImportConfigByCode(String importCode) {
        LambdaQueryWrapper<SysImportConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysImportConfig::getCode, importCode);
        return sysImportConfigMapper.selectOne(lqw);
    }

    /**
     * 根据界面代码查询界面标签配置
     *
     * @param configId 配置主键
     * @return 界面标签配置
     */
//    @Cacheable(cacheNames = CacheNames.SYS_IMPORT_ITEM, key = "#configId")
    @Override
    public List<SysImportConfigItem> selectImportItemByConfigId(Long configId) {
        LambdaQueryWrapper<SysImportConfigItem> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysImportConfigItem::getConfigId, configId);
        lqw.orderByAsc(SysImportConfigItem::getSheetIndex, SysImportConfigItem::getSort);
        return sysImportConfigItemMapper.selectList(lqw);
    }
}
