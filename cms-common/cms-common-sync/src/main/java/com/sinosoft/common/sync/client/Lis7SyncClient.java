package com.sinosoft.common.sync.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.sinosoft.common.core.domain.GlobalResponse;
import com.sinosoft.common.forest.interceptor.YdSecurityInterceptor;
import com.sinosoft.integration.api.core.Lis7HttpRequest;

/**
 * lis7同步模块客户端
 * <p>
 * 所有业务共享同一个URL，通过不同的businessCode和DTO类型区分业务
 */
@BaseRequest(baseURL = "http://127.0.0.1:18081", charset = "UTF-8", interceptor = YdSecurityInterceptor.class,
    headers = {
        "Content-Type: application/json; charset=UTF-8",
        "safe-mode: 0",
        "auth-mode: AK-SK",
        "clientid: 428a8310cd442757ae699df5d894f001"
    })
public interface Lis7SyncClient {
    /**
     * 通用接口调用方法
     * 使用泛型处理不同的请求和响应类型
     *
     * @param request 请求对象，包含任意类型的业务数据
     * @param <T>     请求数据类型
     * @param <R>     响应数据类型
     * @return 统一响应对象
     */
    @Post("/business-api/v1/grpagent/healthagent/assignOrphanPolicy")
    <T, R> GlobalResponse<R> sendRequest(@JSONBody Lis7HttpRequest<T> request);
}
