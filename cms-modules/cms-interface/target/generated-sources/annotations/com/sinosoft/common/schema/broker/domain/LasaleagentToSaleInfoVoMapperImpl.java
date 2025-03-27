package com.sinosoft.common.schema.broker.domain;

import com.sinosoft.api.agent.domain.vo.SaleInfoVo;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-28T07:25:07+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.7 (GraalVM Community)"
)
@Component
public class LasaleagentToSaleInfoVoMapperImpl implements LasaleagentToSaleInfoVoMapper {

    @Override
    public SaleInfoVo convert(Lasaleagent source) {
        if ( source == null ) {
            return null;
        }

        SaleInfoVo saleInfoVo = new SaleInfoVo();

        saleInfoVo.setSaleCode( source.getSalecode() );
        saleInfoVo.setAgentCom( source.getAgentcom() );
        saleInfoVo.setName( source.getName() );

        return saleInfoVo;
    }

    @Override
    public SaleInfoVo convert(Lasaleagent source, SaleInfoVo target) {
        if ( source == null ) {
            return target;
        }

        target.setSaleCode( source.getSalecode() );
        target.setAgentCom( source.getAgentcom() );
        target.setName( source.getName() );

        return target;
    }
}
