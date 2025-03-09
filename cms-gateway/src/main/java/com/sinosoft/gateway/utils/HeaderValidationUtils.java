package com.sinosoft.gateway.utils;

import com.sinosoft.common.core.constant.CacheNames;
import com.sinosoft.common.core.enums.ErrorCodeEnum;
import com.sinosoft.common.core.exception.GlobalAuthException;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.core.constant.HeaderConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import com.sinosoft.common.redis.utils.RedisUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 请求头校验工具类，用于校验必填参数、时间戳、随机数（Nonce）等，
 * 防止请求篡改或重放攻击
 *
 * @author zzf
 */
@Slf4j
public class HeaderValidationUtils {
    // 定义认证信息的固定前缀
    private static final String AUTHORIZATION_PREFIX = "AK-SK-V1/";
    private static final long NONCE_EXPIRE_MINUTES = 30L;

    /**
     * 校验请求头中必填参数是否存在
     *
     * @param request 当前请求
     * @return 校验失败时返回错误信息，否则返回 null
     */
    public static String validateRequiredHeaders(ServerHttpRequest request) {
        List<String> missingParams = new ArrayList<>();
        if (StringUtils.isBlank(request.getHeaders().getFirst(HeaderConstants.AUTHORIZATION))) {
            missingParams.add("authorization");
        }
        if (StringUtils.isBlank(request.getHeaders().getFirst(HeaderConstants.CLIENT_ID))) {
            missingParams.add("clientid");
        }
        if (StringUtils.isBlank(request.getHeaders().getFirst(HeaderConstants.REQUEST_TIME))) {
            missingParams.add("request-time");
        }
        if (StringUtils.isBlank(request.getHeaders().getFirst(HeaderConstants.NONCE))) {
            missingParams.add("nonce");
        }
        if (StringUtils.isBlank(request.getHeaders().getFirst(HeaderConstants.SIGNATURE))) {
            missingParams.add("signature");
        }
        if (!missingParams.isEmpty()) {
            return "缺少必填请求头: " + String.join(", ", missingParams);
        }
        return null;
    }

    /**
     * 校验请求时间戳是否在允许范围内
     *
     * @param requestTime 请求时间字符串
     * @param expireTime  允许的过期时间（秒）
     * @return 校验失败时返回错误信息，否则返回 null
     */
    public static String validateRequestTime(String requestTime, long expireTime) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            LocalDateTime reqTime = LocalDateTime.parse(requestTime, formatter);
            LocalDateTime now = LocalDateTime.now();
            long secondsDiff = Math.abs(java.time.Duration.between(reqTime, now).getSeconds());
            if (secondsDiff > expireTime) {
                return "请求已过期";
            }
            return null;
        } catch (Exception e) {
            log.error("时间格式解析异常", e);
            return "无效的请求时间格式";
        }
    }

    /**
     * 校验 Nonce 是否重复，用于防止重放攻击
     *
     * @param clientId 客户端ID
     * @param nonce    请求中的随机数
     * @return 如果重复则返回错误信息，否则返回 null
     */
    public static String validateNonce(String clientId, String nonce) {
        String key = String.format("nonce:%s:%s", clientId, nonce);
        // 利用 Redis 的原子操作设置一个 30 分钟的缓存，若已存在则表示重复
        boolean isUnique = RedisUtils.setObjectIfAbsent(key, "", Duration.ofMinutes(NONCE_EXPIRE_MINUTES));
        return !isUnique ? "重复的请求随机数" : null;
    }

    /**
     * 检查API接口权限
     *
     * @param request  请求
     * @param clientId 客户端ID
     */
    public static String checkApiPermission(ServerHttpRequest request, String clientId) {
        String path = request.getURI().getPath();
        String permissionKey = CacheNames.SYS_CLIENT_API_PERMISSION + ":" + clientId;
        boolean isExists = RedisUtils.hasHashKey(permissionKey, path);
        return !isExists ? "权限不足" : null;
    }

    /**
     * 校验认证信息格式是否符合预期
     *
     * @param authorization 认证信息字符串
     * @return 格式不正确时返回错误信息，否则返回 null
     */
    public static String validateAuthorizationFormat(String authorization) {
        if (!authorization.startsWith(AUTHORIZATION_PREFIX)) {
            return "无效的认证信息格式";
        }
        return null;
    }

    /**
     * 从认证信息中提取签名部分
     *
     * @param authorization 认证信息字符串
     * @return 提取后的签名
     */
    public static String extractAuthSignature(String authorization) {
        return authorization.substring(AUTHORIZATION_PREFIX.length());
    }
}
