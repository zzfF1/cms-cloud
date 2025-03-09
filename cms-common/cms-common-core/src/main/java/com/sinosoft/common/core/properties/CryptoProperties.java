package com.sinosoft.common.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 加密配置
 * @author: zzf
 * @create: 2025-02-06 14:07
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway.security.crypto")
public class CryptoProperties {
    /**
     * 是否启用加密
     */
    private boolean enabled = true;

    /**
     * SM2公钥
     */
    private String sm2PublicKey;

    /**
     * SM2私钥
     */
    private String sm2PrivateKey;

    /**
     * SM4密钥
     */
    private String sm4Key;
}
