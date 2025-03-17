package com.sinosoft.common.mq.core;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息处理结果
 */
@Setter
@Getter
public class MqResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // Getters and Setters
    // 是否成功
    private boolean success;

    // 结果数据
    private T data;

    // 错误码
    private String code;

    // 错误消息
    private String message;

    private MqResult(boolean success, T data, String code, String message) {
        this.success = success;
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public static <T> MqResult<T> success() {
        return new MqResult<>(true, null, "200", "success");
    }

    public static <T> MqResult<T> success(T data) {
        return new MqResult<>(true, data, "200", "success");
    }

    public static <T> MqResult<T> failure(String code, String message) {
        return new MqResult<>(false, null, code, message);
    }

    @Override
    public String toString() {
        return "MqResult{" +
            "success=" + success +
            ", data=" + data +
            ", code='" + code + '\'' +
            ", message='" + message + '\'' +
            '}';
    }
}
