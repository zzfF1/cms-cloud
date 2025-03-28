package com.sinosoft.api.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.sinosoft.common.forest.interceptor.YdSecurityInterceptor;

@BaseRequest(baseURL = "http://localhost:8080", charset = "UTF-8", interceptor = YdSecurityInterceptor.class,
    headers = {
        "Content-Type: application/json; charset=UTF-8",
        "safe-mode: 1",
        "auth-mode: AK-SK",
        "clientid: 428a8310cd442757ae699df5d894f001"
    })
public interface TestClient {

    @Post("/business-api/v1/grpagent/list")
    String testList(@JSONBody("username") String username, @JSONBody("clientId") String clientId);

    @Post("http://localhost:8080/auth/login")
    String testLogin(@JSONBody("tenantId") String tenantId, @JSONBody("username") String username, @JSONBody("password") String password, @JSONBody("clientId") String clientId, @JSONBody("grantType") String grantType);

    @Post("/business-api/v1/grpagent/healthagent/list")
    String testManagecom(@JSONBody("manageCom") String managecom);

    @Post("/business-api/v1/grpagent/healthagent/listLasaleagent")
    String testSaleManagecom(@JSONBody("manageCom") String managecom);
}
