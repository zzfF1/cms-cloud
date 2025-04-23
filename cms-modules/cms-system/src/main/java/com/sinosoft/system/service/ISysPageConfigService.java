package com.sinosoft.system.service;

import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.system.domain.bo.SysPageConfigBo;
import com.sinosoft.system.domain.vo.SysPageConfigVo;

import java.util.Collection;
import java.util.List;

/**
 * 界面配置Service接口
 *
 * @author zzf
 * @date 2024-07-02
 */
public interface ISysPageConfigService {

    /**
     * 查询界面配置
     */
    SysPageConfigVo queryById(Long id);

    /**
     * 查询界面配置列表
     */
    TableDataInfo<SysPageConfigVo> queryPageList(SysPageConfigBo bo, PageQuery pageQuery);

    /**
     * 查询界面配置列表
     */
    List<SysPageConfigVo> queryList(SysPageConfigBo bo);

    /**
     * 新增界面配置
     */
    Boolean insertByBo(SysPageConfigBo bo);

    /**
     * 修改界面配置
     */
    Boolean updateByBo(SysPageConfigBo bo);

    /**
     * 校验并批量删除界面配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
