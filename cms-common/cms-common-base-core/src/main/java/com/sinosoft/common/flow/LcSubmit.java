package com.sinosoft.common.flow;

import cn.hutool.core.collection.CollUtil;

import com.sinosoft.common.domain.*;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.event.LcProcTrackEvent;
import com.sinosoft.common.exception.FlowException;
import com.sinosoft.system.api.model.LoginUser;
import com.sinosoft.common.core.utils.SpringUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.enums.LcCzTypeEnum;
import com.sinosoft.common.enums.LcTypeSerNoEnum;
import com.sinosoft.common.service.ILcMainService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程保存
 *
 * @author: zzf
 * @create: 2023-07-02 08:40
 */
@Slf4j
@Component
public class LcSubmit {
    /**
     * 流程主表服务
     */
    @Autowired
    private ILcMainService lcMainService;

    @Autowired
    private FlowHandlerFactory handlerFactory;

    /**
     * 流程提交
     *
     * @param lcTypeSer 流程类型
     * @param curLc     当前流程
     * @param dataIds   业务数据集合
     * @param yj        意见
     * @param context   流程上下文
     * @throws FlowException 流程异常
     */
    public void submit_lc(LcTypeSerNoEnum lcTypeSer, int curLc, List<String> dataIds, String yj,
                          LcContext context) throws FlowException {
        try {
            MDC.put("flowType", lcTypeSer.name());
            MDC.put("flowNode", String.valueOf(curLc));
            MDC.put("operator", context.getOperator());
            MDC.put("actionType", "SUBMIT");

            processFlow(lcTypeSer, curLc, LcNodeConstants.DEFAULT_NODE, LcCzTypeEnum.ADOPT, dataIds, yj, context);
        } finally {
            MDC.clear();
        }
    }

    /**
     * 流程退回
     *
     * @param lcTypeSer 流程类型
     * @param curLc     当前流程
     * @param dataIds   业务数据集合
     * @param yj        意见
     * @param context   流程上下文
     * @throws FlowException 流程异常
     */
    public void return_lc(LcTypeSerNoEnum lcTypeSer, int curLc, List<String> dataIds, String yj,
                          LcContext context) throws FlowException {
        try {
            MDC.put("flowType", lcTypeSer.name());
            MDC.put("flowNode", String.valueOf(curLc));
            MDC.put("operator", context.getOperator());
            MDC.put("actionType", "RETURN");

            processFlow(lcTypeSer, curLc, LcNodeConstants.DEFAULT_NODE, LcCzTypeEnum.REJECT, dataIds, yj, context);
        } finally {
            MDC.clear();
        }
    }

    /**
     * 数据保存
     *
     * @param nLcTypeSer 流程类型
     * @param dataIds    业务数据集合
     * @param context    流程上下文
     * @throws FlowException 流程异常
     */
    public void save_lc(LcTypeSerNoEnum nLcTypeSer, List<String> dataIds, LcContext context) throws FlowException {
        try {
            MDC.put("flowType", nLcTypeSer.name());
            MDC.put("operator", context.getOperator());
            MDC.put("actionType", "SAVE");

            processFlow(nLcTypeSer, LcNodeConstants.DEFAULT_NODE, LcNodeConstants.DEFAULT_NODE,
                LcCzTypeEnum.SAVE, dataIds, "", context);
        } finally {
            MDC.clear();
        }
    }

    /**
     * 提交或退回到指定节点
     *
     * @param lcTypeSer 流程类型主键
     * @param czType    操作方式
     * @param curLc     当前流程节点
     * @param toLcId    指定流程节点
     * @param dataIds   业务数据
     * @param yj        意见
     * @param context   流程上下文
     * @return true成功 false失败
     * @throws FlowException 流程异常
     */
    public boolean lc_submitAssign(LcTypeSerNoEnum lcTypeSer, LcCzTypeEnum czType, int curLc, int toLcId,
                                   List<String> dataIds, String yj, LcContext context) throws FlowException {
        try {
            MDC.put("flowType", lcTypeSer.name());
            MDC.put("flowNode", String.valueOf(curLc));
            MDC.put("targetNode", String.valueOf(toLcId));
            MDC.put("operator", context.getOperator());
            MDC.put("actionType", czType.getName());

            return processFlow(lcTypeSer, curLc, toLcId, czType, dataIds, yj, context);
        } finally {
            MDC.clear();
        }
    }

