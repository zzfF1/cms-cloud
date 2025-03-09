package com.sinosoft.common.domain.dto;

import lombok.Data;
import com.sinosoft.common.schema.commission.domain.LaRateCommisionConfig;
import com.sinosoft.common.schema.commission.domain.LaRateCommisionConfigItem;

import java.util.List;

/**
 * @program: cms6
 * @description: 费率配置DTO
 * @author: zzf
 * @create: 2023-12-31 10:45
 */
@Data
public class RateConfigDto {
    /**
     * 费率配置
     */
    LaRateCommisionConfig rateConfig;

    /**
     * 费率配置项
     */
    List<LaRateCommisionConfigItem> configItems;
}
