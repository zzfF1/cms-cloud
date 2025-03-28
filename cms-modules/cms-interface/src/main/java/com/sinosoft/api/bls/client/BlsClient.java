package com.sinosoft.api.bls.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.sinosoft.common.forest.interceptor.YdSecurityInterceptor;
import com.sinosoft.common.core.domain.GlobalResponse;
import com.sinosoft.integration.api.bls.model.BlsPaymentDto;
import com.sinosoft.integration.api.bls.model.BlsPaymentQuery;
import com.sinosoft.integration.api.bls.model.BlsPaymentQueryResponse;
import com.sinosoft.integration.api.bls.model.BlsPaymentResponse;

/**
 * 结算服务
 */
@BaseRequest(baseURL = "http://10.1.105.111:29001", charset = "UTF-8", interceptor = YdSecurityInterceptor.class,
    headers = {
        "Content-Type: application/json; charset=UTF-8",
        "safe-mode: 0",
        "auth-mode: AK-SK",
        "clientid: 43431c516abd43faa2247b38bc63bb06"
    })
public interface BlsClient {

    /**
     * 批量代付
     *
     * @param paymentDto 代付信息
     * @return 代付结果
     */
    @Post("/bls/business/batchPayables")
    GlobalResponse<BlsPaymentResponse> batchPayables(@JSONBody BlsPaymentDto paymentDto);

    /**
     * 查询代付
     *
     * @param paymentDto 查询条件
     * @return 代付结果
     */
    @Post("/bls/business/batchPayablesQury")
    GlobalResponse<BlsPaymentQueryResponse> batchPayablesQury(@JSONBody BlsPaymentQuery paymentDto);
}
