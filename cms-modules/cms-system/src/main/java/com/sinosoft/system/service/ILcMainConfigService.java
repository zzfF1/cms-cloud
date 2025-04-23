package com.sinosoft.system.service;


import com.sinosoft.system.domain.bo.LcDefineBo;
import com.sinosoft.system.domain.vo.LcDefineVo;

import java.util.Collection;
import java.util.List;

/**
 * 流程定义Service接口
 *
 * @author zzf
 * @date 2023-11-12
 */
public interface ILcMainConfigService {

    /**
     * 查询流程定义
     */
    LcDefineVo queryById(Integer id);

    /**
     * 查询流程定义列表
     */
    List<LcDefineVo> queryList(Integer lcSerialNo);

    /**
     * 新增流程定义
     */
    Boolean insertByBo(LcDefineBo bo);

    /**
     * 修改流程定义
     */
    Boolean updateByBo(LcDefineBo bo);

    /**
     * 校验并批量删除流程定义信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
