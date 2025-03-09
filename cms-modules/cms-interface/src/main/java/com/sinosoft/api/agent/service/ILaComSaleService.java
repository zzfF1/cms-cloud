package com.sinosoft.api.agent.service;


import com.sinosoft.api.agent.domain.vo.SaleInfoVo;
import com.sinosoft.common.schema.broker.domain.Lasaleagent;

import java.util.List;

/**
 * 中介机构管理
 *
 * @author zhangjiaxin
 * @date 2023-07-07
 */
public interface ILaComSaleService {

    /**
     * 查询中介机构
     *
     * @param managecom 管理公司
     * @return 结果
     */
    List<SaleInfoVo> selectSaleAgent(String managecom);
}
