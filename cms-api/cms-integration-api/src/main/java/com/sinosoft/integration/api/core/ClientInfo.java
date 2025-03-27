package com.sinosoft.integration.api.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * lis7请求头信息
 *
 * @author: zzf
 * @create: 2025-03-26 15:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @JsonProperty("dealType")
    private String dealType;

    @JsonProperty("date")
    private String date;

    @JsonProperty("time")
    private String time;

    @JsonProperty("seqNo")
    private String seqNo;

    @JsonProperty("businessCode")
    private String businessCode;

    @JsonProperty("subBusinessCode")
    private String subBusinessCode;

    @JsonProperty("errorList")
    private List<Error> errorList;

    /**
     * 响应结果类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
    public static class Error implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty("errorMessage")
        private String errorMessage;
    }
}
