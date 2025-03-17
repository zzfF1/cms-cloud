package com.sinosoft.api.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.sinosoft.api.agent.domain.bo.BartchPaymentAgentBo;
import com.sinosoft.api.agent.domain.bo.CheckOrphanSingleBo;
import com.sinosoft.api.agent.domain.vo.BartchPaymentAgentVo;
import com.sinosoft.api.agent.domain.vo.SaleInfoVo;
import com.sinosoft.api.interceptor.YdSecurityInterceptor;
import com.sinosoft.common.core.domain.GlobalResponse;

import java.util.List;

/**
 * 结算服务
 */
@BaseRequest(baseURL = "http://10.1.105.111:29001", charset = "UTF-8", interceptor = YdSecurityInterceptor.class,
    headers = {
        "Content-Type: application/json; charset=UTF-8",
        "safe-mode: 0",
        "auth-mode: AK-SK",
        "clientid: 77cc8201024d964929d3083ae6fae1e6"
    })
public interface SettlementServiceClient {
    @Post("bls/business/batchPayables")
    GlobalResponse<BartchPaymentAgentVo> BartchPaymentAgent(@JSONBody BartchPaymentAgentBo bo);
}
