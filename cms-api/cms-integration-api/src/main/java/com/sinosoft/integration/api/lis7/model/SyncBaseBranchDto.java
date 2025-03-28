package com.sinosoft.integration.api.lis7.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sinosoft.common.schema.agent.domain.Latree;
import com.sinosoft.common.schema.team.domain.Labranchgroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 销售机构
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class SyncBaseBranchDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 人员行政信息
     */
    @JsonProperty("lABranchGroups")
    private List<Labranchgroup> lABranchGroups;
}
