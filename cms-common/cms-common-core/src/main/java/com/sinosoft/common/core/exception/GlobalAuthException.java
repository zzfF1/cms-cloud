package com.sinosoft.common.core.exception;

import com.sinosoft.common.core.enums.ErrorCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 全局认证异常
 *
 * @author: zzf
 * @create: 2025-02-16 14:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GlobalAuthException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 错误码
     */
    private Integer code;
    /**
     * 业务错误码
     */
    private String errorCode;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误明细，内部调试错误
     */
    private String detailMessage;

    /**
     * 空构造方法，避免反序列化问题
     */
    public GlobalAuthException() {
    }

    /**
     * 通过错误码和错误信息创建异常对象
     */
    public GlobalAuthException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * 通过错误码枚举创建异常对象
     */
    public GlobalAuthException(ErrorCodeEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorCode = errorEnum.getCode();
        this.message = errorEnum.getMessage();
    }

    /**
     * 通过错误码枚举和自定义消息创建异常对象
     */
    public GlobalAuthException(ErrorCodeEnum errorEnum, String message) {
        super(message);
        this.errorCode = errorEnum.getCode();
        this.message = message;
    }

    /**
     * 通过错误码枚举、自定义消息和详细信息创建异常对象
     */
    public GlobalAuthException(ErrorCodeEnum errorEnum, String message, String detailMessage) {
        super(message);
        this.errorCode = errorEnum.getCode();
        this.message = message;
        this.detailMessage = detailMessage;
    }

    /**
     * 通过HTTP状态码、错误码枚举和自定义消息创建异常对象
     */
    public GlobalAuthException(Integer code, ErrorCodeEnum errorEnum, String message) {
        super(message);
        this.code = code;
        this.errorCode = errorEnum.getCode();
        this.message = message;
    }

    /**
     * 完整参数构造方法
     */
    public GlobalAuthException(Integer code, String errorCode, String message, String detailMessage) {
        super(message);
        this.code = code;
        this.errorCode = errorCode;
        this.message = message;
        this.detailMessage = detailMessage;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"header\":{");
        // 添加code
        sb.append("\"code\":\"").append(code != null ? code : 400).append("\",");
        // 添加error_code
        if (errorCode != null && !errorCode.isEmpty()) {
            sb.append("\"error_code\":\"").append(errorCode).append("\",");
        }
        // 添加message
        if (message != null && !message.isEmpty()) {
            sb.append("\"message\":\"").append(escapeJson(message)).append("\",");
        }
        // 添加detail_message
        if (detailMessage != null && !detailMessage.isEmpty()) {
            sb.append("\"detail_message\":\"").append(escapeJson(detailMessage)).append("\",");
        }
        // 添加messageid
        sb.append("\"messageid\":\"").append(java.util.UUID.randomUUID()).append("\"");
        // 闭合header并添加空的response
        sb.append("},\"response\":{}}");
        return sb.toString();
    }

    /**
     * 转义JSON字符串中的特殊字符
     */
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}
