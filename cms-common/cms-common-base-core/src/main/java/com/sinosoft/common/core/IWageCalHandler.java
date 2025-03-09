package com.sinosoft.common.core;

import com.sinosoft.common.domain.dto.CalPerson;
import com.sinosoft.common.domain.dto.WageCalMainDto;
import com.sinosoft.common.utils.wage.WageRedisUtil;

/**
 * 佣金计算初始化人员接口
 */
public interface IWageCalHandler {

    /**
     * 获取计算人员
     *
     * @param wageCalDelayedDto 佣金计算参数
     * @return 计算人员
     */
    CalPerson getCalAgents(WageCalMainDto wageCalDelayedDto);
    /**
     * 计算开始前准备
     *
     * @param wageCalDelayedDto 佣金计算对象
     */
    void calPreparation(WageCalMainDto wageCalDelayedDto, WageRedisUtil wageRedisUtil);

    /**
     * 计算结束后处理
     *
     * @param wageCalDelayedDto 佣金计算对象
     */
    void calEnd(WageCalMainDto wageCalDelayedDto);
}
