package com.sinosoft.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sinosoft.common.schema.commission.domain.LaRateCommision;
import com.sinosoft.common.schema.commission.mapper.LaRateCommisionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.service.IRateConfigBaseService;
import com.sinosoft.common.schema.commission.domain.LaRateCommisionConfig;
import com.sinosoft.common.schema.commission.domain.LaRateCommisionConfigItem;
import com.sinosoft.common.schema.commission.mapper.LaRateCommisionConfigItemMapper;
import com.sinosoft.common.schema.commission.mapper.LaRateCommisionConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: cms6
 * @description: 提奖配置服务
 * @author: zzf
 * @create: 2024-02-02 22:55
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class RateConfigBaseServiceImpl implements IRateConfigBaseService {
    private final LaRateCommisionConfigMapper laRateCommisionConfigMapper;
    private final LaRateCommisionConfigItemMapper laRateCommisionConfigItemMapper;
    private final LaRateCommisionMapper laRateCommisionMapper;

    /**
     * 根据代码查询提奖配置
     *
     * @param calCode 代码
     * @return 提奖配置对象
     */
    @Override
    public LaRateCommisionConfig queryConfigByCode(String calCode) {
        LambdaQueryWrapper<LaRateCommisionConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(LaRateCommisionConfig::getCode, calCode);
        return laRateCommisionConfigMapper.selectOne(lqw);
    }

    /**
     * 查询提奖配置明细
     *
     * @param configId 配置主键
     * @return 配置明细
     */
    @Override
    public List<LaRateCommisionConfigItem> queryConfigItem(Long configId) {
        LambdaQueryWrapper<LaRateCommisionConfigItem> lqw = Wrappers.lambdaQuery();
        lqw.eq(LaRateCommisionConfigItem::getConfigId, configId);
        lqw.orderByAsc(LaRateCommisionConfigItem::getCalSort);
        return laRateCommisionConfigItemMapper.selectList(lqw);
    }

    /**
     * 查询奖项配置
     *
     * @param branchType 渠道
     * @param riskCode   险种
     * @param date       日期
     * @return 提奖配置
     */
    @Override
    public List<LaRateCommision> queryRateDetail(String branchType, String riskCode, String date) {
        LambdaQueryWrapper<LaRateCommision> lqw = Wrappers.lambdaQuery();
        lqw.eq(LaRateCommision::getBranchtype, branchType);
        lqw.eq(LaRateCommision::getRiskcode, riskCode);
//        lqw.and(wrapper -> wrapper.ge(Laratecommision::getStartDate, date).lt(Laratecommision::getEndDate, date));
        // TODO 存在不存在字段 lqw.orderByDesc(Laratecommision::getWeight);
        lqw.orderByDesc(LaRateCommision::getManagecom);
        return laRateCommisionMapper.selectList(lqw);
    }

}
