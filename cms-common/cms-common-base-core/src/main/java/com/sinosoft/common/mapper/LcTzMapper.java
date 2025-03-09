package com.sinosoft.common.mapper;

import com.sinosoft.common.domain.LcTz;
import org.apache.ibatis.annotations.Param;
import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 流程跳转Mapper接口
 *
 * @author zzf
 * @date 2023-07-02
 */
public interface LcTzMapper extends BaseMapperPlus<LcTz, LcTz> {

    /**
     * 查询流程跳转
     *
     * @param lcId 流程ID
     * @param bTh  是否退回
     * @return 流程跳转
     */
    List<LcTz> selectLcTzByLcId(@Param("lcId") Long lcId, @Param("bTh") boolean bTh);

}
