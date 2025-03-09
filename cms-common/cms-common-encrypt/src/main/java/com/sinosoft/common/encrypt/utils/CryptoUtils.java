package com.sinosoft.common.encrypt.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.*;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.SM4;
import com.sinosoft.common.core.constant.HttpStatus;
import com.sinosoft.common.core.enums.ErrorCodeEnum;
import com.sinosoft.common.core.exception.GlobalAuthException;
import com.sinosoft.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.Security;

/**
 * 加密工具类，提供 SM2/SM4 加解密功能和签名计算
 * <p>
 * 主要功能：
 * - SM2 公钥加密/私钥解密
 * - SM4 对称加解密
 * - SM3 哈希计算
 * - 数字签名生成（MD5+SM3）
 * <p>
 * 注意事项：
 * 1. SM4 密钥使用十六进制字符串格式
 * 2. SM2 公钥/私钥使用 Base64 编码格式
 * 3. 加密数据统一使用十六进制字符串格式
 */
@Slf4j
public class CryptoUtils {

    /**
     * 使用 SM2 私钥解密 SM4 密钥
     *
     * @param encryptedKey  十六进制格式的加密密钥
     * @param sm2PrivateKey Base64 编码的 SM2 私钥
     * @return 解密后的 SM4 密钥（十六进制格式）
     */
    public static String decryptSm4Key(String encryptedKey, String sm2PrivateKey) {
        // 参数校验
        if (encryptedKey == null || encryptedKey.isEmpty()) {
            throw new GlobalAuthException(ErrorCodeEnum.PARAM_EMPTY, "加密密钥不能为空", "解密SM4密钥需要提供加密后的密钥数据");
        }
        if (sm2PrivateKey == null || sm2PrivateKey.isEmpty()) {
            throw new GlobalAuthException(ErrorCodeEnum.PARAM_EMPTY, "SM2私钥不能为空", "解密SM4密钥需要提供SM2私钥");
        }

        try {
            return SmUtil.sm2(sm2PrivateKey, null).decryptStr(encryptedKey, KeyType.PrivateKey);
        } catch (Exception e) {
            log.error("SM4密钥解密失败: {}", e.getMessage());
            throw new GlobalAuthException(HttpStatus.ERROR, ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode(), "SM4密钥解密失败", e.getMessage());
        }
    }

    /**
     * 使用 SM2 公钥加密 SM4 密钥
     *
     * @param sm4Key       SM4 密钥（十六进制格式）
     * @param sm2PublicKey Base64 编码的 SM2 公钥
     * @return 十六进制格式的加密 SM4 密钥
     */
    public static String encryptSm4Key(String sm4Key, String sm2PublicKey) {
        // 参数校验
        if (sm4Key == null || sm4Key.isEmpty()) {
            throw new GlobalAuthException(ErrorCodeEnum.PARAM_EMPTY, "SM4密钥不能为空", "加密SM4密钥需要提供原始密钥数据");
        }
        if (sm2PublicKey == null || sm2PublicKey.isEmpty()) {
            throw new GlobalAuthException(ErrorCodeEnum.PARAM_EMPTY, "SM2公钥不能为空", "加密SM4密钥需要提供SM2公钥");
        }
        try {
            // 使用SM2加密密钥并返回十六进制字符串
            return SmUtil.sm2(null, sm2PublicKey).encryptHex(sm4Key, KeyType.PublicKey);
        } catch (Exception e) {
            log.error("SM4密钥加密失败: {}", e.getMessage());
            throw new GlobalAuthException(HttpStatus.ERROR, ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode(), "SM4密钥加密失败", e.getMessage());
        }
    }

    /**
     * 验证SM4密钥有效性
     */
    private static void validateSm4Key(String key) {
        if (StringUtils.isBlank(key)) {
            throw new GlobalAuthException(ErrorCodeEnum.PARAM_EMPTY, "SM4密钥不能为空", "加解密操作需要提供有效的SM4密钥");
        }
        if (key.length() != 32) {
            throw new GlobalAuthException(ErrorCodeEnum.PARAM_FORMAT_INVALID, "SM4密钥格式错误", "SM4密钥长度必须为32个十六进制字符");
        }
        try {
            HexUtil.decodeHex(key);
        } catch (Exception e) {
            throw new GlobalAuthException(ErrorCodeEnum.PARAM_FORMAT_INVALID, "SM4密钥格式错误", "SM4密钥必须是有效的十六进制字符串");
        }
    }

