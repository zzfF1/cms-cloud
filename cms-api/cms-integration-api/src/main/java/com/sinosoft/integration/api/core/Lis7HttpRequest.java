package com.sinosoft.integration.api.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HTTP请求包装类 - 使用泛型支持不同的业务数据结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lis7HttpRequest<T> {
    /**
     * 客户端信息
     */
    private ClientInfo clientInfo;

    /**
     * 业务数据 - 使用泛型
     */
    @JsonProperty("inputData")
    private T inputData;
}
