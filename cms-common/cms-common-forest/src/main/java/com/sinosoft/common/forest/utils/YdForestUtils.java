package com.sinosoft.common.forest.utils;


import cn.hutool.json.JSONUtil;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.utils.ForestDataType;
import com.sinosoft.common.core.constant.HeaderConstants;
import com.sinosoft.common.core.enums.FormatsType;
import com.sinosoft.common.core.utils.DateUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.encrypt.utils.CryptoUtils;
import com.sinosoft.common.forest.exception.ForestException;
import com.sinosoft.system.api.domain.vo.RemoteClientVo;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Forest第三方请求工具类
 *
 * @author zzf
 */
@Slf4j
public class YdForestUtils {

    /**
     * 设置基础请求头
     *
     * @param request 请求对象
     */
    public static void addBasicHeaders(ForestRequest request) {
        String requestTime = DateUtils.dateTimeNow(FormatsType.YYYY_MM_DD_T_HH_MM_SSS);
        request.addHeader(HeaderConstants.REQUEST_TIME, requestTime);
        String nonce = UUID.randomUUID().toString();
        request.addHeader(HeaderConstants.NONCE, nonce);
    }

    /**
     * 重新包装请求体
     *
     * @param request 请求对象
     */
    public static void setRequestBody(ForestRequest request) {
        // 获取原始请求体
        String originalBody = request.getBody() != null ? request.body().encodeToString(ForestDataType.JSON) : "{}";
        Map<String, Object> wrappedBody = new HashMap<>();
        // 构建header
        Map<String, Object> header = new HashMap<>();
        String version = StringUtils.defaultString(request.getHeaderValue("version"), "1.00");
        header.put("version", version);
        header.put("messageid", UUID.randomUUID().toString());
        // 构建完整请求体
        wrappedBody.put("header", header);
        wrappedBody.put("request", JSONUtil.parse(originalBody));
        // 替换原始请求体
        request.replaceBody(JSONUtil.toJsonStr(wrappedBody));
        // 移除原始请求头中的version
        request.getHeaders().remove("version");
    }

    /**
     * 处理加密逻辑
     *
     * @param request  请求对象
     * @param clientVo 客户端信息
     */
    public static void handleEncryption(ForestRequest request, RemoteClientVo clientVo) {
        //校验参数
        if (request == null || clientVo == null) {
            return;
        }
        String safeMode = request.getHeaderValue(HeaderConstants.SAFE_MODE);
        if (!"1".equals(safeMode)) {
            return;
        }
        try {
            // 1. 生成随机SM4密钥
            byte[] sm4Key = new byte[16];
            new SecureRandom().nextBytes(sm4Key);
            String sm4KeyHex = cn.hutool.core.util.HexUtil.encodeHexStr(sm4Key);
            // 2. SM2加密SM4密钥
            String encryptedKey = CryptoUtils.encryptSm4Key(
                sm4KeyHex,
                clientVo.getClientConfig().getSm2PublicKey()
            );
            request.addHeader(HeaderConstants.ENCRYPT_KEY, encryptedKey);
            // 3. SM4加密请求体
            String body = request.getBody() != null ? request.body().encodeToString(ForestDataType.JSON) : "";
            String encryptedBody = CryptoUtils.encryptBody(body, sm4KeyHex);
            request.replaceBody(encryptedBody);
        } catch (Exception e) {
            log.error("加密处理失败", e);
            throw new ForestException("GWS006", "加密处理失败:" + e.getMessage());
        }
    }

    /**
     * 生成签名
     *
     * @param request  请求对象
     * @param clientVo 客户端信息
     */
    public static void generateSignatures(ForestRequest request, RemoteClientVo clientVo) {
        String authMode = request.getHeaderValue(HeaderConstants.AUTH_MODE);
        if (!"AK-SK".equals(authMode)) {
            return;
        }
        // 生成认证签名和报文签名
        String authSignature = calculateAuthSignature(request, clientVo.getClientConfig().getSk());
        request.addHeader(HeaderConstants.AUTHORIZATION, "AK-SK-V1/" + authSignature);
        String signature = calculateSignature(request, clientVo.getClientConfig().getSk());
        request.addHeader(HeaderConstants.SIGNATURE, signature);
    }

    /**
     * 计算认证签名
     *
     * @param request   请求对象
     * @param secretKey 密钥
     * @return 认证签名
     */
    public static String calculateAuthSignature(ForestRequest request, String secretKey) {
        StringBuilder stringToSign = new StringBuilder();
        // HTTP请求信息
        String method = request.getType().name();
        String path = URI.create(request.getUrl()).getPath();
        stringToSign.append(method).append(";").append(path).append(";");

        // HTTP请求报文头
        String clientId = request.getHeaderValue(HeaderConstants.CLIENT_ID);
        String requestTime = request.getHeaderValue(HeaderConstants.REQUEST_TIME);
        String nonce = request.getHeaderValue(HeaderConstants.NONCE);
        stringToSign.append(clientId).append(";").append(requestTime).append(";").append(nonce).append(";").append(secretKey);

        return CryptoUtils.calculateSm3(stringToSign.toString());
    }

    /**
     * 计算报文签名
     *
     * @param request   请求对象
     * @param secretKey 密钥
     * @return 报文签名
     */
    public static String calculateSignature(ForestRequest request, String secretKey) {
        StringBuilder stringToSign = new StringBuilder();
        // HTTP请求报文头
        stringToSign.append(request.getHeaderValue(HeaderConstants.CLIENT_ID)).append(";")
            .append(request.getHeaderValue(HeaderConstants.REQUEST_TIME)).append(";")
            .append(request.getHeaderValue(HeaderConstants.NONCE)).append(";")
            .append(StringUtils.defaultString(request.getHeaderValue(HeaderConstants.VALID_TIME))).append(";")
            .append(StringUtils.defaultString(request.getHeaderValue(HeaderConstants.SAFE_MODE))).append(";")
            .append(StringUtils.defaultString(request.getHeaderValue(HeaderConstants.ENCRYPT_KEY))).append(";");

        // 添加body和secretKey
        String body = request.getBody() != null ? request.body().encodeToString(ForestDataType.JSON) : "";
        stringToSign.append(body).append(secretKey);

        return CryptoUtils.calculateSignature(stringToSign.toString());
    }

    /**
     * 处理响应体内容并转换为指定类型
     *
     * @param responseContent 响应数据
     * @param targetType      目标类型
     * @param <R>             返回类型泛型
     * @return 转换后的对象
     */
    public static <R> R parseResponseBody(String responseContent, Class<R> targetType) {
        if (StringUtils.isBlank(responseContent)) {
            return null;
        }
        try {
            // 使用JSON工具库将响应内容转换为指定类型对象
            return JSONUtil.toBean(responseContent, targetType);
        } catch (Exception e) {
            log.error("解析响应体失败: {}", e.getMessage());
            throw new ForestException("响应体解析失败: " + e.getMessage());
        }
    }

}
