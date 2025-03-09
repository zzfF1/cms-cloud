package com.sinosoft.common.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.domain.dto.CalPerson;
import com.sinosoft.common.domain.dto.CommissionCalMainDto;
import com.sinosoft.common.domain.dto.WageCalAgentDto;
import com.sinosoft.common.enums.CalBatchStatus;
import com.sinosoft.common.enums.RabbitQueue;
import com.sinosoft.common.mq.WageRabbitMQUtil;
import com.sinosoft.common.schema.agent.domain.Laagent;
import com.sinosoft.common.schema.agent.domain.Latree;
import com.sinosoft.common.schema.commission.domain.WageCalculationBatch;
import com.sinosoft.common.schema.common.domain.BaseLawVersion;
import com.sinosoft.common.schema.common.domain.WageCalculationDefinition;
import com.sinosoft.common.service.ICommissionService;
import com.sinosoft.common.service.IWageCalculationService;
import com.sinosoft.common.utils.wage.WageRedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 佣金计算基础类 - 负责佣金计算的整体流程控制
 * 实现了按步长顺序进行佣金计算的功能，确保低步长的佣金项计算完成后再计算高步长的佣金项
 *
 * @author: zzf
 * @create: 2025-02-26 19:21
 * @update: 2025-03-04 10:00 - 优化为在Redis中统计状态，批量更新数据库
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WageCalculationBase {

    private final ICommissionService commissionService;
    private final ScheduledExecutorService scheduledExecutorService;
    private final WageRabbitMQUtil wageRabbitMQUtil;
    private final IWageCalculationService wageCalculationService;

    private static final int MAX_CHECK_COUNT = 600;

    /**
     * 佣金计算主入口方法
     * 按步长顺序执行佣金计算，确保低步长的佣金项计算完成后再计算高步长的佣金项
     *
     * @param wageCalDelayedDto     佣金计算参数
     * @param commissionInitHandler 佣金计算初始化处理器
     * @return 执行结果，true表示成功，false表示失败
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean calculation(CommissionCalMainDto wageCalDelayedDto, ICommissionBaseHandler commissionInitHandler) {
        log.info("佣金计算开始 - 管理机构: {}, 计算期间: {}, 渠道类型: {}, 佣金类型: {}",
            wageCalDelayedDto.getManageCom(),
            wageCalDelayedDto.getIndexCalNo(),
            wageCalDelayedDto.getBranchType(),
            wageCalDelayedDto.getWageType());
        WageCalculationBatch batch = null;
        try {
            // 参数校验
            validateParameters(wageCalDelayedDto, commissionInitHandler);
            // 获取基本法版本并检查佣金状态
            BaseLawVersion baseLawVersion = commissionService.selectById(wageCalDelayedDto.getBaseLawVersion());
            if (baseLawVersion == null) {
                throw new ServiceException(StrUtil.format("{}渠道 {}管理机构,基本法版本为空!",
                    wageCalDelayedDto.getBranchType().getCode(),
                    wageCalDelayedDto.getManageCom()));
            }
            log.debug("佣金计算使用基本法版本: {}, 版本ID: {}", baseLawVersion.getName(), baseLawVersion.getId());
            // 检查佣金状态
            commissionInitHandler.checkAndInitCommisionState(wageCalDelayedDto);
            // 生成计算批次号
            wageCalDelayedDto.generateMd5Val();
            wageCalDelayedDto.generateUuid();
            // 获取计算人员
            CalPerson calPerson = commissionInitHandler.getCalAgents(wageCalDelayedDto);
            if (calPerson == null) {
                throw new ServiceException("计算人员获取失败，返回为空!");
            }
            List<WageCalAgentDto> agentList = calPerson.getWageCalAgentDtoList();
            int agentCount = agentList != null ? agentList.size() : 0;
            log.info("成功获取佣金计算人员: {}人", agentCount);
            // 创建批次记录
            batch = createBatchRecord(wageCalDelayedDto, agentCount);

            // 没有计算人员时直接完成
            if (CollUtil.isEmpty(agentList)) {
                log.info("佣金计算人员为空，不进行计算! 管理机构: {}", batch.getManageCom());
                updateBatchStatus(batch, CalBatchStatus.COMPLETED, null);
                return true;
            }
            // 更新批次状态为运行中
            updateBatchStatus(batch, CalBatchStatus.RUNNING, null);
            // 初始化Redis
            WageRedisUtil wageRedisUtil = new WageRedisUtil(wageCalDelayedDto.getMd5Val(), batch.getRunId());

            // 初始化Redis中的计数统计
            wageRedisUtil.setCalParmsVal("BATCH_STATS:" + batch.getRunId() + ":PENDING_COUNT", String.valueOf(agentCount));
            wageRedisUtil.setCalParmsMapVal("BATCH_STATS:" + batch.getRunId(), "SUCCESS_COUNT", "0");
            wageRedisUtil.setCalParmsMapVal("BATCH_STATS:" + batch.getRunId(), "FAILED_COUNT", "0");

            // 初始化佣金计算规则
            if (!initCalculationRules(wageCalDelayedDto, calPerson.getLaAgentMap(), calPerson.getLaTreeMap(), wageRedisUtil)) {
                updateBatchStatus(batch, CalBatchStatus.FAILED, "初始化佣金计算规则失败");
                return false;
            }
            // 计算前准备
            try {
                commissionInitHandler.calPreparation(wageCalDelayedDto, wageRedisUtil);
            } catch (Exception e) {
                log.error("计算前准备异常: {}", e.getMessage(), e);
                updateBatchStatus(batch, CalBatchStatus.FAILED, "计算前准备失败: " + e.getMessage());
                return false;
            }
            // 获取所有步长
            List<Integer> allSteps = getAllSteps(wageRedisUtil);
            if (CollUtil.isEmpty(allSteps)) {
                log.error("没有找到任何计算步长! 管理机构: {}", batch.getManageCom());
                updateBatchStatus(batch, CalBatchStatus.FAILED, "没有找到任何计算步长");
                return false;
            }
            // 更新批次的总步长
            batch.setTotalSteps(allSteps.size());
            wageCalculationService.updateCalculationBatch(batch);
            // 将步长信息存入Redis
            wageRedisUtil.setStepsList(allSteps);
            for (Integer step : allSteps) {
                wageRedisUtil.initStepInfo(step, agentList.size());
            }
            // 启动第一个步长的计算
            int firstStep = allSteps.get(0);
            startStepCalculation(firstStep, wageRedisUtil, wageCalDelayedDto, agentList, batch);
            // 启动定时检查任务
            scheduleStepCompletionCheck(wageCalDelayedDto, commissionInitHandler, wageRedisUtil, agentList, allSteps, batch);
            return true;
        } catch (ServiceException se) {
            log.error("佣金计算业务异常: {}", se.getMessage());
            if (batch != null) {
                updateBatchStatus(batch, CalBatchStatus.FAILED, se.getMessage());
            }
            throw new ServiceException(se.getMessage());
        } catch (Exception e) {
            log.error("佣金计算系统异常: {}", e.getMessage(), e);
            if (batch != null) {
                updateBatchStatus(batch, CalBatchStatus.FAILED, e.getMessage());
            }
            throw new ServiceException("佣金计算系统异常: " + e.getMessage());
        }
    }

    /**
     * 验证佣金计算参数
     */
    private void validateParameters(CommissionCalMainDto wageCalDelayedDto, ICommissionBaseHandler commissionInitHandler) {
        if (wageCalDelayedDto == null) {
            throw new ServiceException("佣金计算参数不能为空!");
        }
        if (StringUtils.isBlank(wageCalDelayedDto.getManageCom())) {
            throw new ServiceException("管理机构代码不能为空!");
        }
        if (StringUtils.isBlank(wageCalDelayedDto.getIndexCalNo())) {
            throw new ServiceException("佣金年月不能为空!");
        }
        if (wageCalDelayedDto.getBranchType() == null) {
            throw new ServiceException("渠道标志不能为空!");
        }
        if (wageCalDelayedDto.getIndexCalType() == null) {
            throw new ServiceException("计算类型不能为空!");
        }
        if (wageCalDelayedDto.getWageType() == null) {
            throw new ServiceException("佣金类型不能为空!");
        }
        if (wageCalDelayedDto.getBaseLawVersion() == null) {
            throw new ServiceException("基本法版本不能为空!");
        }
        if (commissionInitHandler == null) {
            throw new ServiceException("佣金计算处理不能为空!");
        }
    }

    /**
     * 创建计算批次记录
     */
    private WageCalculationBatch createBatchRecord(CommissionCalMainDto wageCalDelayedDto, int agentCount) {
        WageCalculationBatch batch = new WageCalculationBatch();
        batch.setBatchId(wageCalDelayedDto.getMd5Val());
        batch.setRunId(wageCalDelayedDto.getUuid());
        batch.setIsCurrent(1);
        batch.setManageCom(wageCalDelayedDto.getManageCom());
        batch.setIndexCalNo(wageCalDelayedDto.getIndexCalNo());
        batch.setBranchType(wageCalDelayedDto.getBranchType().getCode());
        batch.setWageType(wageCalDelayedDto.getWageType().getCode());
        batch.setCurrentStep(1);
        batch.setTotalSteps(0);
        batch.setTotalAgents(agentCount);
        batch.setPendingCount(agentCount);
        batch.setSuccessCount(0);
        batch.setFailCount(0);
        batch.setStatus(CalBatchStatus.INIT.getCode());
        batch.setStartTime(new Date());

        boolean success = wageCalculationService.createCalculationBatch(batch);
        if (!success) {
            throw new ServiceException("创建佣金计算批次记录失败!");
        }

        log.info("成功创建佣金计算批次记录: runId={}, batchId={}", batch.getRunId(), batch.getBatchId());
        return batch;
    }


    /**
     * 更新批次状态和备注信息
     */
    private void updateBatchStatus(WageCalculationBatch batch, CalBatchStatus status, String message) {
        try {
            batch.setStatus(status.getCode());

            if (status == CalBatchStatus.COMPLETED || status == CalBatchStatus.FAILED) {
                batch.setFinishTime(new Date());
            }

            if (StringUtils.isNotBlank(message)) {
                batch.setRemark(message.length() > 500 ? message.substring(0, 500) : message);
            }

            wageCalculationService.updateCalculationBatch(batch);
            log.info("已更新批次状态为{}: runId={}", status.getDescription(), batch.getRunId());
        } catch (Exception e) {
            log.error("更新批次状态为{}时发生异常: {}", status.getDescription(), e.getMessage(), e);
        }
    }

    /**
     * 获取所有计算步长并排序
     */
    private List<Integer> getAllSteps(WageRedisUtil wageRedisUtil) {
        try {
            List<WageCalculationDefinition> definitions = wageRedisUtil.getDefinitionList();
            if (CollUtil.isEmpty(definitions)) {
                log.warn("未找到任何佣金计算定义项");
                return new ArrayList<>();
            }

            List<Integer> steps = definitions.stream()
                .map(WageCalculationDefinition::getWagecalStep)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

            log.debug("提取到{}个不同的计算步长: {}", steps.size(), steps);
            return steps;
        } catch (Exception e) {
            log.error("获取计算步长异常: {}", e.getMessage(), e);
            throw new ServiceException("无法获取计算步长: " + e.getMessage());
        }
    }

    /**
     * 启动指定步长的计算
     */
    private void startStepCalculation(int step, WageRedisUtil wageRedisUtil,
                                      CommissionCalMainDto wageCalDelayedDto,
                                      List<WageCalAgentDto> agents,
                                      WageCalculationBatch batch) {
        // 设置当前步长
        wageRedisUtil.setCurrentStep(step);
        // 更新批次的当前步长
        batch.setCurrentStep(step);
        wageCalculationService.updateCalculationBatch(batch);
        log.info("开始执行步长{}的佣金计算", step);

        // 初始化步长的Redis统计
        wageRedisUtil.setCalParmsVal("STEP_STATS:" + batch.getRunId() + ":" + step + ":PENDING_COUNT",
            String.valueOf(agents.size()));

        // 发送计算消息
        int taskCount = 0;
        for (WageCalAgentDto agent : agents) {
            try {
                agent.setCurrentStep(step);
                agent.setCalBatch(batch.getBatchId());
                agent.setRunId(batch.getRunId());
                wageRabbitMQUtil.sendWageCal(RabbitQueue.WAGE_CAL_QUEUE, agent);
                wageRedisUtil.setAgentStepCalResultFlag(agent.getAgentCode(), step, "0");
                taskCount++;
            } catch (Exception e) {
                log.error("发送代理人计算消息失败 - 代理人: {}, 步长: {}, 错误: {}",
                    agent.getAgentCode(), step, e.getMessage());
            }
        }
        // 设置步长任务计数
        wageRedisUtil.setStepTotalCount(step, taskCount);
        wageRedisUtil.resetStepCalResultCount(step);
        log.info("步长{}的计算消息已全部发送，共{}个任务", step, taskCount);
    }

    /**
     * 启动步长完成监控任务
     */
    private ScheduledFuture<?> scheduleStepCompletionCheck(CommissionCalMainDto wageCalDelayedDto,
                                                           ICommissionBaseHandler commissionInitHandler,
                                                           WageRedisUtil wageRedisUtil,
                                                           List<WageCalAgentDto> agents,
                                                           List<Integer> allSteps,
                                                           WageCalculationBatch batch) {
        final AtomicInteger checkCount = new AtomicInteger(0);
        final AtomicReference<ScheduledFuture<?>> futureRef = new AtomicReference<>();

        ScheduledFuture<?> future = scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                int currentCount = checkCount.incrementAndGet();
                int currentStep = wageRedisUtil.getCurrentStep();
                // 从Redis获取步长进度
                long completedCount = wageRedisUtil.getStepCalResultCount(currentStep);
                int totalCount = wageRedisUtil.getStepTotalCount(currentStep);
                // 定期输出进度日志
                if (completedCount > 0 && (currentCount % 3 == 0 || completedCount == totalCount)) {
                    int progressPercentage = totalCount > 0 ? (int) (completedCount * 100 / totalCount) : 0;
                    log.info("步长{}佣金计算进度: {}/{} ({}%) - 批次: {}, 检查次数: {}",
                        currentStep, completedCount, totalCount, progressPercentage, batch.getBatchId(), currentCount);
                }
                // 判断当前步长是否全部完成
                if (completedCount >= totalCount && totalCount > 0) {
                    log.info("步长{}佣金计算已完成", currentStep);

                    // 更新步长计算结果到数据库
                    updateStepResultsToDatabase(batch.getRunId(), currentStep, wageRedisUtil);

                    // 更新Redis中步长状态
                    wageRedisUtil.setStepStatus(currentStep, "COMPLETED");
                    // 获取当前步长索引
                    int currentStepIndex = allSteps.indexOf(currentStep);
                    // 判断是否还有下一个步长
                    if (currentStepIndex < allSteps.size() - 1) {
                        // 启动下一步长计算
                        int nextStep = allSteps.get(currentStepIndex + 1);
                        startStepCalculation(nextStep, wageRedisUtil, wageCalDelayedDto, agents, batch);
                    } else {
                        // 所有步长已完成，执行收尾工作
                        log.info("所有步长佣金计算已完成，执行收尾工作");
                        try {
                            // 更新最终的批次统计结果到数据库
                            updateFinalBatchResults(batch, wageRedisUtil);

                            // 执行业务收尾处理
                            commissionInitHandler.calEnd(wageCalDelayedDto);
                            // 更新批次状态为已完成
                            updateBatchStatus(batch, CalBatchStatus.COMPLETED,null);
                            // 清理计算结果缓存
                            if (wageRedisUtil.clearResultCache()) {
                                log.info("已清理佣金计算结果缓存");
                            }
                            log.info("佣金计算收尾工作完成");
                        } catch (Exception e) {
                            log.error("佣金计算收尾工作异常: {}", e.getMessage(), e);
                            updateBatchStatus(batch, CalBatchStatus.FAILED, "佣金计算收尾工作异常: " + e.getMessage());
                        }
                        // 取消定时任务
                        ScheduledFuture<?> self = futureRef.get();
                        safelyCancelScheduledTask(self);
                    }
                }
                // 超过最大检查次数则强制结束
                if (currentCount > MAX_CHECK_COUNT) {
                    log.warn("佣金计算检查次数超限({}次)，强制结束检查", currentCount);
                    updateBatchStatus(batch, CalBatchStatus.FAILED, "佣金计算检查次数超限(" + currentCount + "次)，强制结束");
                    ScheduledFuture<?> self = futureRef.get();
                    safelyCancelScheduledTask(self);
                }
            } catch (Exception e) {
                log.error("检查佣金计算完成状态异常: {}", e.getMessage(), e);
                // 异常情况下取消定时任务
                ScheduledFuture<?> self = futureRef.get();
                safelyCancelScheduledTask(self);
            }
        }, 10, 10, TimeUnit.SECONDS);

        futureRef.set(future);
        return future;
    }

    /**
     * 安全取消定时任务，避免CancellationException
     */
    private void safelyCancelScheduledTask(ScheduledFuture<?> future) {
        if (future != null && !future.isDone() && !future.isCancelled()) {
            // 使用mayInterruptIfRunning=false来优雅地取消任务
            future.cancel(false);
            log.debug("已安全取消定时任务");
        }
    }


    /**
     * 更新步长计算结果到数据库
     * 在每个步长完成时从Redis获取统计数据并更新到数据库
     */
    private void updateStepResultsToDatabase(String runId, int step, WageRedisUtil wageRedisUtil) {
        try {
            // 从Redis获取成功和失败的任务数
            int successCount = wageRedisUtil.getBatchSuccessCount(runId);
            int failCount = wageRedisUtil.getBatchFailCount(runId);
            int pendingCount = wageRedisUtil.getBatchPendingCount(runId);

            // 计算待处理任务数
            int totalPendingCount = Math.max(0, wageRedisUtil.getStepTotalCount(step) - successCount - failCount);

            // 直接调用批量更新方法
            boolean success = wageCalculationService.batchUpdateCount(runId, successCount, failCount, pendingCount);

            if (success) {
                log.info("已更新步长{}完成情况到数据库: 成功={}, 失败={}, 待处理={}",
                    step, successCount, failCount, pendingCount);
            } else {
                log.error("更新步长{}结果失败: 无法更新批次信息, runId={}", step, runId);
            }
        } catch (Exception e) {
            log.error("更新步长{}结果到数据库异常: {}", step, e.getMessage(), e);
        }
    }

    /**
     * 更新最终批次结果到数据库
     */
    private void updateFinalBatchResults(WageCalculationBatch batch, WageRedisUtil wageRedisUtil) {
        try {
            // 从Redis获取最终统计结果
            int successCount = wageRedisUtil.getBatchSuccessCount(batch.getRunId());
            int failCount = wageRedisUtil.getBatchFailCount(batch.getRunId());

            // 直接调用批量更新方法，所有任务已完成，待处理任务为0
            boolean success = wageCalculationService.batchUpdateCount(batch.getRunId(), successCount, failCount, 0);

            if (success) {
                log.info("已更新最终批次结果到数据库: 成功={}, 失败={}", successCount, failCount);
            } else {
                log.error("更新最终批次结果失败, runId={}", batch.getRunId());
            }
        } catch (Exception e) {
            log.error("更新最终批次结果到数据库异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 初始化佣金计算规则
     */
    protected boolean initCalculationRules(CommissionCalMainDto wageCalDelayedDto,
                                           Map<String, Laagent> agents,
                                           Map<String, Latree> trees,
                                           WageRedisUtil wageRedisUtil) {
        try {
            // 清空缓存并初始化计算参数
            if (commissionService != null) {
                commissionService.clearAndinitCalRunParms(wageCalDelayedDto);
            } else {
                log.warn("佣金服务为空，无法清空缓存和初始化计算参数");
            }
            // 初始化计算人员信息到Redis
            if (agents != null && trees != null) {
                wageRedisUtil.initWageCalAgents(agents, trees);
            } else {
                log.warn("代理人信息或职级信息为空，无法初始化计算人员信息");
                return false;
            }

            // 初始化全局状态计数
            wageRedisUtil.setCalParmsMapVal("BATCH_STATS:" + wageCalDelayedDto.getUuid(), "SUCCESS_COUNT", "0");
            wageRedisUtil.setCalParmsMapVal("BATCH_STATS:" + wageCalDelayedDto.getUuid(), "FAILED_COUNT", "0");
            wageRedisUtil.setCalParmsMapVal("BATCH_STATS:" + wageCalDelayedDto.getUuid(), "TOTAL_COUNT", String.valueOf(agents.size()));

            return true;
        } catch (Exception e) {
            log.error("初始化佣金计算规则异常: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除佣金计算数据
     * 注意：此方法需要在子类中实现具体的删除逻辑
     */
    public boolean deleteCalculationData(CommissionCalMainDto wageCalDelayedDto, ICommissionBaseHandler commissionInitHandler) {
        log.warn("deleteCalculationData方法未被覆盖实现，无法删除计算数据! 管理机构: {}, 计算期间: {}",
            wageCalDelayedDto.getManageCom(), wageCalDelayedDto.getIndexCalNo());
        return false;
    }
}
