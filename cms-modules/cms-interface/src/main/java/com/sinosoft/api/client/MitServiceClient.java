package com.sinosoft.api.client;

import com.dtflys.forest.annotation.*;
import com.sinosoft.common.forest.client.ClientAddressSource;
import com.sinosoft.common.forest.interceptor.MitServiceInterceptor;

/**
 * Mit客户端
 *
 * @author: zzf
 * @create: 2025-03-06 11:08
 */
@Address(source = ClientAddressSource.class)
@BaseRequest(interceptor = MitServiceInterceptor.class)
public interface MitServiceClient {

    @Post(url = "/lis/services/MITWebService")
    @Headers({
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"\"",
        "clientid: 428a8310cd442757ae699df5d894f002"
    })
    String doService(@Body String input);
}
