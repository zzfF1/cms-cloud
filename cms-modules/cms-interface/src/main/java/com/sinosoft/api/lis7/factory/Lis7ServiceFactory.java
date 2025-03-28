package com.sinosoft.api.lis7.factory;

import com.sinosoft.api.lis7.client.Lis7Client;
import com.sinosoft.api.lis7.constants.Lis7BusinessCodes;
import com.sinosoft.api.lis7.util.Lis7RequestBuilder;
import com.sinosoft.common.core.domain.GlobalResponse;
import com.sinosoft.integration.api.core.Lis7HttpRequest;
import com.sinosoft.integration.api.core.Lis7HttpResponse;
import com.sinosoft.integration.api.core.ResponseBaseDto;
import com.sinosoft.integration.api.lis7.model.OrphanPolicyAssignmentResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * LIS7服务工厂
 * 用于动态创建和管理LIS7服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Lis7ServiceFactory {

    private final Lis7Client lis7Client;

    // 存储业务代码与响应类型的映射关系
    private final Map<String, Class<? extends ResponseBaseDto>> responseTypeMap = new HashMap<>();

    /**
     * 初始化工厂，注册默认业务代码与响应类型
     */
    @PostConstruct
    public void init() {
        // 注册默认业务类型
        registerResponseType(Lis7BusinessCodes.ORPHAN_POLICY_ASSIGNMENT, OrphanPolicyAssignmentResponseDto.class);
        registerResponseType(Lis7BusinessCodes.SYNC_AGENT, Lis7HttpResponse.class);
        registerResponseType(Lis7BusinessCodes.SYNC_TREE, Lis7HttpResponse.class);

        log.info("LIS7服务工厂初始化完成，已注册 {} 个业务代码", responseTypeMap.size());
    }

    /**
     * 注册业务代码和对应的响应类型
     */
    public <R extends ResponseBaseDto> void registerResponseType(String businessCode, Class<R> responseType) {
        responseTypeMap.put(businessCode, responseType);
        log.info("已注册业务代码 {} 对应的响应类型: {}", businessCode, responseType.getName());
    }

    /**
     * 发送请求并处理响应
     */
    @SuppressWarnings("unchecked")
    public <T, R extends ResponseBaseDto> R sendRequest(T inputData, String businessCode) {
        try {
            log.debug("处理LIS7请求, 业务代码: {}, 数据类型: {}", businessCode, inputData.getClass().getSimpleName());

            // 获取响应类型
            Class<R> responseType = (Class<R>) responseTypeMap.getOrDefault(
                businessCode,
                Lis7HttpResponse.class
            );

            // 构建请求
            Lis7HttpRequest<T> request = Lis7RequestBuilder.buildRequest(inputData, businessCode);

            // 发送请求
            GlobalResponse<R> response = lis7Client.sendRequest(request);

            // 处理响应
            return processResponse(response, () -> {
                try {
                    return responseType.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    log.error("创建响应对象实例失败: {}", e.getMessage(), e);
                    return null;
                }
            });
        } catch (Exception e) {
            log.error("处理LIS7请求失败, 业务代码: {}, 错误: {}", businessCode, e.getMessage(), e);

            // 创建默认错误响应
            try {
                Class<R> responseType = (Class<R>) responseTypeMap.getOrDefault(
                    businessCode,
                    Lis7HttpResponse.class
                );

                R errorResponse = responseType.getDeclaredConstructor().newInstance();
                errorResponse.setSuccess(false);
                errorResponse.setCode("500");
                errorResponse.setMessage("处理请求失败: " + e.getMessage());
                return errorResponse;
            } catch (Exception ex) {
                log.error("创建错误响应失败: {}", ex.getMessage(), ex);
                throw new RuntimeException("处理LIS7请求失败", e);
            }
        }
    }

    /**
     * 处理响应结果
     */
    private <T extends ResponseBaseDto> T processResponse(GlobalResponse<T> response, Supplier<T> defaultSupplier) {
        log.debug("处理LIS7响应: {}", response);

        T result = response.getData();
        if (result == null) {
            log.info("响应数据为空，使用默认值");
            result = defaultSupplier.get();
        }

        // 设置基础响应字段
        if (result != null) {
            result.setSuccess(response.isSuccess());
            result.setCode(String.valueOf(response.getCode()));
            result.setMessage(response.getMessage());
        }

        log.debug("响应处理完成");
        return result;
    }
}
