package com.sinosoft.system.api.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 客户端配置参数对象（存储 AK/SK、密钥等）
 * @author: zzf
 * @create: 2025-02-11 15:05
 */
@Data
public class ClientConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访问密钥（公钥）
     */
    private String ak;

    /**
     * 安全密钥（私钥，需要加密存储）
     */
    private String sk;

    /**
     * 加密方式（如 SM2, SM4）
     */
    private String encryptionMode;

    /**
     * 客户端密钥（用于加密通信）
     */
    private String sm2PublicKey;

    /**
     *  客户端私钥（用于加密通信）
     */
    private String sm2PrivateKey;
    /**
     * 服务地址
     */
    private String serverPath;
    /**
     * 端口
     */
    private Integer port;

}
