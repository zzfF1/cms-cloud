package com.sinosoft.api.interceptor;

import cn.hutool.json.JSONUtil;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.utils.ForestDataType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sinosoft.common.core.constant.CacheNames;
import com.sinosoft.common.core.constant.HeaderConstants;
import com.sinosoft.common.core.domain.GlobalResponse;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.encrypt.utils.CryptoUtils;
import com.sinosoft.common.forest.domain.ApiResponse;
import com.sinosoft.common.forest.exception.ForestException;
import com.sinosoft.common.forest.utils.YdForestUtils;
import com.sinosoft.common.json.utils.JsonUtils;
import com.sinosoft.common.redis.utils.RedisUtils;
import com.sinosoft.system.api.domain.vo.RemoteClientVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

/**
 * 安全拦截器
 * 处理认证签名、报文加密等安全相关逻辑
 * 主要功能：
 * 1. 请求前的客户端认证、签名生成和加密处理
 * 2. 响应后的解密和类型转换
 * 3. 异常处理和日志记录
 */
@Slf4j
@Component
public class YdSecurityInterceptor<T> implements Interceptor<T> {

    // 常量定义，避免魔法字符串
    private static final String ERROR_LOG_FORMAT = "{}: {}";
    private static final String EMPTY_RESPONSE_MESSAGE = "响应体为空";
    private static final String ENCRYPTION_MODE = "1";

    /**
     * 请求执行前的处理
     * 负责客户端认证、请求加密和签名生成
     *
     * @param request 请求对象
     * @return 是否继续执行请求
     */
    @Override
    public boolean beforeExecute(ForestRequest request) {
        try {
            // 1. 获取并验证客户端信息
            String clientId = getRequiredHeader(request, HeaderConstants.CLIENT_ID, "客户端ID不能为空", "GWC002");

            // 2. 从缓存获取客户端配置并验证有效性
            RemoteClientVo clientVo = getClientInfo(clientId)
                .orElseThrow(() -> new ForestException("GWA001", "无效的客户端ID"));

            // 3. 设置请求相关信息
            YdForestUtils.addBasicHeaders(request);     // 添加基础请求头
            YdForestUtils.setRequestBody(request);      // 设置请求体
            YdForestUtils.handleEncryption(request, clientVo);  // 处理加密
            YdForestUtils.generateSignatures(request, clientVo); // 生成签名

            return true; // 继续执行请求
        } catch (Exception e) {
            // 记录异常并转换为ForestException
            log.error(ERROR_LOG_FORMAT, "接口处理失败", e.getMessage(), e);
            throw new ForestException("接口处理失败,错误信息:" + e.getMessage());
        }
    }

    /**
     * 请求成功后的处理
     * 负责响应内容的解密和类型转换
     *
     * @param data     响应数据
     * @param request  请求对象
     * @param response 响应对象
     */
    @Override
    public void onSuccess(T data, ForestRequest request, ForestResponse response) {
        log.info("执行成功，状态码: {}", response.getStatusCode());

        String content = response.getContent();

        // 判断是否需要解密
        if (isEncryptionMode(request)) {
            // 获取客户端信息并解密
            String clientId = request.getHeaderValue(HeaderConstants.CLIENT_ID);
            String encryptedKey = request.getHeaderValue(HeaderConstants.ENCRYPT_KEY);

            // 处理加密响应内容
            getClientInfo(clientId).ifPresent(clientVo -> {
                String decryptedContent = decryptContent(content, encryptedKey, clientVo);
                processResponseContent(decryptedContent, request, response);
            });
        } else {
            // 非加密模式下也需要转换为GlobalResponse
            logResponseContent(content, false);
            processResponseContent(content, request, response);
        }
    }

    /**
     * 请求发生错误时的处理
     * 负责错误响应的解密和类型转换
     *
     * @param ex       异常对象
     * @param request  请求对象
     * @param response 响应对象
     */
    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
        log.info("执行失败");
        logRequestInfo(request);

