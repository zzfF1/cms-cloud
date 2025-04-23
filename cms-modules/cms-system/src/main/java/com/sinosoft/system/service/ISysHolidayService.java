package com.sinosoft.system.service;


import com.sinosoft.system.domain.SysHolidayInfo;

import java.time.LocalDate;
import java.util.List;

public interface ISysHolidayService {

    void syncFromThirdParty();
    /**
     * 查询时间段内节假日
     */
     List<SysHolidayInfo> getHolidaysBetween(LocalDate start, LocalDate end);
}
