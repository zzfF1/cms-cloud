package com.sinosoft.common.forest.domain;

import lombok.Data;

import java.util.UUID;

/**
 * 基础请求类
 *
 * @param <T> 具体的业务请求参数类型
 */
@Data
public class BaseRequest<T> {
    private Header header;
    private T request;

    public BaseRequest() {
        this.header = new Header();
    }

    public BaseRequest(T request) {
        this.header = new Header();
        this.request = request;
    }

    @Data
    public static class Header {
        private String version = "1.00";
        private String messageid = UUID.randomUUID().toString();
    }
}

