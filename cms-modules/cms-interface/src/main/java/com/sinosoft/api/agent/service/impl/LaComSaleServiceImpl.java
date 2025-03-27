package com.sinosoft.api.agent.service.impl;

import com.sinosoft.api.agent.domain.vo.SaleInfoVo;
import com.sinosoft.api.agent.mapper.GrpLasaleagentMapper;
import com.sinosoft.api.agent.service.ILaComSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 中介机构接口
 *
 * @author zzf
 * @date 2024-01-09
 */
@RequiredArgsConstructor
@Service
public class LaComSaleServiceImpl implements ILaComSaleService {

    private final GrpLasaleagentMapper laSaleAgentInputMapper;

    @Override
    public List<SaleInfoVo> selectSaleAgent(String manageCom) {
        return laSaleAgentInputMapper.selectSaleAgentInfo(manageCom);
    }
}
