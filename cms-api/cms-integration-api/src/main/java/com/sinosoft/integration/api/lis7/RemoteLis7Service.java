package com.sinosoft.integration.api.lis7;

import cn.hutool.core.util.IdUtil;
import com.sinosoft.common.core.domain.GlobalResponse;
import com.sinosoft.common.core.utils.DateUtils;
import com.sinosoft.integration.api.core.ClientInfo;
import com.sinosoft.integration.api.core.Lis7HttpRequest;
import com.sinosoft.integration.api.core.Lis7HttpResponse;
import com.sinosoft.integration.api.core.ResponseBaseDto;
import com.sinosoft.integration.api.lis7.model.OrphanPolicyAssignmentDto;
import com.sinosoft.integration.api.lis7.model.OrphanPolicyAssignmentResponseDto;
import com.sinosoft.integration.api.lis7.model.SyncBaseAgentDto;
import com.sinosoft.integration.api.lis7.model.SyncBaseTreeDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;

/**
 * lis7服务接口
 */
public interface RemoteLis7Service {

    /**
     * 日志对象
     */
    Logger log = LoggerFactory.getLogger(RemoteLis7Service.class);

    /**
     * 孤儿单分配接口
     * 用于处理无代理人保单的自动分配
     *
     * @param grpContNos 孤儿单分配请求对象
     * @return 孤儿单分配响应对象
     */
    OrphanPolicyAssignmentResponseDto assignOrphanPolicy(OrphanPolicyAssignmentDto grpContNos);

    /**
     * 基础信息同步
     *
     * @param agentDto 人员职级信息
     * @return 同步结果
     */
    Lis7HttpResponse syncAgent(SyncBaseAgentDto agentDto);

    /**
     * 职级信息同步
     *
     * @param treeDto 人员职级信息
     * @return 同步结果
     */
    Lis7HttpResponse syncTree(SyncBaseTreeDto treeDto);

    /**
     * 构建请求对象
     */
    default <T> Lis7HttpRequest<T> buildRequest(T inputData, String businessCode) {
        log.debug("开始构建请求对象，业务代码: {}", businessCode);
        ClientInfo clientInfo = createDefaultClientInfo(businessCode);
        Lis7HttpRequest<T> request = new Lis7HttpRequest<>();
        request.setClientInfo(clientInfo);
        request.setInputData(inputData);
        log.debug("请求对象构建完成: {}", request);
        return request;
    }

    /**
     * 处理响应结果
     */
    default <T extends ResponseBaseDto> T processResponse(GlobalResponse<T> response, Supplier<T> defaultSupplier) {
        log.debug("开始处理响应结果: {}", response);
        T result = response.getData();
        if (result == null) {
            log.info("响应数据为空，使用默认值");
            result = defaultSupplier.get();
        }
        defaultResponse(response, result);
        log.debug("响应处理完成: {}", result);
        return result;
    }

    /**
     * 默认设置响应结果
     */
    default <T extends ResponseBaseDto> void defaultResponse(GlobalResponse<?> response, T result) {
        if (response != null && result != null) {
            log.trace("设置响应结果字段, 成功状态: {}, 代码: {}", response.isSuccess(), response.getCode());
            result.setSuccess(response.isSuccess());
            result.setCode(String.valueOf(response.getCode()));
            result.setMessage(response.getMessage());
        } else {
            log.warn("响应或结果对象为空，无法设置响应结果字段");
        }
    }

    /**
     * 生成请求头
     */
    default ClientInfo createDefaultClientInfo(String businessCode) {
        log.trace("创建默认请求头，业务代码: {}", businessCode);
        ClientInfo info = ClientInfo.builder()
            .dealType("salem")
            .date(DateUtils.getDate())
            .time(DateUtils.getTimeWithHourMinuteSecond())
            .seqNo(IdUtil.fastSimpleUUID())
            .businessCode(businessCode)
            .subBusinessCode("1")
            .build();
        log.trace("请求头创建完成: {}", info);
        return info;
    }
}
