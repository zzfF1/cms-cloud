package com.sinosoft.common.service;

import com.sinosoft.common.assess.AdjustContext;
import com.sinosoft.common.assess.GraderConfirmAgent;

import java.util.List;

/**
 * 销售机构树公共接口
 */
public interface IBranchTreeCommonService {

    /**
     * 初始化机构树-异动
     *
     * @param agentList 人员
     * @param context   上下文
     */
    void initBranchTree(List<GraderConfirmAgent> agentList, AdjustContext context);

    /**
     * 初始化树最大编号
     *
     * @param context 上下文
     */
    void initTreeMaxBh(AdjustContext context);
}
