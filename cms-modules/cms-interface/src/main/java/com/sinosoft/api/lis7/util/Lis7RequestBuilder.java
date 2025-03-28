package com.sinosoft.api.lis7.util;

import cn.hutool.core.util.IdUtil;
import com.sinosoft.common.core.utils.DateUtils;
import com.sinosoft.integration.api.core.ClientInfo;
import com.sinosoft.integration.api.core.Lis7HttpRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * LIS7请求构建工具类
 */
@Slf4j
public class Lis7RequestBuilder {

    /**
     * 构建LIS7请求对象
     *
     * @param inputData    业务数据
     * @param businessCode 业务代码
     * @param <T>          业务数据类型
     * @return 封装好的请求对象
     */
    public static <T> Lis7HttpRequest<T> buildRequest(T inputData, String businessCode) {
        log.debug("构建LIS7请求对象, 业务代码: {}", businessCode);

        // 创建客户端信息
        ClientInfo clientInfo = ClientInfo.builder()
            .dealType("salem")
            .date(DateUtils.getDate())
            .time(DateUtils.getTimeWithHourMinuteSecond())
            .seqNo(IdUtil.fastSimpleUUID())
            .businessCode(businessCode)
            .subBusinessCode("1")
            .build();

        // 创建并返回请求对象
        Lis7HttpRequest<T> request = new Lis7HttpRequest<>();
        request.setClientInfo(clientInfo);
        request.setInputData(inputData);

        log.debug("LIS7请求对象构建完成");
        return request;
    }
}
