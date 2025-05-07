package com.sinosoft.system.service;


import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.system.domain.bo.SysExportConfigBo;
import com.sinosoft.system.domain.vo.SysExportConfigSheetVo;
import com.sinosoft.system.domain.vo.SysExportConfigVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * excel导出配置Service接口
 *
 * @author demo
 * @date 2024-04-20
 */
public interface ISysExportConfigService {

    /**
     * 查询excel导出配置
     */
    SysExportConfigVo queryById(Long id);

    /**
     * 查询excel导出配置列表
     */
    TableDataInfo<SysExportConfigVo> queryPageList(SysExportConfigBo bo, PageQuery pageQuery);

    /**
     * 查询excel导出配置列表
     */
    List<SysExportConfigVo> queryList(SysExportConfigBo bo);

    /**
     * 新增excel导出配置
     */
    Long insertByBo(SysExportConfigBo bo);

    /**
     * 修改excel导出配置
     */
    Boolean updateByBo(SysExportConfigBo bo);

    /**
     * 校验并批量删除excel导出配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 生成Excel并返回文件路径
     */
    String generateExcel(Long id, Map<String, Object> params);

    /**
     * 上传模板文件
     */
    String uploadTemplate(String originalFilename, byte[] fileData);

    /**
     * 根据配置ID查询Sheet配置列表
     *
     * @param configId 配置ID
     * @return Sheet配置列表
     */
    List<SysExportConfigSheetVo> querySheetsByConfigId(Long configId);
}
