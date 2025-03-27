package com.sinosoft.api.lis7.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.sinosoft.common.core.domain.GlobalResponse;
import com.sinosoft.common.forest.interceptor.YdSecurityInterceptor;
import com.sinosoft.integration.api.bls.model.BlsPaymentDto;
import com.sinosoft.integration.api.bls.model.BlsPaymentQuery;
import com.sinosoft.integration.api.bls.model.BlsPaymentQueryResponse;
import com.sinosoft.integration.api.bls.model.BlsPaymentResponse;
import com.sinosoft.integration.api.core.Lis7HttpRequest;
import com.sinosoft.integration.api.core.Lis7HttpResponse;
import com.sinosoft.integration.api.lis7.model.OrphanPolicyAssignmentDto;
import com.sinosoft.integration.api.lis7.model.OrphanPolicyAssignmentResponseDto;
import com.sinosoft.integration.api.lis7.model.SyncBaseAgentDto;
import com.sinosoft.integration.api.lis7.model.SyncBaseTreeDto;
import org.apache.poi.ss.formula.functions.T;

/**
 * lis7系统
 */
@BaseRequest(baseURL = "http://127.0.0.1:18081", charset = "UTF-8", interceptor = YdSecurityInterceptor.class,
    headers = {
        "Content-Type: application/json; charset=UTF-8",
        "safe-mode: 0",
        "auth-mode: AK-SK",
        "clientid: 428a8310cd442757ae699df5d894f001"
    })
public interface Lis7Client {

    /**
     * 孤儿单确认
     *
     * @param request 孤儿单确认对象
     * @return 确认结果
     */
    @Post("/business-api/v1/grpagent/healthagent/assignOrphanPolicy")
    GlobalResponse<OrphanPolicyAssignmentResponseDto> assignOrphanPolicy(@JSONBody Lis7HttpRequest<OrphanPolicyAssignmentDto> request);

    /**
     * 人员基础信息同步
     *
     * @param request 人员基础信息
     * @return 确认结果
     */
    @Post("/business-api/v1/grpagent/healthagent/assignOrphanPolicy")
    GlobalResponse<Lis7HttpResponse> syncAgent(@JSONBody Lis7HttpRequest<SyncBaseAgentDto> request);

    /**
     * 人员职级信息同步
     *
     * @param request 人员职级信息
     * @return 确认结果
     */
    @Post("/business-api/v1/grpagent/healthagent/assignOrphanPolicy")
    GlobalResponse<Lis7HttpResponse> syncTree(@JSONBody Lis7HttpRequest<SyncBaseTreeDto> request);
}