    /**
     * 流程操作方法
     *
     * @param lcTypeSer  流程类型
     * @param currLcNode 当前流程
     * @param assignLcId 指定流程ID
     * @param czType     方式
     * @param dataIds    业务数据集合
     * @param opinion    意见
     * @param context    流程上下文
     * @return true成功 false失败
     * @throws FlowException 流程异常
     */
    private boolean processFlow(LcTypeSerNoEnum lcTypeSer, int currLcNode, int assignLcId,
                                LcCzTypeEnum czType, List<String> dataIds, String opinion,
                                LcContext context) throws FlowException {
        // 如果没有数据，直接返回成功
        if (CollUtil.isEmpty(dataIds)) {
            log.info("无流程数据，直接返回");
            return true;
        }

        log.info("流程提交信息: 操作人[{}]; 操作类型[{}]; 当前流程[{}]; 数据条数[{}]",
            context.getOperator(), czType, currLcNode, dataIds.size());

        // 获取流程类型
        LcMain lcMain = getLcMainOrThrow(lcTypeSer);

        // 处理保存操作下的流程节点
        if (czType == LcCzTypeEnum.SAVE) {
            currLcNode = lcMainService.getFirstLcByType(lcTypeSer.getCode());
        }

        // 获取当前流程节点定义
        LcDefine currentLcDefine = getLcDefineOrThrow(currLcNode);

        // 是否为退回操作
        boolean isReturnOperation = czType == LcCzTypeEnum.REJECT;

        // 操作说明
        String operationMsg = StringUtils.isNotBlank(opinion) ? opinion : czType.getName();

        // 非保存操作需校验流程节点
        if (czType != LcCzTypeEnum.SAVE) {
            validateLcNode(lcMain, currentLcDefine, dataIds, czType.getName());
        }

        // 执行前置操作
        int preActionType = isReturnOperation ? LcActionType.BEFORE_RETURN : LcActionType.BEFORE_SUBMIT;
        executeNodeAction(currentLcDefine.getId(), lcMain, dataIds, preActionType, context);

        // 处理流程保存操作
        if (czType == LcCzTypeEnum.SAVE) {
            // 记录流程轨迹并直接返回
            createLcTraceAsync(lcTypeSer, lcMain, currentLcDefine, currentLcDefine.getId(),
                dataIds, czType, operationMsg, context.getOperator());
            return true;
        }

        // 处理流程跳转并过滤已处理的数据
        List<String> remainingDataIds = handleFlowTransition(
            lcTypeSer, lcMain, currentLcDefine, new ArrayList<>(dataIds),
            czType, operationMsg, context);

        // 如果流程跳转后没有剩余数据，直接返回成功
        if (CollUtil.isEmpty(remainingDataIds)) {
            log.info("所有数据已通过流程跳转处理，无需继续");
            return true;
        }

        // 获取下一个流程节点
        int nextLcId = determineNextNode(assignLcId, isReturnOperation, lcMain, currentLcDefine);

        // 记录流程轨迹
        createLcTraceAsync(lcTypeSer, lcMain, currentLcDefine, nextLcId,
            remainingDataIds, czType, operationMsg, context.getOperator());

        // 执行后置操作
        int postActionType = isReturnOperation ? LcActionType.AFTER_RETURN : LcActionType.AFTER_SUBMIT;
        executeNodeAction(nextLcId, lcMain, remainingDataIds, postActionType, context);

        return true;
    }

