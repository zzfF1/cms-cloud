package com.sinosoft.common.core.constant;

/**
 * 报文头常量
 *
 * @author: zzf
 * @create: 2025-02-06 13:51
 */
public interface HeaderConstants {
    /**
     * 内容类型
     */
    String CONTENT_TYPE = "content-type";

    /**
     * 客户端ID
     */
    String CLIENT_ID = "clientid";

    /**
     * 请求时间
     */
    String REQUEST_TIME = "request-time";

    /**
     * 响应时间
     */
    String RESPONSE_TIME = "response-time";

    /**
     * 随机数
     */
    String NONCE = "nonce";

    /**
     * 有效期
     */
    String VALID_TIME = "valid-time";

    /**
     * 安全模式
     */
    String SAFE_MODE = "safe-mode";

    /**
     * 加密密钥
     */
    String ENCRYPT_KEY = "encrypt-key";

    /**
     * 签名
     */
    String SIGNATURE = "signature";

    /**
     * 认证方式
     */
    String AUTH_MODE = "auth-mode";

    /**
     * 认证信息
     */
    String AUTHORIZATION = "authorization1";

    /**
     * 默认CONTENT_TYPE
     */
    String DEFAULT_CONTENT_TYPE = "application/json; charset=UTF-8";
}
