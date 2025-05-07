package com.sinosoft.system.service;

import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.system.domain.bo.SysImportConfigBo;
import com.sinosoft.system.domain.vo.SysImportConfigVo;

import java.util.Collection;
import java.util.List;

/**
 * excel导入模板Service接口
 *
 * @author zzf
 * @date 2024-01-04
 */
public interface ISysImportConfigService {

    /**
     * 查询excel导入模板
     */
    SysImportConfigVo queryById(Long id);

    /**
     * 查询excel导入模板列表
     */
    TableDataInfo<SysImportConfigVo> queryPageList(SysImportConfigBo bo, PageQuery pageQuery);

    /**
     * 查询excel导入模板列表
     */
    List<SysImportConfigVo> queryList(SysImportConfigBo bo);

    /**
     * 新增excel导入模板
     */
    Boolean insertByBo(SysImportConfigBo bo);

    /**
     * 修改excel导入模板
     */
    Boolean updateByBo(SysImportConfigBo bo);

    /**
     * 校验并批量删除excel导入模板信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 保存excel导入模板配置项
     */
    Boolean saveConfigItems(Long configId, List<SysImportConfigBo> sheetConfigs);

    /**
     * 生成Excel模板
     */
    byte[] generateExcelTemplate(Long id);
}
