package com.sinosoft.common.service;

import com.sinosoft.common.schema.commission.domain.LaRateCommision;
import com.sinosoft.common.schema.commission.domain.LaRateCommisionConfig;
import com.sinosoft.common.schema.commission.domain.LaRateCommisionConfigItem;

import java.util.List;

/**
 * 提奖配置服务
 */
public interface IRateConfigBaseService {

    /**
     * 根据代码查询提奖配置
     *
     * @param calCode 代码
     * @return 提奖配置对象
     */
    LaRateCommisionConfig queryConfigByCode(String calCode);

    /**
     * 查询提奖配置明细
     *
     * @param configId 配置主键
     * @return 配置明细
     */
    List<LaRateCommisionConfigItem> queryConfigItem(Long configId);

    /**
     * 查询奖项配置
     *
     * @param branchType 渠道
     * @param riskCode   险种
     * @param date       日期
     * @return 提奖配置
     */
    List<LaRateCommision> queryRateDetail(String branchType, String riskCode, String date);
}
