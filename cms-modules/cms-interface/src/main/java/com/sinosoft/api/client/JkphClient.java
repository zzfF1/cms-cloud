package com.sinosoft.api.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.sinosoft.api.agent.domain.bo.CheckContractChangeBo;
import com.sinosoft.api.agent.domain.vo.SaleInfoVo;
import com.sinosoft.api.interceptor.YdSecurityInterceptor;
import com.sinosoft.common.core.domain.GlobalResponse;

import java.util.List;

/**
 * 健康普华客户端查询
 */
@BaseRequest(baseURL = "http://localhost:8080", charset = "UTF-8", interceptor = YdSecurityInterceptor.class,
    headers = {
        "Content-Type: application/json; charset=UTF-8",
        "safe-mode: 0",
        "auth-mode: AK-SK",
        "clientid: 43431c516abd43faa2247b38bc63bb06"
    })
public interface JkphClient {

    @Post("/business-api/v1/grpagent/healthagent/checkContractChange")
    GlobalResponse<List<SaleInfoVo>> checkContractChange(@JSONBody List<CheckContractChangeBo> queryBo);

}
