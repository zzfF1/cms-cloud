package com.sinosoft.system.domain.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 密钥对视图对象
 */
@Data
public class KeyPairVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 私钥（脱敏后）
     */
    private String privateKey;

    /**
     * 原始私钥（内部使用，不返回给前端）
     */
    private String rawPrivateKey;
}
