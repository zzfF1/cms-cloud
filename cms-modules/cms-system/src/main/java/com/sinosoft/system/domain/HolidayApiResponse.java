package com.sinosoft.system.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;
@Data
public class HolidayApiResponse {
    @JsonProperty("code")        // 对应 JSON 中的 "code" 字段
    private int code;            // 响应码

    @JsonProperty("holiday")     // 对应 JSON 中的 "holiday" 字段
    private Map<String, HolidayDetail> holidays; // 键为日期字符串，值为节假日详情

    @Data
    public static class HolidayDetail {
        @JsonProperty("holiday")    // 对应 JSON 中的 "holiday" 字段
        private boolean isHoliday;  // 是否为节假日

        @JsonProperty("name")       // 对应 JSON 中的 "name" 字段
        private String name;        // 节假日名称

        @JsonProperty("date")       // 对应 JSON 中的 "date" 字段
        private String date;        // 日期字符串（如 "2023-10-01"）

        @JsonProperty("wage")       // 对应 JSON 中的 "wage" 字段
        private int wage;           // 加班工资倍数

        @JsonProperty("rest")       // 对应 JSON 中的 "rest" 字段
        private int rest;           // 调休天数
    }

}
