package com.sinosoft.system.service;


import com.sinosoft.common.schema.common.domain.LcMain;
import com.sinosoft.system.domain.bo.LcMainBo;

import java.util.Collection;
import java.util.List;

/**
 * 流程定义Service接口
 *
 * @author zzf
 * @date 2023-11-12
 */
public interface ILcMainSetService {

    /**
     * 查询流程定义
     */
    LcMain queryById(Integer serialno);

    /**
     * 查询流程定义列表
     */
    List<LcMain> queryList(LcMainBo bo);

    /**
     * 新增流程定义
     */
    Boolean insertByBo(LcMainBo bo);

    /**
     * 修改流程定义
     */
    Boolean updateByBo(LcMainBo bo);

    /**
     * 校验并批量删除流程定义信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
