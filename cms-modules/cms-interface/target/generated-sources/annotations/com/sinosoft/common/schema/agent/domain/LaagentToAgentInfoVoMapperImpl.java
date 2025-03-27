package com.sinosoft.common.schema.agent.domain;

import com.sinosoft.api.agent.domain.vo.AgentInfoVo;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-28T07:25:07+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.7 (GraalVM Community)"
)
@Component
public class LaagentToAgentInfoVoMapperImpl implements LaagentToAgentInfoVoMapper {

    @Override
    public AgentInfoVo convert(Laagent source) {
        if ( source == null ) {
            return null;
        }

        AgentInfoVo agentInfoVo = new AgentInfoVo();

        agentInfoVo.setAgentCode( source.getAgentcode() );
        agentInfoVo.setName( source.getName() );

        return agentInfoVo;
    }

    @Override
    public AgentInfoVo convert(Laagent source, AgentInfoVo target) {
        if ( source == null ) {
            return target;
        }

        target.setAgentCode( source.getAgentcode() );
        target.setName( source.getName() );

        return target;
    }
}
