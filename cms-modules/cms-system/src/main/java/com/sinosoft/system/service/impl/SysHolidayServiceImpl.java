package com.sinosoft.system.service.impl;

import com.sinosoft.system.domain.HolidayApiResponse;
import com.sinosoft.system.domain.SysHolidayInfo;
import com.sinosoft.system.mapper.SysHolidayServiceMapper;
import com.sinosoft.system.service.ISysHolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
@RequiredArgsConstructor
@Service
public class SysHolidayServiceImpl implements ISysHolidayService {
    private final SysHolidayServiceMapper mapper;

    @Scheduled(cron = "0 0 2 1 * ?") // 每月自动同步
    public void syncFromThirdParty() {
        //免费获取日期API
        String apiUrl = "https://timor.tech/api/holiday/year/2023";
        RestTemplate restTemplate = new RestTemplate();
        HolidayApiResponse response = restTemplate.getForObject(apiUrl, HolidayApiResponse.class);
        response.getHolidays().forEach((s, holidayDetail) -> {
            SysHolidayInfo holiday = new SysHolidayInfo();
            holiday.setDate(holidayDetail.getDate());
            holiday.setHolidayName(holidayDetail.getName());
            holiday.setHoliday(holidayDetail.isHoliday());
            mapper.insert(holiday);
        });

    }

    @Override
    public List<SysHolidayInfo> getHolidaysBetween(LocalDate start, LocalDate end) {
        return mapper.findByDateBetween(start,end);
    }
}
