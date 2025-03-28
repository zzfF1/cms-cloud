package com.sinosoft.api.bls.service.impl;

import com.sinosoft.api.bls.client.BlsClient;
import com.sinosoft.common.core.domain.GlobalResponse;
import com.sinosoft.integration.api.bls.RemoteBlsService;
import com.sinosoft.integration.api.bls.model.BlsPaymentDto;
import com.sinosoft.integration.api.bls.model.BlsPaymentQuery;
import com.sinosoft.integration.api.bls.model.BlsPaymentQueryResponse;
import com.sinosoft.integration.api.bls.model.BlsPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
 * 结算服务实现
 * @author: zzf
 * @create: 2025-03-13 18:41
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteBlsServiceImpl implements RemoteBlsService {

    private final BlsClient blsClient;

    @Override
    public BlsPaymentResponse Payment(BlsPaymentDto payment) {
        GlobalResponse<BlsPaymentResponse> response = blsClient.batchPayables(payment);
        return response.getData();
    }

    @Override
    public BlsPaymentQueryResponse QueryPayment(BlsPaymentQuery BlsPaymentQuery) {
        GlobalResponse<BlsPaymentQueryResponse> response = blsClient.batchPayablesQury(BlsPaymentQuery);
        return response.getData();
    }
}
