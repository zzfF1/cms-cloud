package com.sinosoft.common.core;

import com.sinosoft.common.domain.dto.WageCalAgentDto;
import com.sinosoft.common.utils.wage.CalParam;
import com.sinosoft.common.utils.wage.WageCalResult;

/**
 * 佣金计算实现接口
 */
public interface IWageCalculationHandler {

    /**
     * 是否计算
     *
     * @param calAgentDto 计算人员信息
     * @param param       计算参数
     * @return true 计算 false 不计算
     */
    default boolean isCalculation(WageCalAgentDto calAgentDto, CalParam param) {
        return true;
    }

    /**
     * 计算方法
     *
     * @param calAgentDto 计算人员信息
     * @param param       计算定义
     */
    WageCalResult calculation(WageCalAgentDto calAgentDto, CalParam param);
}
