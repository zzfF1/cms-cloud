package com.sinosoft.api.agent.domain.vo;

import com.sinosoft.common.schema.agent.domain.Laagent;
import com.sinosoft.common.schema.agent.domain.LaagentToAgentInfoVoMapper;
import io.github.linpeilie.AutoMapperConfig__4156;
import io.github.linpeilie.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    config = AutoMapperConfig__4156.class,
    uses = {LaagentToAgentInfoVoMapper.class},
    imports = {}
)
public interface AgentInfoVoToLaagentMapper extends BaseMapper<AgentInfoVo, Laagent> {
  @Mapping(
      target = "agentcode",
      source = "agentCode"
  )
  Laagent convert(AgentInfoVo source);

  @Mapping(
      target = "agentcode",
      source = "agentCode"
  )
  Laagent convert(AgentInfoVo source, @MappingTarget Laagent target);
}
