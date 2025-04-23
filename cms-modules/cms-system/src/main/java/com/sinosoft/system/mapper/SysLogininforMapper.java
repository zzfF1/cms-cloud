package com.sinosoft.system.mapper;

import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.system.domain.SysLogininfor;
import com.sinosoft.system.domain.bo.SysLogininforBo;
import com.sinosoft.system.domain.vo.SysLogininfoStatisticVo;
import com.sinosoft.system.domain.vo.SysLogininforVo;

import java.util.List;

/**
 * 系统访问日志情况信息 数据层
 *
 * @author zzf
 */
public interface SysLogininforMapper extends BaseMapperPlus<SysLogininfor, SysLogininforVo> {

    List<SysLogininfoStatisticVo> logininfoStatistic(SysLogininforBo bo);

}
