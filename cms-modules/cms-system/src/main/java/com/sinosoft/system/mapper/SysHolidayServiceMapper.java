package com.sinosoft.system.mapper;

import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.system.domain.SysHolidayInfo;
import com.sinosoft.system.domain.vo.SysHolidayInfoVo;

import java.time.LocalDate;
import java.util.List;


public interface SysHolidayServiceMapper extends BaseMapperPlus<SysHolidayInfo, SysHolidayInfoVo> {
    /**
     * 根据日期查询节假日
     */
    SysHolidayInfo findByDate(LocalDate date);

    /**
     * 查询某个日期范围内的节假日
     */
    List<SysHolidayInfo> findByDateBetween(LocalDate start, LocalDate end);

    /**
     * 查询某个日期范围内的节假日（仅节假日）
     */
    List<SysHolidayInfo> findByDateBetweenAndIsHolidayTrue(LocalDate start, LocalDate end);

    /**
     * 查询某个日期范围内的节假日（仅工作日）
     */
    List<SysHolidayInfo> findByDateBetweenAndIsHolidayFalse(LocalDate start, LocalDate end);
}
