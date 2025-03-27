package com.sinosoft.integration.api.lis7.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sinosoft.integration.api.core.ClientInfo;
import com.sinosoft.integration.api.core.ResponseBaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 响应结果类
 * 对应系统返回的JSON响应结构
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class OrphanPolicyAssignmentResponseDto extends ResponseBaseDto {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 响应结果
     */
    @JsonProperty("response")
    private Response response;

    /**
     * 响应结果类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
    public static class Response  implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty("result")
        private String result;
    }
}
