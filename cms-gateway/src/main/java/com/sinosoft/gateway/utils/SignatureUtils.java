package com.sinosoft.gateway.utils;

import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.sinosoft.common.core.constant.HeaderConstants;
import com.sinosoft.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 签名工具类
 *
 * @author: zzf
 * @create: 2025-02-08 15:45
 */
@Slf4j
public class SignatureUtils {

    /**
     * 计算认证签名
     *
     * @param request   请求对象
     * @param secretKey 密钥
     * @return 签名结果
     */
    public static String calculateAuthSignature(ServerHttpRequest request, String secretKey) {
        StringBuilder stringToSign = new StringBuilder();
        // 1. Format（HTTP请求信息）- POST 请求只需要方法和路径
        String method = request.getMethod().name();
        String path = request.getURI().getPath();
        // POST 请求不需要处理查询参数
        stringToSign.append(method).append(";").append(path).append(";");
        // 2. Format（HTTP请求报文头）
        String clientId = request.getHeaders().getFirst(HeaderConstants.CLIENT_ID);
        String requestTime = request.getHeaders().getFirst(HeaderConstants.REQUEST_TIME);
        String nonce = request.getHeaders().getFirst(HeaderConstants.NONCE);
        // 按顺序添加请求头
        stringToSign.append(clientId).append(";").append(requestTime).append(";").append(nonce).append(";");
        // 3. 添加 SK
        stringToSign.append(secretKey);
        return SmUtil.sm3(stringToSign.toString());
    }

    /**
     * 计算报文签名
     *
     * @param request   请求对象
     * @param secretKey 密钥
     * @return 签名结果
     */
    public static String calculateSignature(ServerHttpRequest request, String secretKey) {
        // 1. Format（HTTP请求报文头）
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append(request.getHeaders().getFirst(HeaderConstants.CLIENT_ID)).append(";")
            .append(request.getHeaders().getFirst(HeaderConstants.REQUEST_TIME)).append(";")
            .append(request.getHeaders().getFirst(HeaderConstants.NONCE)).append(";")
            .append(StringUtils.defaultString(request.getHeaders().getFirst(HeaderConstants.VALID_TIME))).append(";")
            .append(StringUtils.defaultString(request.getHeaders().getFirst(HeaderConstants.SAFE_MODE))).append(";")
            .append(StringUtils.defaultString(request.getHeaders().getFirst(HeaderConstants.ENCRYPT_KEY))).append(";");
        ServerWebExchange exchange = SaReactorSyncHolder.getContext();
        String jsonParam = WebFluxUtils.resolveBodyFromCacheRequest(exchange);
        stringToSign.append(jsonParam).append(secretKey);
        // 3. 计算签名
        String md5Hex = DigestUtil.md5Hex(stringToSign.toString());
        return SmUtil.sm3(md5Hex);
    }
}
