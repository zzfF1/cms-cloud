package com.sinosoft.integration.api.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sinosoft.integration.api.lis7.model.OrphanPolicyAssignmentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * lis7响应结果类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class Lis7HttpResponse extends ResponseBaseDto {
    /**
     * 客户端信息
     */
    private ClientInfo clientInfo;
    
    /**
     * 响应结果
     */
    @JsonProperty("response")
    private OrphanPolicyAssignmentResponseDto.Response response;

    /**
     * 响应结果类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
    public static class Response implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty("result")
        private String result;
    }
}
