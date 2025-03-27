package com.sinosoft.api.agent.domain.vo;

import com.sinosoft.common.schema.agent.domain.Laagent;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-28T07:25:07+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.7 (GraalVM Community)"
)
@Component
public class AgentInfoVoToLaagentMapperImpl implements AgentInfoVoToLaagentMapper {

    @Override
    public Laagent convert(AgentInfoVo source) {
        if ( source == null ) {
            return null;
        }

        Laagent laagent = new Laagent();

        laagent.setAgentcode( source.getAgentCode() );
        laagent.setName( source.getName() );

        return laagent;
    }

    @Override
    public Laagent convert(AgentInfoVo source, Laagent target) {
        if ( source == null ) {
            return target;
        }

        target.setAgentcode( source.getAgentCode() );
        target.setName( source.getName() );

        return target;
    }
}