    /**
     * 获取流程类型定义或抛出异常
     *
     * @param lcTypeSer 流程类型枚举
     * @return 流程类型定义
     * @throws FlowException 流程类型不存在时抛出异常
     */
    private LcMain getLcMainOrThrow(LcTypeSerNoEnum lcTypeSer) throws FlowException {
        LcMain lcMain = lcMainService.selectLcMainById((long) lcTypeSer.getCode());
        if (lcMain == null) {
            String errorMsg = StringUtils.format("流程类型[{}]不存在", lcTypeSer.getCode());
            log.error(errorMsg);
            throw new FlowException(errorMsg);
        }
        return lcMain;
    }

    /**
     * 获取流程节点定义或抛出异常
     *
     * @param lcNodeId 流程节点ID
     * @return 流程节点定义
     * @throws FlowException 流程节点不存在时抛出异常
     */
    private LcDefine getLcDefineOrThrow(int lcNodeId) throws FlowException {
        if (lcNodeId == LcNodeConstants.DEFAULT_NODE) {
            throw new FlowException("流程节点ID无效");
        }

        LcDefine lcDefine = lcMainService.selectLcDefineById((long) lcNodeId);
        if (lcDefine == null) {
            String errorMsg = StringUtils.format("流程节点[{}]不存在", lcNodeId);
            log.error(errorMsg);
            throw new FlowException(errorMsg);
        }
        return lcDefine;
    }

    /**
     * 确定下一个流程节点
     *
     * @param assignLcId        指定的流程节点ID
     * @param isReturnOperation 是否为退回操作
     * @param lcMain            流程类型定义
     * @param currentLcDefine   当前流程节点定义
     * @return 下一个流程节点ID
     * @throws FlowException 获取节点失败时抛出异常
     */
    private int determineNextNode(int assignLcId, boolean isReturnOperation,
                                  LcMain lcMain, LcDefine currentLcDefine) throws FlowException {
        // 如果指定了节点ID，验证并返回
        if (assignLcId != LcNodeConstants.DEFAULT_NODE) {
            getLcDefineOrThrow(assignLcId); // 验证节点存在
            return assignLcId;
        }

        // 否则获取上一个或下一个节点
        int nextLcId = lcMainService.getLcPreviousOrTheNext(isReturnOperation, lcMain, currentLcDefine);
        if (nextLcId == LcNodeConstants.ERROR_NODE) {
            throw new FlowException(
                StringUtils.format("获取{}流程节点出错!",
                    isReturnOperation ? "上一个" : "下一个"));
        }

        return nextLcId;
    }

    /**
     * 创建流程轨迹
     *
     * @param lcTypeSer 流程枚举
     * @param lcMain    流程类型
     * @param lcDefine  流程明细
     * @param nextLcId  下个流程节点
     * @param ids       数据ID集合
     * @param czType    操作类型
     * @param message   操作说明
     * @param operator  操作人
     * @throws FlowException 流程异常
     */
    private void createLcTraceAsync(LcTypeSerNoEnum lcTypeSer, LcMain lcMain, LcDefine lcDefine,
                               int nextLcId, List<String> ids, LcCzTypeEnum czType,
                               String message, String operator) throws FlowException {
        try {
            // 退回操作需要更新轨迹标志
            if (czType == LcCzTypeEnum.REJECT) {
                lcMainService.updateLcProcess(lcDefine.getId(), lcTypeSer, nextLcId, false, ids);
            }

            // 更新数据流程状态
            lcMainService.updateDataLc(nextLcId, lcDefine.getId(), lcMain, czType, ids, message);

            // 创建轨迹事件
            LcProcTrackEvent trackEvent = createTrackEvent(lcMain, lcDefine, czType, message, ids, operator);
            // 发布事件，由异步监听器处理
            SpringUtils.context().publishEvent(trackEvent);

            log.debug("发布流程轨迹事件: 当前节点[{}], 下一节点[{}], 数据数量[{}]",
                lcDefine.getId(), nextLcId, ids.size());
        } catch (Exception e) {
            log.error("创建流程轨迹失败", e);
            throw new FlowException("创建流程轨迹失败: " + e.getMessage());
        }
    }

