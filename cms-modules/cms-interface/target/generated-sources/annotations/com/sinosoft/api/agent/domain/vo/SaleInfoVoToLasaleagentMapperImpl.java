package com.sinosoft.api.agent.domain.vo;

import com.sinosoft.common.schema.broker.domain.Lasaleagent;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-28T07:25:07+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.7 (GraalVM Community)"
)
@Component
public class SaleInfoVoToLasaleagentMapperImpl implements SaleInfoVoToLasaleagentMapper {

    @Override
    public Lasaleagent convert(SaleInfoVo source) {
        if ( source == null ) {
            return null;
        }

        Lasaleagent lasaleagent = new Lasaleagent();

        lasaleagent.setSalecode( source.getSaleCode() );
        lasaleagent.setAgentcom( source.getAgentCom() );
        lasaleagent.setName( source.getName() );

        return lasaleagent;
    }

    @Override
    public Lasaleagent convert(SaleInfoVo source, Lasaleagent target) {
        if ( source == null ) {
            return target;
        }

        target.setSalecode( source.getSaleCode() );
        target.setAgentcom( source.getAgentCom() );
        target.setName( source.getName() );

        return target;
    }
}
