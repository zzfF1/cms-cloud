package com.sinosoft.system.mapper;

import org.apache.ibatis.annotations.Param;
import com.sinosoft.common.schema.common.domain.vo.LcProcessShowVo;
import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.common.schema.common.domain.Ldcom;

import java.util.List;

/**
 * @program: cms6
 * @description: 公共服务
 * @author: zzf
 * @create: 2023-10-02 10:20
 */
public interface CommonMapper extends BaseMapperPlus<Ldcom, Ldcom> {

    List<LcProcessShowVo> queryProcess(@Param("dataId") String dataId, @Param("lcSerialNo") Integer lcSerialNo);
}
