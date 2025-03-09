package com.sinosoft.common.forest.domain;

import lombok.Data;

/**
 * 基础响应类
 *
 * @param <T> 具体的业务响应数据类型
 */
@Data
public class BaseResponse<T> {
    private Header header;
    private T response;

    @Data
    public static class Header {
        private String code;
        private String error_code;
        private String message;
        private String detail_message;
        private String messageid;
    }
}
