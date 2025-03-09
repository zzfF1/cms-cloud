package com.sinosoft.api.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.http.ForestResponse;

/**
 * @program: cms-cloud
 * @description:
 * @author: zzf
 * @create: 2025-02-17 00:02
 */
public interface CmsServiceClient {

    @Post(url = "http://localhost:7001/cms/services/CmsService.CmsServiceHttpSoap11Endpoint/")
    @Headers({
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: urn:queryBankSaleCode"
    })
    ForestResponse queryBankSaleCode(@Body String cXml);

    @Post(url = "http://10.1.10.50:22001/lis/services/MITWebService")
    @Headers({
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"\""
    })
    ForestResponse doService(@Body String input);
}
