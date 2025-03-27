package com.sinosoft.common.schema.broker.domain;

import com.sinosoft.api.agent.domain.vo.SaleInfoVo;
import com.sinosoft.api.agent.domain.vo.SaleInfoVoToLasaleagentMapper;
import io.github.linpeilie.AutoMapperConfig__4156;
import io.github.linpeilie.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    config = AutoMapperConfig__4156.class,
    uses = {SaleInfoVoToLasaleagentMapper.class},
    imports = {}
)
public interface LasaleagentToSaleInfoVoMapper extends BaseMapper<Lasaleagent, SaleInfoVo> {
  @Mapping(
      target = "saleCode",
      source = "salecode"
  )
  @Mapping(
      target = "agentCom",
      source = "agentcom"
  )
  SaleInfoVo convert(Lasaleagent source);

  @Mapping(
      target = "saleCode",
      source = "salecode"
  )
  @Mapping(
      target = "agentCom",
      source = "agentcom"
  )
  SaleInfoVo convert(Lasaleagent source, @MappingTarget SaleInfoVo target);
}
