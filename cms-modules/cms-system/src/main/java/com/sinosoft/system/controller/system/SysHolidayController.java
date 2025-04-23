package com.sinosoft.system.controller.system;


import com.sinosoft.system.domain.SysHolidayInfo;
import com.sinosoft.system.service.ISysHolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/holidays")
public class SysHolidayController {

    private final ISysHolidayService holidayService;

    @PostMapping("/range")
    public ResponseEntity<List<SysHolidayInfo>> getHolidaysInRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(holidayService.getHolidaysBetween(start, end));
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncHolidays() {
        holidayService.syncFromThirdParty();
        return ResponseEntity.ok("同步成功");
    }
}
