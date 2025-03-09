package com.sinosoft.common.event;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sinosoft.common.schema.common.domain.*;
import com.sinosoft.common.schema.common.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.core.IBusinessProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 业务事件监听器
 *
 * @author: zzf
 * @create: 2024-12-18 17:30
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BusinessEventListener {

    /**
     * 队列状态：待处理
     * 新创建的队列记录默认状态，表示等待处理
     */
    private static final int STATUS_PENDING = 1;
    /**
     * 队列状态：处理中
     * 处理器正在执行业务逻辑的状态
     */
    private static final int STATUS_PROCESSING = 2;
    /**
     * 队列状态：处理成功
     * 业务逻辑执行完成且成功的状态
     */
    private static final int STATUS_SUCCESS = 3;
    /**
     * 队列状态：处理失败
     * 业务逻辑执行失败或发生异常的状态
     */
    private static final int STATUS_FAILED = 4;

    private final AsyncProcessConfigMapper configMapper;
    private final AsyncProcessStepMapper stepMapper;
    private final AsyncProcessQueueMapper queueMapper;
    private final AsyncProcessRequestMapper requestMapper;
    private final AsyncProcessResultMapper resultMapper;
    private final ApplicationContext applicationContext;

    /**
     * 处理异步业务事件
     * 支持单次处理和多步骤处理两种模式
     *
     * @param event 业务事件对象，包含业务编码、业务ID等信息
     */
    @Async
    @EventListener
    public void onEvent(AsyncBusinessEvent event) {
        if (Objects.isNull(event)) {
            log.warn("收到空的业务事件，忽略处理");
            return;
        }
        log.info("收到业务事件：{}", event);
        try {
            // 1. 保存请求参数
            AsyncProcessRequest request = saveRequest(event);
            // 2. 获取业务配置
            AsyncProcessConfig config = getBusinessConfig(event.getBusinessCode());
            if (config == null) {
                log.error("未找到业务配置：{}", event.getBusinessCode());
                return;
            }
            // 3. 根据配置决定处理方式
            if (config.getStepFlag() == 1) {
                processWithSteps(event, config, request.getId());
            } else {
                processWithoutSteps(event, config, request.getId());
            }
        } catch (Exception e) {
            log.error("业务事件处理异常：{}", event, e);
        }
    }

    /**
     * 保存异步处理请求参数
     *
     * @param event 业务事件对象
     * @return 保存后的请求参数对象
     */
    private AsyncProcessRequest saveRequest(AsyncBusinessEvent event) {
        AsyncProcessRequest request = new AsyncProcessRequest();
        request.setBusinessCode(event.getBusinessCode());
        request.setBusinessId(event.getBusinessId());
        request.setParameters(event.getJsonData());
        requestMapper.insert(request);
        return request;
    }

    /**
     * 获取业务配置信息
     *
     * @param businessCode 业务编码
     * @return 业务配置对象，如不存在则返回null
     */
    private AsyncProcessConfig getBusinessConfig(String businessCode) {
        return configMapper.selectOne(
            Wrappers.<AsyncProcessConfig>lambdaQuery()
                .eq(AsyncProcessConfig::getBusinessCode, businessCode)
        );
    }

    /**
     * 处理多步骤业务流程
     * 为每个步骤创建队列记录，并按顺序执行
     *
     * @param event     业务事件对象
     * @param config    业务配置信息
     * @param requestId 请求参数ID
     */
    private void processWithSteps(AsyncBusinessEvent event, AsyncProcessConfig config, Long requestId) {
        // 1. 获取所有启用的步骤配置
        List<AsyncProcessStep> steps = stepMapper.selectList(
            Wrappers.<AsyncProcessStep>lambdaQuery()
                .eq(AsyncProcessStep::getBusinessCode, config.getBusinessCode())
                .eq(AsyncProcessStep::getStatus, 1)
                .orderByAsc(AsyncProcessStep::getStepOrder)
        );
        if (CollUtil.isEmpty(steps)) {
            log.warn("业务{}未配置处理步骤", config.getBusinessCode());
            return;
        }
        // 2. 为每个步骤创建队列记录
        List<AsyncProcessQueue> queues = steps.stream().map(step -> {
            AsyncProcessQueue queue = new AsyncProcessQueue();
            queue.setBusinessCode(event.getBusinessCode());
            queue.setBusinessId(event.getBusinessId());
            queue.setRequestId(requestId);
            queue.setAsyncStepId(step.getId());
            queue.setStatus(STATUS_PENDING);
            queueMapper.insert(queue);
            return queue;
        }).toList();

        // 3. 按顺序处理每个步骤
        for (int i = 0; i < queues.size(); i++) {
            AsyncProcessQueue currentQueue = queues.get(i);
            AsyncProcessStep currentStep = steps.get(i);
            try {
                // 更新当前步骤状态为处理中
                updateQueueStatus(currentQueue.getId(), STATUS_PROCESSING);
                // 执行处理器
                IBusinessProcessor processor = applicationContext.getBean(currentStep.getProcessBean(), IBusinessProcessor.class);
                boolean success = processor.process();
                if (success) {
                    // 记录成功结果
                    saveProcessResult(currentQueue, currentStep.getId(), "步骤处理成功", STATUS_SUCCESS);
                    // 删除当前步骤队列
                    deleteQueue(currentQueue.getId());
                } else {
                    // 记录失败结果并更新状态
                    saveProcessResult(currentQueue, currentStep.getId(), "步骤处理失败", STATUS_FAILED);
                    updateQueueStatus(currentQueue.getId(), STATUS_FAILED);
                }
            } catch (Exception e) {
                log.error("步骤处理异常", e);
                // 记录异常结果并更新状态
                saveProcessResult(currentQueue, currentStep.getId(), e.getMessage(), STATUS_FAILED);
                updateQueueStatus(currentQueue.getId(), STATUS_FAILED);
            }
        }
    }


    /**
     * 处理无步骤的业务流程
     * 创建单个处理队列并执行
     *
     * @param event     业务事件对象
     * @param config    业务配置信息
     * @param requestId 请求参数ID
     */
    private void processWithoutSteps(AsyncBusinessEvent event, AsyncProcessConfig config, Long requestId) {
        AsyncProcessQueue queue = new AsyncProcessQueue();
        queue.setBusinessCode(event.getBusinessCode());
        queue.setBusinessId(event.getBusinessId());
        queue.setRequestId(requestId);
        queue.setStatus(STATUS_PENDING);
        queueMapper.insert(queue);
        try {
            updateQueueStatus(queue.getId(), STATUS_PROCESSING);
            IBusinessProcessor processor = applicationContext.getBean(config.getProcessBean(), IBusinessProcessor.class);
            boolean success = processor.process();
            if (success) {
                saveProcessResult(queue, null, "处理成功", STATUS_SUCCESS);
                deleteQueue(queue.getId());
                deleteRequest(requestId);
            } else {
                saveProcessResult(queue, null, "处理失败", STATUS_FAILED);
                updateQueueStatus(queue.getId(), STATUS_FAILED);
            }
        } catch (Exception e) {
            log.error("业务处理异常", e);
            saveProcessResult(queue, null, e.getMessage(), STATUS_FAILED);
            updateQueueStatus(queue.getId(), STATUS_FAILED);
        }
    }

    /**
     * 保存处理结果
     *
     * @param queue   处理队列对象
     * @param stepId  步骤ID，如果不是步骤处理则为null
     * @param message 处理消息
     * @param status  处理状态
     */
    private void saveProcessResult(AsyncProcessQueue queue, Long stepId, String message, int status) {
        AsyncProcessResult result = new AsyncProcessResult();
        result.setBusinessCode(queue.getBusinessCode());
        result.setBusinessId(queue.getBusinessId());
        result.setRequestId(queue.getRequestId());
        result.setAsyncStepId(stepId != null ? stepId : 0L);
        result.setStatus(status == STATUS_SUCCESS ? 1 : 0);
        result.setErrorMessage(status == STATUS_SUCCESS ? null : message);
        result.setProcessTime(new Date());
        resultMapper.insert(result);
    }

    /**
     * 更新队列状态
     *
     * @param queueId 队列ID
     * @param status  更新后的状态
     */
    private void updateQueueStatus(Long queueId, int status) {
        queueMapper.update(
            null,
            Wrappers.<AsyncProcessQueue>lambdaUpdate()
                .set(AsyncProcessQueue::getStatus, status)
                .set(AsyncProcessQueue::getUpdateTime, new Date())
                .eq(AsyncProcessQueue::getId, queueId)
        );
    }

    /**
     * 删除处理队列
     *
     * @param queueId 队列ID
     */
    private void deleteQueue(Long queueId) {
        queueMapper.deleteById(queueId);
    }

    /**
     * 删除请求参数
     *
     * @param requestId 请求参数ID
     */
    private void deleteRequest(Long requestId) {
        requestMapper.deleteById(requestId);
    }
}
