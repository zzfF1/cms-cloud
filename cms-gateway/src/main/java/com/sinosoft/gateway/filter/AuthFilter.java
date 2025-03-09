package com.sinosoft.gateway.filter;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.sinosoft.common.core.constant.CacheNames;
import com.sinosoft.common.core.constant.HttpStatus;
import com.sinosoft.common.core.domain.GlobalResponse;
import com.sinosoft.common.core.exception.GlobalAuthException;
import com.sinosoft.common.core.exception.SseException;
import com.sinosoft.common.core.utils.SpringUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.redis.utils.RedisUtils;
import com.sinosoft.common.satoken.utils.LoginHelper;
import com.sinosoft.gateway.config.properties.IgnoreWhiteProperties;
import com.sinosoft.common.core.constant.HeaderConstants;
import com.sinosoft.gateway.utils.HeaderValidationUtils;
import com.sinosoft.gateway.utils.SignatureUtils;
import com.sinosoft.gateway.utils.WebFluxUtils;
import com.sinosoft.system.api.domain.vo.RemoteClientVo;
import com.sinosoft.common.core.enums.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

/**
 * [Sa-Token 权限认证] 拦截器
 *
 * @author zzf
 */
@Slf4j
@Configuration
public class AuthFilter {

    private static final long EXPIRE_TIME = 300L; // 5分钟过期时间

    /**
     * 注册 Sa-Token 全局过滤器
     */
    @Bean
    public SaReactorFilter getSaReactorFilter(IgnoreWhiteProperties ignoreWhite) {
        return new SaReactorFilter()
            // 拦截地址
            .addInclude("/**")
            .addExclude("/favicon.ico", "/actuator", "/actuator/**")
            // 鉴权方法：每次访问进入
            .setAuth(obj -> {
                // 登录校验 -- 拦截所有路由
                SaRouter.match("/**")
                    .notMatch(ignoreWhite.getWhites())
                    .check(r -> {
                        ServerHttpRequest request = SaReactorSyncHolder.getContext().getRequest();
                        // 获取认证方式
                        String authMode = request.getHeaders().getFirst(HeaderConstants.AUTH_MODE);
                        if (StringUtils.isEmpty(authMode)) {
                            authMode = "JWT";
                        }
                        // 根据认证方式选择不同的认证逻辑
                        switch (authMode.toUpperCase()) {
                            case "JWT":
                                // JWT认证逻辑
                                handleJwtAuth(request);
                                break;
                            case "AK-SK":
                                // AKSK认证逻辑
                                handleAkSkAuth(request);
                                break;
                            default:
                                throw new NotLoginException("不支持的认证方式: " + authMode, StpUtil.getLoginType(), "-100");
                        }
                        // 有效率影响 用于临时测试
                        // if (log.isDebugEnabled()) {
                        //     log.debug("剩余有效时间: {}", StpUtil.getTokenTimeout());
                        //     log.debug("临时有效时间: {}", StpUtil.getTokenActivityTimeout());
                        // }
                    });
            }).setError(e -> {
                if (e instanceof NotLoginException) {
                    return SaResult.error(e.getMessage()).setCode(HttpStatus.UNAUTHORIZED);
                } else if (e instanceof GlobalAuthException) {
                    GlobalAuthException authException = (GlobalAuthException) e;
                    return authException.toString();
                }
                return SaResult.error("认证失败，无法访问系统资源!!").setCode(HttpStatus.UNAUTHORIZED);
            });
    }

    /**
     * 处理JWT鉴权
     *
     * @param request 请求
     */
    private void handleJwtAuth(ServerHttpRequest request) {
        // 检查是否登录
        try {
            StpUtil.checkLogin();
        } catch (NotLoginException e) {
            if (request.getURI().getPath().contains("sse")) {
                throw new SseException(e.getMessage(), e.getCode());
            } else {
                throw e;
            }
        }
        // 检查 header 与 param 里的 clientid 与 token 里的是否一致
        String headerCid = request.getHeaders().getFirst(LoginHelper.CLIENT_KEY);
        String paramCid = request.getQueryParams().getFirst(LoginHelper.CLIENT_KEY);
        String clientId = StpUtil.getExtra(LoginHelper.CLIENT_KEY).toString();
        if (!StringUtils.equalsAny(clientId, headerCid, paramCid)) {
            throw NotLoginException.newInstance(StpUtil.getLoginType(),
                "-100", "客户端ID与Token不匹配", StpUtil.getTokenValue());
        }
    }

