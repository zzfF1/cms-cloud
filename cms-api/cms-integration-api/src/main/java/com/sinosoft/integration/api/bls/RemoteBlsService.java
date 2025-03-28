package com.sinosoft.integration.api.bls;

import com.sinosoft.integration.api.bls.model.BlsPaymentDto;
import com.sinosoft.integration.api.bls.model.BlsPaymentQuery;
import com.sinosoft.integration.api.bls.model.BlsPaymentQueryResponse;
import com.sinosoft.integration.api.bls.model.BlsPaymentResponse;

/**
 * 结算服务接口
 */
public interface RemoteBlsService {

    /**
     * 支付
     *
     * @param payment 支付信息
     * @return 支付结果
     */
    BlsPaymentResponse Payment(BlsPaymentDto payment);


    /**
     * 查询支付
     *
     * @param BlsPaymentQuery 查询条件
     * @return 支付结果
     */
    BlsPaymentQueryResponse QueryPayment(BlsPaymentQuery BlsPaymentQuery);
}
