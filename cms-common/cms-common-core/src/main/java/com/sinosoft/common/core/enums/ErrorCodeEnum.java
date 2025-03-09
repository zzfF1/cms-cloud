package com.sinosoft.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 * 错误码组成: {场景类别(3位)}{具体错误码(3位)}
 * GWS：系统级别错误
 * GWA：权限类错误
 * GWC：验证类错误
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {
    // 系统级错误 (GWS)
    SYSTEM_SERVICE_UNAVAILABLE("GWS001", "服务不可用"),
    SYSTEM_MAINTENANCE("GWS002", "系统维护中"),
    SYSTEM_SERVICE_TIMEOUT("GWS003", "服务调用超时"),
    SYSTEM_SERVICE_CIRCUIT_BREAK("GWS004", "服务熔断请求"),
    SYSTEM_FLOW_LIMITING("GWS005", "系统限流"),
    SYSTEM_INTERNAL_ERROR("GWS006", "系统内部错误"),

    // 认证授权类错误 (GWA)
    AUTH_TOKEN_INVALID("GWA001", "访问令牌失效"),
    AUTH_TOKEN_EXPIRED("GWA002", "访问令牌过期"),
    AUTH_PERMISSION_DENIED("GWA003", "权限不足"),
    AUTH_USER_NOT_LOGIN("GWA004", "用户未登录"),
    AUTH_ACCOUNT_LOCKED("GWA005", "用户账户被锁定"),
    AUTH_IP_RESTRICTED("GWA006", "IP被限制访问"),
    AUTH_CLIENT_MISMATCH("GWA007", "客户端ID不匹配"),
    AUTH_REQUEST_EXPIRED("GWA008", "请求已过期"),
    AUTH_NONCE_INVALID("GWA009", "无效的nonce"),
    AUTH_UNSUPPORTED_MODE("GWA010", "不支持的认证模式"),
    AUTH_SIGNATURE_INVALID("GWA011", "无效的签名"),

    // 参数验证类错误 (GWC)
    PARAM_FORMAT_INVALID("GWC001", "参数格式不正确"),
    PARAM_MISSING("GWC002", "参数缺失"),
    PARAM_OUT_OF_RANGE("GWC003", "参数值超出范围"),
    PARAM_TYPE_ERROR("GWC004", "参数类型错误"),
    PARAM_EMPTY("GWC005", "参数不能为空"),
    PARAM_LENGTH_LIMIT("GWC006", "参数长度超限");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误描述
     */
    private final String message;

    /**
     * 根据错误码获取错误枚举
     */
    public static ErrorCodeEnum getByCode(String code) {
        for (ErrorCodeEnum error : values()) {
            if (error.getCode().equals(code)) {
                return error;
            }
        }
        return null;
    }
}
