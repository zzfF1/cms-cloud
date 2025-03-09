package com.sinosoft.common.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 接口统一响应
 *
 * @author: zzf
 * @create: 2025-02-06 15:49
 */
@Data
@NoArgsConstructor
public class GlobalResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 成功
     */
    public static final int SUCCESS = 200;

    /**
     * 失败
     */
    public static final int FAIL = 400;

    /**
     * 状态码
     */
    private int code;
    /**
     * 错误码
     */
    private String errorCode;
    /**
     * 报文应答消息
     */
    private String message;
    /**
     * 报文应答详细消息
     */
    private String detailMessage;
    /**
     * 数据对象
     */
    private T data;

    public static <T> GlobalResponse<T> ok() {
        return restResult(SUCCESS, "", "", "", null);
    }

    public static <T> GlobalResponse<T> ok(T data) {
        return restResult(SUCCESS, "", "", "", data);
    }

    public static <T> GlobalResponse<T> ok(String message, T data) {
        return restResult(SUCCESS, "", message, "", data);
    }

    public static <T> GlobalResponse<T> fail(String errorCode, String message, String detailMessage) {
        return restResult(FAIL, errorCode, message, detailMessage, null);
    }

    public static <T> GlobalResponse<T> fail(String errorCode, String message, String detailMessage, T data) {
        return restResult(FAIL, errorCode, message, detailMessage, data);
    }

    /**
     * 设置响应结果
     *
     * @param code          状态码
     * @param errorCode     错误码
     * @param message       消息
     * @param detailMessage 详细消息
     * @param data          数据
     * @return 响应结果
     */
    private static <T> GlobalResponse<T> restResult(int code, String errorCode, String message, String detailMessage, T data) {
        GlobalResponse<T> response = new GlobalResponse<>();
        response.setCode(code);
        response.setErrorCode(errorCode);
        response.setMessage(message);
        response.setDetailMessage(detailMessage);
        response.setData(data);
        return response;
    }
}