    /**
     * 处理AK/SK鉴权
     *
     * @param request 请求
     */
    private void handleAkSkAuth(ServerHttpRequest request) {
        //校验必填请求头参数
        String validateResult = HeaderValidationUtils.validateRequiredHeaders(request);
        if (validateResult != null) {
            throw new GlobalAuthException(ErrorCodeEnum.PARAM_MISSING, validateResult);
        }

        {
            //TODO 请求输出
            HttpHeaders headers = request.getHeaders();
            StringBuilder headersLog = new StringBuilder("Request Headers: {");
            headers.forEach((name, values) -> {
                headersLog.append("\n  ").append(name).append(": ").append(String.join(", ", values));
            });
            headersLog.append("\n}");
            headersLog.append("\nRequest URI: ").append(request.getURI());
            ServerWebExchange exchange = SaReactorSyncHolder.getContext();
            String jsonParam = WebFluxUtils.resolveBodyFromCacheRequest(exchange);
            headersLog.append("\nbody: ").append(jsonParam);
            log.info(headersLog.toString());
        }

        // 获取必要的请求头参数
        String authorization = request.getHeaders().getFirst(HeaderConstants.AUTHORIZATION);
        String clientId = request.getHeaders().getFirst(HeaderConstants.CLIENT_ID);
        String requestTime = request.getHeaders().getFirst(HeaderConstants.REQUEST_TIME);
        String nonce = request.getHeaders().getFirst(HeaderConstants.NONCE);
        String signature = request.getHeaders().getFirst(HeaderConstants.SIGNATURE);

        // 校验认证信息格式
        validateResult = HeaderValidationUtils.validateAuthorizationFormat(authorization);
        if (validateResult != null) {
            throw new GlobalAuthException(ErrorCodeEnum.AUTH_REQUEST_EXPIRED, validateResult);
        }
        // 校验请求时间戳
        validateResult = HeaderValidationUtils.validateRequestTime(requestTime, EXPIRE_TIME);
        if (validateResult != null) {
            throw new GlobalAuthException(ErrorCodeEnum.AUTH_REQUEST_EXPIRED, validateResult);
        }
        // 校验 Nonce 防止重放攻击
        validateResult = HeaderValidationUtils.validateNonce(clientId, nonce);
        if (validateResult != null) {
            throw new GlobalAuthException(ErrorCodeEnum.AUTH_NONCE_INVALID, validateResult);
        }
        // 校验 Nonce 防止重放攻击
        validateResult = HeaderValidationUtils.checkApiPermission(request, clientId);
        if (validateResult != null) {
            throw new GlobalAuthException(ErrorCodeEnum.AUTH_PERMISSION_DENIED, validateResult);
        }
        // 获取客户端信息
        RemoteClientVo clientVo = RedisUtils.getCacheMapValue(CacheNames.SYS_CLIENT_CONFIG, clientId);
        if (clientVo == null || "1".equals(clientVo.getStatus()) || clientVo.getClientConfig() == null) {
            throw new GlobalAuthException(ErrorCodeEnum.AUTH_CLIENT_MISMATCH, "无效的客户端ID");
        }

        // 验证认证签名
        String authSignature = HeaderValidationUtils.extractAuthSignature(authorization);
        String secretKey = clientVo.getClientConfig().getSk();
        String calculatedAuth = SignatureUtils.calculateAuthSignature(request, secretKey);
        if (!StringUtils.equals(calculatedAuth, authSignature)) {
            throw new GlobalAuthException(ErrorCodeEnum.AUTH_SIGNATURE_INVALID, "无效的认证签名");
        }
        // 验证报文签名
        String calculatedSign = SignatureUtils.calculateSignature(request, secretKey);
        if (!StringUtils.equals(calculatedSign, signature)) {
            throw new GlobalAuthException(ErrorCodeEnum.AUTH_SIGNATURE_INVALID, "无效的报文签名");
        }

    }

    /**
     * 对 actuator 健康检查接口 做账号密码鉴权
     */
    @Bean
    public SaReactorFilter actuatorFilter() {
        String username = SpringUtils.getProperty("spring.cloud.nacos.discovery.metadata.username");
        String password = SpringUtils.getProperty("spring.cloud.nacos.discovery.metadata.userpassword");
        return new SaReactorFilter()
            .addInclude("/actuator", "/actuator/**")
            .setAuth(obj -> SaHttpBasicUtil.check(username + ":" + password))
            .setError(e -> SaResult.error(e.getMessage()).setCode(HttpStatus.UNAUTHORIZED));
    }

}