    /**
     * 使用 SM4 密钥解密请求体内容
     *
     * @param encryptedBody 十六进制格式的加密报文
     * @param sm4Key        SM4 密钥（十六进制格式）
     * @return 解密后的明文
     */
    public static String decryptBody(String encryptedBody, String sm4Key) {
        validateSm4Key(sm4Key);
        if (StringUtils.isBlank(encryptedBody)) {
            throw new GlobalAuthException(ErrorCodeEnum.PARAM_EMPTY, "加密数据不能为空", "解密操作需要提供有效的加密数据");
        }
        try {
            byte[] keyBytes = HexUtil.decodeHex(sm4Key);
            SM4 sm4 = new SM4(Mode.CBC, Padding.PKCS5Padding, keyBytes, keyBytes);
            return sm4.decryptStr(encryptedBody, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("SM4解密失败: {}", e.getMessage());
            throw new GlobalAuthException(HttpStatus.ERROR, ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode(), "解密处理失败", e.getMessage());
        }
    }

    /**
     * 使用 SM4 密钥加密报文内容
     *
     * @param encryptedData 原始报文
     * @param sm4Key        SM4 密钥（十六进制格式）
     * @return 十六进制格式的加密报文
     */
    public static String encryptBody(String encryptedData, String sm4Key) {
        validateSm4Key(sm4Key);
        try {
            byte[] keyBytes = HexUtil.decodeHex(sm4Key);
            SM4 sm4 = new SM4(Mode.CBC, Padding.PKCS5Padding, keyBytes, keyBytes);
            return sm4.encryptHex(encryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("SM4加密失败: {}", e.getMessage());
            throw new GlobalAuthException(HttpStatus.ERROR, ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode(), "加密处理失败", e.getMessage());
        }
    }

    /**
     * 计算SM3哈希值
     *
     * @param content 待计算内容
     * @return SM3哈希值（十六进制格式）
     */
    public static String calculateSm3(String content) {
        return SmUtil.sm3(content);
    }

    /**
     * 计算签名（先MD5再SM3）
     *
     * @param content 待签名内容
     * @return 签名值（十六进制格式）
     */
    public static String calculateSignature(String content) {
        String md5Hex = DigestUtil.md5Hex(content);
        return SmUtil.sm3(md5Hex);
    }

    /**
     * 生成SM2密钥对
     * 用于生成新的密钥对，输出Base64编码格式的公私钥
     */
    public static void generateSm2KeyPair() {
        var sm2 = SmUtil.sm2();
        System.out.println("Private Key (Base64): " + sm2.getPrivateKeyBase64());
        System.out.println("Public Key (Base64): " + sm2.getPublicKeyBase64());
    }

    public static void main(String[] args) {
        // 生成SM2密钥对
        SM2 sm2 = SmUtil.sm2();

        // 获取Base64编码的密钥对
        String privateKeyBase64 = sm2.getPrivateKeyBase64();
        String publicKeyBase64 = sm2.getPublicKeyBase64();
        System.out.println("Base64公钥: " + publicKeyBase64);
        System.out.println("Base64私钥: " + privateKeyBase64);

        // 转换为BouncyCastle特定实现类
        BCECPrivateKey bcecPrivateKey = (BCECPrivateKey) sm2.getPrivateKey();
        BCECPublicKey bcecPublicKey = (BCECPublicKey) sm2.getPublicKey();

        // 获取私钥D值
        byte[] privateKeyBytes = bcecPrivateKey.getD().toByteArray();
        String privateKeyHex = HexUtil.encodeHexStr(privateKeyBytes);
        // 注意：可能需要去除前导零，确保是64个字符
        if (privateKeyHex.length() > 64) {
            privateKeyHex = privateKeyHex.substring(privateKeyHex.length() - 64);
        } else if (privateKeyHex.length() < 64) {
            // 如果不足64位，需要前补0
            privateKeyHex = String.format("%064x", new java.math.BigInteger(privateKeyHex, 16));
        }
        System.out.println("原始私钥(Hex): " + privateKeyHex);

        // 获取公钥点坐标（未压缩格式）
        byte[] publicKeyBytes = bcecPublicKey.getQ().getEncoded(false);
        String publicKeyHex = HexUtil.encodeHexStr(publicKeyBytes);
        System.out.println("原始公钥(Hex): " + publicKeyHex);
    }

    /**
     * 打印分隔线
     */
    private static void printLine() {
        System.out.println("========================================");
    }

}
