package com.sinosoft.api.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sinosoft.api.agent.domain.vo.AgentInfoVo;
import com.sinosoft.api.agent.mapper.GrpHealthagentMapper;
import com.sinosoft.api.agent.service.ILahealthagentService;
import com.sinosoft.common.core.utils.MapstructUtils;
import com.sinosoft.common.enums.BranchTypeEnum;
import com.sinosoft.common.schema.agent.domain.Laagent;
import com.sinosoft.common.schema.agent.mapper.LaagentMapper;
import com.sinosoft.common.service.IBaseImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 健康险人员授权信息Service业务层处理
 *
 * @author zzf
 * @date 2024-10-22
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class LahealthagentServiceImpl implements ILahealthagentService, IBaseImpl {
    private final GrpHealthagentMapper lahealthinsauthinfoMapper;
    private final LaagentMapper laagentMapper;

    @Override
    public List<AgentInfoVo> queryHealthToAgentList(String managecom) {
        LambdaQueryWrapper<Laagent> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.likeRight(Laagent::getManagecom, managecom)
            .inSql(Laagent::getAgentcode, "SELECT agentcode FROM lahealthinsauthinfo WHERE authstatus = 'Y'")
            .in(Laagent::getAgentstate, "01", "02")
            .eq(Laagent::getBranchtype, BranchTypeEnum.GRP.getCode())
            .select(Laagent::getAgentcode, Laagent::getName);
        return MapstructUtils.convert(laagentMapper.selectList(queryWrapper), AgentInfoVo.class);
    }
}
