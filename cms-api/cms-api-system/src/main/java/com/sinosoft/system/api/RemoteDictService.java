package com.sinosoft.system.api;

import com.sinosoft.system.api.domain.vo.RemoteDictDataVo;

import java.util.List;

/**
 * 字典服务
 *
 * @author zzf
 */
public interface RemoteDictService {

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    List<RemoteDictDataVo> selectDictDataByType(String dictType);

}
