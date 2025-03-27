package com.sinosoft.api.agent.mapper;

import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.common.schema.agent.domain.Lahealthinsauthinfo;

import java.util.List;

/**
 * 人员基本信息Mapper接口
 *
 * @author zzf
 * @date 2023-11-15
 */
public interface GrpHealthagentMapper extends BaseMapperPlus<Lahealthinsauthinfo, Lahealthinsauthinfo> {

    List<String> selectAgentHasHealthAuth();
}
