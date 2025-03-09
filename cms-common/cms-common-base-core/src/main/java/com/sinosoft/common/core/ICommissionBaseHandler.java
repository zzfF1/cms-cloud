package com.sinosoft.common.core;

import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.domain.dto.CalPerson;
import com.sinosoft.common.domain.dto.CommissionCalMainDto;
import com.sinosoft.common.utils.wage.WageRedisUtil;

/**
 * 佣金计算初始化接口
 */
public interface ICommissionBaseHandler {

    /**
     * 检查与初始化佣金计算状态
     *
     * @param wageCalDelayedDto 佣金计算参数
     * @throws ServiceException 业务异常
     */
    void checkAndInitCommisionState(CommissionCalMainDto wageCalDelayedDto) throws ServiceException;

    /**
     * 获取计算人员
     *
     * @param wageCalDelayedDto 佣金计算参数
     * @return 计算人员
     * @throws ServiceException 业务异常
     */
    CalPerson getCalAgents(CommissionCalMainDto wageCalDelayedDto) throws ServiceException;

    /**
     * 计算开始前准备
     *
     * @param wageCalDelayedDto 佣金计算对象
     * @throws ServiceException 业务异常
     */
    void calPreparation(CommissionCalMainDto wageCalDelayedDto, WageRedisUtil wageRedisUtil) throws ServiceException;

    /**
     * 计算结束后处理
     *
     * @param wageCalDelayedDto 佣金计算对象
     * @throws ServiceException 业务异常
     */
    void calEnd(CommissionCalMainDto wageCalDelayedDto) throws ServiceException;

    /**
     * 删除计算数据
     *
     * @param wageCalDelayedDto 佣金计算对象
     * @throws ServiceException 业务异常
     */
    void delCalData(CommissionCalMainDto wageCalDelayedDto) throws ServiceException;
}