        // 处理响应内容
        if (response != null) {
            logResponseHeaders(response);
            String content = response.getContent();

            if (StringUtils.isNotBlank(content)) {
                if (isEncryptionMode(request)) {
                    // 加密模式需要先解密
                    String clientId = request.getHeaderValue(HeaderConstants.CLIENT_ID);
                    String encryptedKey = request.getHeaderValue(HeaderConstants.ENCRYPT_KEY);

                    getClientInfo(clientId).ifPresent(clientVo -> {
                        String decryptedContent = decryptContent(content, encryptedKey, clientVo);
                        processErrorResponse(decryptedContent, request, response);
                    });
                } else {
                    // 非加密模式直接处理
                    logResponseContent(content, true);
                    processErrorResponse(content, request, response);
                }
            }
        } else {
            log.info("响应为空");
        }
    }

    /**
     * 处理解密后的响应内容
     * 根据返回类型将JSON内容转换为相应对象
     *
     * @param content  响应内容
     * @param request  请求对象
     * @param response 响应对象
     */
    private void processResponseContent(String content, ForestRequest request, ForestResponse response) {
        // 验证内容是否为有效JSON
        if (StringUtils.isBlank(content) || !JSONUtil.isTypeJSON(content)) {
            log.warn("解密内容为空或不是有效JSON，无法转换为对象");
            if (StringUtils.isNotBlank(content)) {
                response.setContent(content);
            }
            return;
        }

        try {
            // 获取方法返回类型
            Type returnType = request.getMethod().getReturnType();

            // 处理GlobalResponse类型
            if (isGlobalResponseType(returnType)) {
                // 对GlobalResponse类型特殊处理
                processGlobalResponseType(content, returnType, response);
            } else {
                // 处理其他返回类型
                Object result = JsonUtils.parseObject(content, new TypeReference<Object>() {
                    @Override
                    public Type getType() {
                        return returnType;
                    }
                });

                response.setResult(result);
                log.debug("已将响应内容转换为目标类型: {}", returnType.getTypeName());
            }
        } catch (Exception e) {
            // 转换失败时记录日志并保留原始内容
            log.error("响应内容转换失败: {}", e.getMessage(), e);
            response.setContent(content);
        }
    }

    /**
     * 处理错误响应
     * 尝试将错误响应转换为GlobalResponse格式
     *
     * @param content  响应内容
     * @param request  请求对象
     * @param response 响应对象
     */
    private void processErrorResponse(String content, ForestRequest request, ForestResponse response) {
        if (StringUtils.isBlank(content)) {
            return;
        }

        try {
            // 获取方法返回类型
            Type returnType = request.getMethod().getReturnType();

            // 如果需要返回GlobalResponse，尝试转换
            if (isGlobalResponseType(returnType)) {
                ApiResponse<?> apiResponse = JsonUtils.parseObject(content, new TypeReference<ApiResponse<?>>() {});
                GlobalResponse<?> globalResponse = apiResponse.toGlobalResponse();
                response.setResult(globalResponse);
                log.debug("已将错误响应转换为GlobalResponse");
            } else {
                // 否则保留原始内容
                response.setContent(content);
            }
        } catch (Exception e) {
            // 转换失败时保留原始内容
            response.setContent(content);
        }
    }

    /**
     * 判断返回类型是否为GlobalResponse类型
     *
     * @param returnType 返回类型
     * @return 是否为GlobalResponse类型
     */
    private boolean isGlobalResponseType(Type returnType) {
        return returnType instanceof ParameterizedType &&
            GlobalResponse.class.isAssignableFrom((Class<?>) ((ParameterizedType) returnType).getRawType());
    }

    /**
     * 处理GlobalResponse类型的响应
     * 将ApiResponse转换为GlobalResponse
     *
     * @param content    响应内容
     * @param returnType 返回类型
     * @param response   响应对象
     */
    private void processGlobalResponseType(String content, Type returnType, ForestResponse response) {
        // 获取GlobalResponse的泛型参数
        ParameterizedType paramType = (ParameterizedType) returnType;
        Type[] typeArgs = paramType.getActualTypeArguments();
        Type dataType = typeArgs[0]; // 获取GlobalResponse的数据类型

        // 构造ApiResponse的类型，保留泛型信息
        Type apiResponseType = createParameterizedType(ApiResponse.class, dataType);

        // 使用带类型参数的TypeReference解析JSON
        TypeReference<ApiResponse<?>> typeReference = new TypeReference<ApiResponse<?>>() {
            @Override
            public Type getType() {
                return apiResponseType;
            }
        };

        // 解析并转换
        ApiResponse<?> apiResponse = JsonUtils.parseObject(content, typeReference);
        GlobalResponse<?> globalResponse = apiResponse.toGlobalResponse();

        // 设置结果
        response.setResult(globalResponse);
        log.debug("已将ApiResponse自动转换为GlobalResponse");
    }

    /**
     * 解密响应内容
     * 使用SM4密钥解密响应体
     *
     * @param content      加密的内容
     * @param encryptedKey 加密的SM4密钥
     * @param clientVo     客户端信息
     * @return 解密后的内容
     */
    private String decryptContent(String content, String encryptedKey, RemoteClientVo clientVo) {
        // 验证参数
        if (StringUtils.isBlank(content) || StringUtils.isBlank(encryptedKey) ||
            clientVo == null || clientVo.getClientConfig() == null) {
            return null;
        }

        try {
            // 1. 使用SM2私钥解密SM4密钥
            String sm4Key = CryptoUtils.decryptSm4Key(encryptedKey, clientVo.getClientConfig().getSm2PrivateKey());

            // 2. 使用SM4密钥解密响应体内容
            String decryptedContent = CryptoUtils.decryptBody(content, sm4Key);
            log.info("解密后的响应内容: {}", decryptedContent);
            return decryptedContent;
        } catch (Exception e) {
            // 记录解密失败的错误
            log.error(ERROR_LOG_FORMAT, "响应解密失败", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从请求头获取必要的值，如果为空则抛出异常
     *
     * @param request    请求对象
     * @param headerName 头部名称
     * @param errorMsg   错误消息
     * @param errorCode  错误代码
     * @return 头部值
     */
    private String getRequiredHeader(ForestRequest request, String headerName, String errorMsg, String errorCode) {
        String value = request.getHeaderValue(headerName);
        if (StringUtils.isBlank(value)) {
            throw new ForestException(errorCode, errorMsg);
        }
        return value;
    }

    /**
     * 检查请求是否为加密模式
     * 判断条件：safeMode=1且包含加密密钥和客户端ID
     *
     * @param request 请求对象
     * @return 是否为加密模式
     */
    private boolean isEncryptionMode(ForestRequest request) {
        String safeMode = request.getHeaderValue(HeaderConstants.SAFE_MODE);
        return ENCRYPTION_MODE.equals(safeMode) &&
            StringUtils.isNotBlank(request.getHeaderValue(HeaderConstants.ENCRYPT_KEY)) &&
            StringUtils.isNotBlank(request.getHeaderValue(HeaderConstants.CLIENT_ID));
    }

    /**
     * 从Redis获取客户端信息
     * 使用Optional避免空值检查
     *
     * @param clientId 客户端ID
     * @return 客户端信息的Optional包装
     */
    private Optional<RemoteClientVo> getClientInfo(String clientId) {
        if (StringUtils.isBlank(clientId)) {
            return Optional.empty();
        }
        try {
            // 从Redis缓存获取客户端配置
            return Optional.ofNullable(RedisUtils.getCacheMapValue(CacheNames.SYS_CLIENT_CONFIG, clientId));
        } catch (Exception e) {
            // 记录获取失败的错误
            log.error(ERROR_LOG_FORMAT, "获取客户端信息失败", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * 创建参数化类型
     * 用于构造带泛型参数的类型引用
     *
     * @param rawType 原始类型
     * @param argType 参数类型
     * @return 参数化类型
     */
    private Type createParameterizedType(Class<?> rawType, Type argType) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{argType};
            }

            @Override
            public Type getRawType() {
                return rawType;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    /**
     * 记录请求信息（头部和体）
     *
     * @param request 请求对象
     */
    private void logRequestInfo(ForestRequest request) {
        // 输出请求头
        logHeaders(request.getHeaders(), "请求头");

        // 输出请求体内容
        try {
            log.info("请求体: {}", request.body().encodeToString(ForestDataType.JSON));
        } catch (Exception e) {
            log.error(ERROR_LOG_FORMAT, "获取请求体时发生错误", e.getMessage(), e);
        }
    }

    /**
     * 记录响应头信息
     *
     * @param response 响应对象
     */
    private void logResponseHeaders(ForestResponse response) {
        if (response.getHeaders() != null) {
            logHeaders(response.getHeaders(), "响应头");
        }
    }

    /**
     * 记录头部信息
     * 格式化输出头部键值对
     *
     * @param headers    头部信息映射
     * @param headerType 头部类型描述
     */
    private void logHeaders(Map<String, String> headers, String headerType) {
        StringBuilder builder = new StringBuilder();
        headers.forEach((key, value) -> builder.append(key).append("=").append(value).append(";").append("\n"));
        log.info("{}:\n {}", headerType, builder);
    }

    /**
     * 记录响应体内容
     * 根据是否为错误响应决定日志级别
     *
     * @param content 响应体内容
     * @param isError 是否为错误响应
     */
    private void logResponseContent(String content, boolean isError) {
        if (StringUtils.isBlank(content)) {
            log.info(EMPTY_RESPONSE_MESSAGE);
            return;
        }

        // 错误响应使用info级别，正常响应使用debug级别
        if (isError) {
            log.info("原始响应体: {}", content);
        } else if (log.isDebugEnabled()) {
            log.debug("原始响应体: {}", content);
        }
    }
}
