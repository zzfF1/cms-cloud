package com.sinosoft.common.schema.agent.domain;

import com.sinosoft.api.agent.domain.vo.AgentInfoVo;
import com.sinosoft.api.agent.domain.vo.AgentInfoVoToLaagentMapper;
import io.github.linpeilie.AutoMapperConfig__4156;
import io.github.linpeilie.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    config = AutoMapperConfig__4156.class,
    uses = {AgentInfoVoToLaagentMapper.class},
    imports = {}
)
public interface LaagentToAgentInfoVoMapper extends BaseMapper<Laagent, AgentInfoVo> {
  @Mapping(
      target = "agentCode",
      source = "agentcode"
  )
  AgentInfoVo convert(Laagent source);

  @Mapping(
      target = "agentCode",
      source = "agentcode"
  )
  AgentInfoVo convert(Laagent source, @MappingTarget AgentInfoVo target);
}
