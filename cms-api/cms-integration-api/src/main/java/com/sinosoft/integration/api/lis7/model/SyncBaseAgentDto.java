package com.sinosoft.integration.api.lis7.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sinosoft.common.schema.agent.domain.Laagent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 人员基础信息同步
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class SyncBaseAgentDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 人员信息
     */
    @JsonProperty("lAAgents")
    private List<Laagent> lAAgents;
}
