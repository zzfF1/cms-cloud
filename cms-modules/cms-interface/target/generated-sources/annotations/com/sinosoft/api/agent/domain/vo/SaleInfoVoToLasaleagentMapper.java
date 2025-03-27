package com.sinosoft.api.agent.domain.vo;

import com.sinosoft.common.schema.broker.domain.Lasaleagent;
import com.sinosoft.common.schema.broker.domain.LasaleagentToSaleInfoVoMapper;
import io.github.linpeilie.AutoMapperConfig__4156;
import io.github.linpeilie.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    config = AutoMapperConfig__4156.class,
    uses = {LasaleagentToSaleInfoVoMapper.class},
    imports = {}
)
public interface SaleInfoVoToLasaleagentMapper extends BaseMapper<SaleInfoVo, Lasaleagent> {
  @Mapping(
      target = "salecode",
      source = "saleCode"
  )
  @Mapping(
      target = "agentcom",
      source = "agentCom"
  )
  Lasaleagent convert(SaleInfoVo source);

  @Mapping(
      target = "salecode",
      source = "saleCode"
  )
  @Mapping(
      target = "agentcom",
      source = "agentCom"
  )
  Lasaleagent convert(SaleInfoVo source, @MappingTarget Lasaleagent target);
}