    /**
     * 创建流程轨迹事件对象
     */
    private LcProcTrackEvent createTrackEvent(LcMain lcMain, LcDefine lcDefine,
                                              LcCzTypeEnum czType, String message,
                                              List<String> ids, String operator) {
        LcProcTrackEvent trackEvent = new LcProcTrackEvent();
        trackEvent.setLcSerialNo(lcMain.getSerialno());
        trackEvent.setLcId(lcDefine.getId());
        trackEvent.setCzType(czType.getCode());
        trackEvent.setSm(message);
        trackEvent.setIdsList(ids);
        trackEvent.setOperator(operator);
        return trackEvent;
    }

    /**
     * 处理流程跳转
     *
     * @param lcTypeSer 流程枚举
     * @param lcMain    流程类型
     * @param lcDefine  流程明细
     * @param dataIds   数据条件
     * @param czType    操作类型
     * @param message   操作说明
     * @param context   流程上下文
     * @return 未处理的数据ID列表
     * @throws FlowException 流程异常
     */
    private List<String> handleFlowTransition(LcTypeSerNoEnum lcTypeSer, LcMain lcMain,
                                              LcDefine lcDefine, List<String> dataIds,
                                              LcCzTypeEnum czType, String message,
                                              LcContext context) throws FlowException {
        try {
            boolean isReturnOperation = czType == LcCzTypeEnum.REJECT;

            // 获取流程跳转规则
            List<LcTz> tzRules = lcMainService.getLcTzListByLcId(lcDefine.getId(), isReturnOperation);
            if (CollUtil.isEmpty(tzRules)) {
                return dataIds; // 无跳转规则，返回原始数据
            }

            // 记录已处理的数据ID
            Set<String> processedIds = new HashSet<>();

            // 处理每条跳转规则
            for (LcTz tzRule : tzRules) {
                String condition = tzRule.getTzTj();
                int targetNodeId = isReturnOperation ? tzRule.getLcId() : tzRule.getLcNextId();

                // 如果无条件，跳过
                if (StringUtils.isBlank(condition)) {
                    continue;
                }

                // 查询符合条件的数据
                List<String> matchedIds = findMatchingDataIds(
                    lcDefine.getId(), lcMain, condition, dataIds);

                // 如果有匹配数据，进行处理
                if (CollUtil.isNotEmpty(matchedIds)) {
                    processedIds.addAll(matchedIds);

                    // 记录流程轨迹
                    createLcTraceAsync(lcTypeSer, lcMain, lcDefine, targetNodeId,
                        matchedIds, czType, message, context.getOperator());

                    // 执行节点操作
                    int actionType = isReturnOperation ? LcActionType.AFTER_RETURN : LcActionType.AFTER_SUBMIT;
                    executeNodeAction(targetNodeId, lcMain, matchedIds, actionType, context);

                    log.info("流程跳转处理: 条件[{}], 目标节点[{}], 匹配数据数量[{}]",
                        StringUtils.substring(condition, 0, 50), targetNodeId, matchedIds.size());
                }
            }

            // 返回未处理的数据ID
            if (processedIds.isEmpty()) {
                return dataIds;
            } else {
                return dataIds.stream()
                    .filter(id -> !processedIds.contains(id))
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("流程跳转处理失败", e);
            throw new FlowException("流程跳转处理失败: " + e.getMessage());
        }
    }

    /**
     * 查找匹配条件的数据ID
     */
    private List<String> findMatchingDataIds(int lcNodeId, LcMain lcMain,
                                             String condition, List<String> dataIds) {
        String idsStr = CollUtil.join(dataIds, ",", "'", "'");
        return lcMainService.getDataIdByLcIdAndTj(
            lcNodeId, lcMain.getLcTable(), lcMain.getIdField(),
            lcMain.getLcField(), condition, idsStr);
    }

    /**
     * 执行节点操作（检查和动作）
     *
     * @param nodeId     节点ID
     * @param lcMain     流程类型
     * @param dataIds    数据ID列表
     * @param actionType 操作类型
     * @param context    流程上下文
     * @throws FlowException 流程异常
     */
    private void executeNodeAction(Integer nodeId, LcMain lcMain, List<String> dataIds,
                                   int actionType, LcContext context) throws FlowException {
        // 执行检查
        List<LcCheck> checkList = lcMainService.getLcCheckListByLcId(nodeId, actionType);
        executeChecks(checkList, lcMain, nodeId, dataIds, context);

        // 执行动作
        List<LcDz> actionList = lcMainService.getLcDzListByLcId(nodeId, actionType);
        executeActions(actionList, lcMain, nodeId, dataIds, context);
    }

    /**
     * 执行检查
     *
     * @param checkList 检查列表
     * @param lcMain    流程类型
     * @param nodeId    节点ID
     * @param dataIds   数据ID列表
     * @param context   流程上下文
     * @throws FlowException 检查失败时抛出异常
     */
    private void executeChecks(List<LcCheck> checkList, LcMain lcMain, Integer nodeId,
                               List<String> dataIds, LcContext context) throws FlowException {
        if (CollUtil.isEmpty(checkList)) {
            return;
        }

        for (LcCheck check : checkList) {
            log.debug("执行检查: [{}], 类型[{}]", check.getName(), check.getCheckType());

            switch (check.getCheckType()) {
                case 0: // SQL检查
                    executeSqlCheck(check.getName(), lcMain.getLcTable(), lcMain.getIdField(),
                        check.getCheckTj(), dataIds, check.getCheckMsg());
                    break;

                case 3: // 类检查
                    executeClassCheck(check.getName(), check.getCheckTj(), lcMain,
                        nodeId, dataIds, check.getCheckMsg(), context);
                    break;

                default:
                    log.warn("未知检查类型: {}", check.getCheckType());
                    break;
            }
        }
    }

    /**
     * 执行动作
     *
     * @param actionList 动作列表
     * @param lcMain     流程类型
     * @param nodeId     节点ID
     * @param dataIds    数据ID列表
     * @param context    流程上下文
     * @throws FlowException 动作执行失败时抛出异常
     */
    private void executeActions(List<LcDz> actionList, LcMain lcMain, Integer nodeId,
                                List<String> dataIds, LcContext context) throws FlowException {
        if (CollUtil.isEmpty(actionList)) {
            return;
        }

        for (LcDz action : actionList) {
            log.debug("执行动作: [{}], 类型[{}]", action.getName(), action.getDzType());

            switch (action.getDzType()) {
                case 0: // SQL动作
                    executeSqlCheck(action.getName(), lcMain.getLcTable(), lcMain.getIdField(),
                        action.getDz(), dataIds, "");
                    break;

                case 3: // 类动作
                    executeClassAction(action.getName(), action.getDz(), lcMain,
                        nodeId, dataIds, context);
                    break;

                default:
                    log.warn("未知动作类型: {}", action.getDzType());
                    break;
            }
        }
    }

    /**
     * 执行SQL检查
     *
     * @param name      执行名称
     * @param tableName 表名
     * @param idField   主键字段
     * @param sql       SQL语句
     * @param dataIds   数据ID列表
     * @param errorMsg  错误信息
     * @throws FlowException SQL执行失败或检查不通过时抛出异常
     */
    private void executeSqlCheck(String name, String tableName, String idField,
                                 String sql, List<String> dataIds, String errorMsg) throws FlowException {
        if (StringUtils.isBlank(sql)) {
            return;
        }

        // 替换SQL中的数据条件
        String processedSql = processSqlWithDataIds(sql, dataIds);

        // 执行SQL
        try {
            if (isSqlQuery(processedSql)) {
                List<String> results = lcMainService.execSqlSelect(processedSql);

                // 如果有结果，说明检查不通过
                if (CollUtil.isNotEmpty(results)) {
                    throwValidationException(name, results, errorMsg);
                }
            } else {
                // 执行更新操作
                lcMainService.execSqlUpdate(processedSql);
            }
        } catch (Exception e) {
            if (e instanceof FlowException) {
                throw e;
            }
            log.error("SQL执行失败: {}", processedSql, e);
            throw new FlowException(StringUtils.format("执行[{}]失败: {}", name, e.getMessage()));
        }
    }

    /**
     * 处理SQL中的数据ID条件
     */
    private String processSqlWithDataIds(String sql, List<String> dataIds) {
        if (sql.contains("?DATAWHERE?")) {
            return sql.replaceAll("\\?DATAWHERE\\?", CollUtil.join(dataIds, ",", "'", "'"));
        }
        return sql;
    }

    /**
     * 判断是否为查询SQL
     */
    private boolean isSqlQuery(String sql) {
        return sql.trim().toLowerCase().startsWith("select");
    }

    /**
     * 抛出验证异常
     */
    private void throwValidationException(String name, List<String> results, String errorMsg) throws FlowException {
        if (StringUtils.isNotBlank(errorMsg)) {
            String valMsg = formatValidationMessage(results);
            throw new FlowException(String.format("[%s]%s", valMsg, errorMsg));
        } else {
            throw new FlowException(StringUtils.format("动作[{}]校验未通过,请检查!", name));
        }
    }

    /**
     * 格式化验证消息
     */
    private String formatValidationMessage(List<String> results) {
        String valMsg = CollUtil.join(results, ",", "", "");
        if (valMsg.length() > 20) {
            String[] parts = valMsg.split(",", 4);
            if (parts.length > 3) {
                return String.join(",", Arrays.stream(parts).limit(3).toArray(String[]::new)) + "...等数据";
            }
        }
        return valMsg;
    }

    /**
     * 执行类检查
     *
     * @param name         执行名称
     * @param handlerClass 处理类名
     * @param lcMain       流程类型
     * @param nodeId       节点ID
     * @param dataIds      数据ID列表
     * @param errorMsg     错误信息
     * @param context      流程上下文
     * @throws FlowException 检查失败时抛出异常
     */
    private void executeClassCheck(String name, String handlerClass, LcMain lcMain,
                                   Integer nodeId, List<String> dataIds, String errorMsg,
                                   LcContext context) throws FlowException {
        if (StringUtils.isBlank(handlerClass)) {
            throw new FlowException(StringUtils.format("检查[{}]校验,处理类为空,请联系管理员!", name));
        }

        try {
            ILcCheckHandler checkHandler = handlerFactory.getCheckHandler(handlerClass);
            checkHandler.checkHandler(nodeId, lcMain, dataIds, context.getLoginUser(), context.getParameters());
        } catch (Exception e) {
            log.error("执行类检查[{}]失败", name, e);
            if (e instanceof FlowException) {
                throw e;
            }
            throw new FlowException(StringUtils.format("检查[{}]执行失败: {}", name, e.getMessage()));
        }
    }

    /**
     * 执行类动作
     *
     * @param name         执行名称
     * @param handlerClass 处理类名
     * @param lcMain       流程类型
     * @param nodeId       节点ID
     * @param dataIds      数据ID列表
     * @param context      流程上下文
     * @throws FlowException 动作执行失败时抛出异常
     */
    private void executeClassAction(String name, String handlerClass, LcMain lcMain,
                                    Integer nodeId, List<String> dataIds,
                                    LcContext context) throws FlowException {
        if (StringUtils.isBlank(handlerClass)) {
            throw new FlowException(StringUtils.format("动作[{}]校验,处理类为空,请联系管理员!", name));
        }

        try {
            ILcDzHandler actionHandler = handlerFactory.getActionHandler(handlerClass);
            if (!actionHandler.handler(nodeId, lcMain, dataIds, context.getLoginUser(), context.getParameters())) {
                throw new FlowException(StringUtils.format("动作[{}]执行失败!", name));
            }
        } catch (Exception e) {
            log.error("执行类动作[{}]失败", name, e);
            if (e instanceof FlowException) {
                throw e;
            }
            throw new FlowException(StringUtils.format("动作[{}]执行失败: {}", name, e.getMessage()));
        }
    }

    /**
     * 校验流程节点一致性
     *
     * @param lcMain   流程主表
     * @param lcDefine 当前流程定义
     * @param dataIds  业务数据集合
     * @param msg      流程操作方式说明
     * @throws FlowException 校验失败时抛出异常
     */
    private void validateLcNode(LcMain lcMain, LcDefine lcDefine, List<String> dataIds, String msg) throws FlowException {
        validateLcNode(lcMain, lcDefine, dataIds, msg, false);
    }

    /**
     * 校验流程节点一致性
     *
     * @param lcMain   流程主表
     * @param lcDefine 当前流程定义
     * @param dataIds  业务数据集合
     * @param msg      流程操作方式说明
     * @param haveTz   是否有流程跳转
     * @throws FlowException 校验失败时抛出异常
     */
    private void validateLcNode(LcMain lcMain, LcDefine lcDefine, List<String> dataIds,
                                String msg, boolean haveTz) throws FlowException {
        try {
            // 构建查询SQL
            String subIdsStr = CollUtil.join(dataIds, ",", "'", "'");
            String querySql = StringUtils.format(
                "select {} from {} where {} in ({}) group by {}",
                lcMain.getLcField(), lcMain.getLcTable(), lcMain.getIdField(),
                subIdsStr, lcMain.getLcField()
            );

            // 获取数据当前流程节点
            List<String> dataLcIds = lcMainService.getDataCurLcId(querySql);

            // 校验流程节点一致性
            validateNodeConsistency(dataLcIds, lcDefine, msg, haveTz);
        } catch (Exception e) {
            if (e instanceof FlowException) {
                throw e;
            }
            log.error("校验流程节点失败", e);
            throw new FlowException("校验流程节点失败: " + e.getMessage());
        }
    }

    /**
     * 校验节点一致性
     */
    private void validateNodeConsistency(List<String> dataLcIds, LcDefine lcDefine,
                                         String msg, boolean haveTz) throws FlowException {
        // 情况1：多个不同的流程节点
        if (dataLcIds.size() > 1) {
            log.error("流程{}操作失败,数据存在多个流程{},当前流程与数据流程不一致!", msg, dataLcIds.size());
            throw new FlowException(StringUtils.format("流程{}操作失败,当前流程与数据流程不一致!", msg));
        }
        // 情况2：无流程节点
        else if (dataLcIds.isEmpty()) {
            // 如果有排除数据
            if (haveTz) {
                return;
            }
            log.error("数据不在同一流程状态,不能{}!", msg);
            throw new FlowException(StringUtils.format("数据不在同一流程状态,不能{}!", msg));
        }
        // 情况3：流程节点不一致
        else {
            String idsStr = dataLcIds.get(0);
            if (StringUtils.isNotBlank(idsStr)) {
                // 获取数据当前流程
                int dataLcId = Integer.parseInt(idsStr);
                // 判断当前流程是否一致
                if (dataLcId != lcDefine.getId()) {
                    log.error("流程{}操作失败,当前流程[{}]与数据流程[{}]不一致!",
                        msg, lcDefine.getId(), dataLcId);
                    throw new FlowException(StringUtils.format("流程{}操作失败,当前流程与数据流程不一致!", msg));
                }
            } else {
                log.error("流程{}操作失败,当前流程为空!", msg);
                throw new FlowException(StringUtils.format("流程{}操作失败,当前流程为空!", msg));
            }
        }
    }
}
