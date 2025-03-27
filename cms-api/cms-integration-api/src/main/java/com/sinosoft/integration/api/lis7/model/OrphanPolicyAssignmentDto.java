package com.sinosoft.integration.api.lis7.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sinosoft.integration.api.core.ClientInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 孤儿单分配对象类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class OrphanPolicyAssignmentDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private List<GrpContNoItem> grpContNos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrpContNoItem  implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        /**
         * 团单号
         */
        private String grpContNo;
        /**
         * 业务员
         */
        private String agentCode;
    }
}
