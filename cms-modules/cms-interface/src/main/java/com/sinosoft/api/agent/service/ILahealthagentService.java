package com.sinosoft.api.agent.service;


import com.sinosoft.api.agent.domain.vo.AgentInfoVo;
import com.sinosoft.common.schema.agent.domain.Laagent;

import java.util.List;

/**
 * 健康险人员授权信息Service接口
 *
 * @author zzf
 * @date 2024-10-22
 */
public interface ILahealthagentService {

    /**
     * @param managecom 管理机构
     * @return
     * @author pyz
     * @date 2025/1/17 14:07
     */
    List<AgentInfoVo> queryHealthToAgentList(String managecom);
}
