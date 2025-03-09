package com.sinosoft.common.service;

import com.sinosoft.common.schema.common.domain.*;
import com.sinosoft.common.schema.common.domain.vo.SysPageConfigTabVo;

import java.util.List;

/**
 * 销管公用
 *
 * @author zzf
 * @date 2023-07-13
 */
public interface ICmsCommonService {

    /**
     * @param pageCode
     * @return
     */
    SysPageConfig selectPageConfigByCode(String pageCode);

    /**
     * @param exportCode
     * @return
     */
    SysExportConfig selectExportConfigByCode(String exportCode);

    /**
     * @param pageId
     * @return
     */
    List<SysPageConfigQuery> selectPageConfigQueryByPageId(Long pageId);

    /**
     * @param configId
     * @return
     */
    List<SysExportConfigSheet> selectExportSheetByConfigId(Long configId);

    /**
     * @param pageId
     * @return
     */
    List<SysPageConfigTabVo> selectPageConfigTabByPageId(Long pageId);

    /**
     * @param configId
     * @return
     */
    List<SysExportConfigItem> selectExportItemByConfigId(Long configId);

    /**
     * @param importCode
     * @return
     */
    SysImportConfig selectImportConfigByCode(String importCode);

    /**
     * @param configId
     * @return
     */
    List<SysImportConfigItem> selectImportItemByConfigId(Long configId);
}
