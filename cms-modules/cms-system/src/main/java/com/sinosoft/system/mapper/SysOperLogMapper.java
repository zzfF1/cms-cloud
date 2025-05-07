package com.sinosoft.system.mapper;

import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.system.domain.SysOperLog;
import com.sinosoft.system.domain.bo.SysOperLogBo;
import com.sinosoft.system.domain.vo.SysOperLogStatisticVo;
import com.sinosoft.system.domain.vo.SysOperLogVo;

import java.util.List;

/**
 * 操作日志 数据层
 *
 * @author zzf
 */
public interface SysOperLogMapper extends BaseMapperPlus<SysOperLog, SysOperLogVo> {

    /**
     * 操作日志统计
     * @param bo
     * @return
     */
    List<SysOperLogStatisticVo> operLogStatistic(SysOperLogBo bo);

}
