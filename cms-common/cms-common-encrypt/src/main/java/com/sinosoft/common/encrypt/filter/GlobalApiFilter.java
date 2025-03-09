package com.sinosoft.common.encrypt.filter;

import cn.hutool.core.util.ObjectUtil;
import com.sinosoft.common.core.constant.CacheNames;
import com.sinosoft.common.core.constant.HeaderConstants;
import com.sinosoft.common.core.constant.HttpStatus;
import com.sinosoft.common.core.enums.ErrorCodeEnum;
import com.sinosoft.common.core.exception.GlobalAuthException;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.core.utils.SpringUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.redis.utils.RedisUtils;
import com.sinosoft.system.api.domain.vo.RemoteClientVo;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 统一接入 API 过滤器
 *
 * @author: zzf
 * @create: 2025-02-12 16:00
 */
@Slf4j
@Component
public class GlobalApiFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        ServletRequest requestWrapper = null;
        ServletResponse responseWrapper = null;
        GlobalApiResponseBodyWrapper responseBodyWrapper = null;
        try {
            //获取请求头信息
            //是否为加密模式
            String safeMode = servletRequest.getHeader(HeaderConstants.SAFE_MODE);
            String clientId = servletRequest.getHeader(HeaderConstants.CLIENT_ID);
            String nonce = servletRequest.getHeader(HeaderConstants.NONCE);
            String encryptKey = servletRequest.getHeader(HeaderConstants.ENCRYPT_KEY);
            //是否为aksk验证
            boolean akskFlag = false;
            RemoteClientVo clientVo = null;
            if (StringUtils.isNotBlank(clientId)) {
                // 获取客户端配置
                clientVo = RedisUtils.getCacheMapValue(CacheNames.SYS_CLIENT_CONFIG, clientId);
                if (clientVo == null) {
                    throw new GlobalAuthException(HttpStatus.FORBIDDEN, ErrorCodeEnum.AUTH_CLIENT_MISMATCH.getCode(), "无效的客户端", "客户端未授权，请联系管理员");
                }
                if (clientVo.getGrantType().contains("aksk")) {
                    // 验证客户端ID
                    if (StringUtils.isBlank(clientId)) {
                        throw new GlobalAuthException(ErrorCodeEnum.PARAM_EMPTY, "客户端ID不能为空", "请求头中必须包含有效的客户端ID");
                    }
                    // 验证 nonce
                    if (StringUtils.isBlank(nonce)) {
                        throw new GlobalAuthException(ErrorCodeEnum.PARAM_EMPTY, "nonce不能为空", "请求头中必须包含有效的nonce值");
                    }
                    akskFlag = true;
                    // 检查加密模式下的必要参数
                    if ("1".equals(safeMode)) {
                        if (StringUtils.isBlank(encryptKey)) {
                            throw new GlobalAuthException(ErrorCodeEnum.PARAM_EMPTY, "加密密钥不能为空", "安全模式下必须提供加密密钥");
                        }
                    }
                    requestWrapper = new GlobalApiRequestWrapper(servletRequest, safeMode, clientVo);
                    responseBodyWrapper = new GlobalApiResponseBodyWrapper(servletResponse);
                    responseWrapper = responseBodyWrapper;
                }
            }
            // 继续过滤器链
            chain.doFilter(
                ObjectUtil.defaultIfNull(requestWrapper, request),
                ObjectUtil.defaultIfNull(responseWrapper, response)
            );
            // 处理加密响应
            if (akskFlag) {
                servletResponse.reset();
                String responseContent = responseBodyWrapper.getResponseContent(servletResponse, nonce, safeMode, encryptKey, clientVo);
                writeResponse(servletResponse, responseContent);
            }
        } catch (GlobalAuthException e) {
            handleException(servletResponse, e);
        } catch (Exception e) {
            log.error("请求处理失败", e);
            handleException(servletResponse, new GlobalAuthException(HttpStatus.ERROR, ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode(), "系统处理异常", e.getMessage()));
        }
    }

    /**
     * 处理异常响应
     */
    private void handleException(HttpServletResponse response, GlobalAuthException e) throws IOException {
        response.setStatus(e.getCode() != null ? e.getCode() : HttpStatus.ERROR);
        writeResponse(response, e.toString());
    }

    /**
     * 写入响应内容
     */
    private void writeResponse(HttpServletResponse response, String content) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(content);
    }


    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
