package com.sinosoft.common.forest.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sinosoft.common.core.domain.GlobalResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * API响应适配类
 * 用于匹配API返回的JSON结构
 *
 * @author: zzf
 * @create: 2025-03-01
 */
@Data
@NoArgsConstructor
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应头
     */
    private Header header;

    /**
     * 响应体
     */
    private T response;

    /**
     * 转换为GlobalResponse
     *
     * @return GlobalResponse对象
     */
    public GlobalResponse<T> toGlobalResponse() {
        GlobalResponse<T> globalResponse = new GlobalResponse<>();

        if (header != null) {
            // 设置状态码
            globalResponse.setCode(header.getCodeAsInt());
            // 设置错误码
            globalResponse.setErrorCode(header.getErrorCode());
            // 设置消息
            globalResponse.setMessage(header.getMessage());
            // 设置详细消息
            globalResponse.setDetailMessage(header.getDetailMessage());
        }

        // 设置数据
        globalResponse.setData(response);

        return globalResponse;
    }

    /**
     * 创建成功响应
     *
     * @param data 数据
     * @param <R>  数据类型
     * @return API响应对象
     */
    public static <R> ApiResponse<R> ok(R data) {
        ApiResponse<R> apiResponse = new ApiResponse<>();
        Header header = new Header();
        header.setCode("200");
        apiResponse.setHeader(header);
        apiResponse.setResponse(data);
        return apiResponse;
    }

    /**
     * 创建失败响应
     *
     * @param errorCode     错误码
     * @param message       消息
     * @param detailMessage 详细消息
     * @param <R>           数据类型
     * @return API响应对象
     */
    public static <R> ApiResponse<R> fail(String errorCode, String message, String detailMessage) {
        ApiResponse<R> apiResponse = new ApiResponse<>();
        Header header = new Header();
        header.setCode("400");
        header.setErrorCode(errorCode);
        header.setMessage(message);
        header.setDetailMessage(detailMessage);
        apiResponse.setHeader(header);
        return apiResponse;
    }

    /**
     * 响应头
     */
    @Data
    @NoArgsConstructor
    public static class Header implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 状态码
         */
        private String code;

        /**
         * 错误码
         */
        @JsonProperty("error_code")
        private String errorCode;

        /**
         * 消息
         */
        private String message;

        /**
         * 详细消息
         */
        @JsonProperty("detail_message")
        private String detailMessage;

        /**
         * 消息ID
         */
        private String messageid;

        /**
         * 获取状态码为整数
         *
         * @return 整数状态码
         */
        public int getCodeAsInt() {
            try {
                return Integer.parseInt(code);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }
}
