package com.sinosoft.common.flow;

import com.sinosoft.system.api.model.LoginUser;
import com.sinosoft.common.domain.LcMain;
import com.sinosoft.common.exception.FlowException;

import java.util.List;
import java.util.Map;

/**
 * 流程检查接口
 */
public interface ILcCheckHandler {
    /**
     * 流程检查处理类
     *
     * @param nLcId     流程ID
     * @param lcMain    流程主表
     * @param ids       业务数据
     * @param loginUser 操作人
     * @param lcParms   流程参数
     * @throws FlowException 流程处理异常
     */
    void checkHandler(int nLcId, LcMain lcMain, List<String> ids, LoginUser loginUser, Map<String, Object> lcParms) throws FlowException;
}
