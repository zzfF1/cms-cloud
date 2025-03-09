package com.sinosoft.common.utils.wage;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.redis.utils.RedisUtils;
import com.sinosoft.common.schema.common.domain.WageCalGradeRelation;
import com.sinosoft.common.schema.common.domain.WageCalculationDefinition;
import com.sinosoft.common.schema.agent.domain.Laagent;
import com.sinosoft.common.schema.agent.domain.Latree;
import com.sinosoft.common.json.utils.JsonUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 佣金计算缓存工具类
 * 用于佣金计算过程中的数据缓存和读取
 *
 * @author zzf
 * @version 3.0
 * @update 2025-03-04 - 增强了统计功能，优化了状态跟踪
 */
@Slf4j
public class WageRedisUtil {

    /**
     * 缓存键类型枚举
     */
    public enum KeyType {
        /**
         * 算法定义
         */
        DEFINITION("DEF"),
        /**
         * 佣金与算法关系
         */
        RELATION("REL"),
        /**
         * 计算过程
         */
        ELEMENT("ELE"),
        /**
         * 人员信息
         */
        AGENT("AGENT"),
        /**
         * 职级信息
         */
        TREE("TREE"),
        /**
         * 计算参数
         */
        PARAMETER("PARM"),
        /**
         * 代理人计算结果
         */
        AGENT_RESULT("AGENT_RESULT_KEY"),
        /**
         * 计算结果数据
         */
        CALCULATION_RESULT("CAL_RESULT_KEY"),
        /**
         * 完成计算数量
         */
        RESULT_COUNT("CAL_RESULT_COUNT"),
        /**
         * 当前步长
         */
        CURRENT_STEP("CURRENT_STEP"),
        /**
         * 步长计算完成计数
         */
        STEP_RESULT_COUNT("STEP_RESULT_COUNT"),
        /**
         * 步长总任务数
         */
        STEP_TOTAL_COUNT("STEP_TOTAL_COUNT"),
        /**
         * 任务缓存
         */
        TASK("TASK");

        private final String code;

        KeyType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    // 常量定义 ----------------------------------------------------------
    private static final String WAGE_KEY = "WAGE";
    private static final String SPLIT = ":";

    /**
     * 计算结果标志：未计算
     */
    public static final String RESULT_FLAG_PENDING = "0";
    /**
     * 计算结果标志：计算完成
     */
    public static final String RESULT_FLAG_COMPLETED = "1";
    /**
     * 计算结果标志：计算失败
     */
    public static final String RESULT_FLAG_FAILED = "-1";

    /**
     * 批处理统计的键类型
     */
    public static final String BATCH_STATS_KEY = "BATCH_STATS";
    public static final String STEP_STATS_KEY = "STEP_STATS";

    /**
     * 佣金计算缓存前缀
     */
    private final String redisHeader;

    /**
     * 运行ID
     */
    private final String runId;

    /**
     * 构造函数
     *
     * @param redisHeader 缓存标识（MD5值）
     * @param runId       运行ID
     */
    public WageRedisUtil(String redisHeader, String runId) {
        this.redisHeader = WAGE_KEY + SPLIT + redisHeader;
        this.runId = runId;
        log.debug("初始化佣金计算缓存工具: header={}, runId={}", this.redisHeader, this.runId);
    }

    // 键管理方法 ----------------------------------------------------------

    /**
     * 获取完整的缓存键
     *
     * @param keyType 键类型
     * @param subKeys 子键数组
     * @return 完整的缓存键
     */
    private String buildKey(KeyType keyType, String... subKeys) {
        StringBuilder keyBuilder = new StringBuilder(redisHeader)
            .append(SPLIT)
            .append(keyType.getCode());

        for (String subKey : subKeys) {
            if (StringUtils.isNotBlank(subKey)) {
                keyBuilder.append(SPLIT).append(subKey);
            }
        }
        return keyBuilder.toString();
    }

    /**
     * 获取缓存键前缀
     *
     * @param keyType 键类型
     * @return 缓存键前缀
     */
    private String buildKeyPattern(KeyType keyType) {
        return redisHeader + SPLIT + keyType.getCode() + "*";
    }

    /**
     * 获取缓存前缀
     *
     * @return 缓存前缀
     */
    public String getHeader() {
        return this.redisHeader;
    }

    /**
     * 获取运行ID
     *
     * @return 运行ID
     */
    public String getRunId() {
        return this.runId;
    }

    // 核心操作方法 ----------------------------------------------------------

    /**
     * 安全获取缓存，出现异常时返回默认值
     *
     * @param operation    缓存操作
     * @param defaultValue 默认值
     * @param <T>          返回类型
     * @return 操作结果或默认值
     */
    private <T> T safeOperation(Supplier<T> operation, T defaultValue) {
        try {
            T result = operation.get();
            return result != null ? result : defaultValue;
        } catch (Exception e) {
            log.warn("Redis操作异常: {}", e.getMessage());
            return defaultValue;
        }
    }

    /**
     * 安全执行操作，记录详细错误日志
     *
     * @param operation 操作
     * @param errorMsg  错误信息
     * @param params    错误信息参数
     * @return 操作是否成功
     */
    private boolean safeExecute(Runnable operation, String errorMsg, Object... params) {
        try {
            operation.run();
            return true;
        } catch (Exception e) {
            log.error(errorMsg + ": {}", params, e.getMessage(), e);
            return false;
        }
    }

    // 缓存清理方法 ----------------------------------------------------------

    /**
     * 清空当前批次的所有缓存
     *
     * @return true成功 false失败
     */
    public boolean clearRedis() {
        log.info("清空佣金计算缓存: {}", this.redisHeader);
        if (StringUtils.isEmpty(redisHeader)) {
            return true;
        }
        return safeExecute(
            () -> RedisUtils.deleteKeys(redisHeader + "*"),
            "清空佣金计算缓存异常"
        );
    }

    /**
     * 清空计算结果相关缓存（保留定义和参数）
     * 用于在计算完成后清理临时数据
     *
     * @return true成功 false失败
     */
    public boolean clearResultCache() {
        log.info("清空佣金计算结果缓存: {}", this.redisHeader);
        if (StringUtils.isEmpty(redisHeader)) {
            return true;
        }

        try {
            // 清除结果相关缓存
            clearRedisByType(KeyType.AGENT_RESULT);
            clearRedisByType(KeyType.CALCULATION_RESULT);
            clearRedisByType(KeyType.RESULT_COUNT);
            clearRedisByType(KeyType.STEP_RESULT_COUNT);
            clearRedisByType(KeyType.STEP_TOTAL_COUNT);
            clearRedisByType(KeyType.CURRENT_STEP);
            clearRedisByType(KeyType.TASK);

            log.info("成功清空佣金计算结果缓存: {}", this.redisHeader);
            return true;
        } catch (Exception e) {
            log.error("清空佣金计算结果缓存异常: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 根据键类型删除缓存
     *
     * @param keyType 键类型
     * @return 是否成功
     */
    public boolean clearRedisByType(KeyType keyType) {
        if (StringUtils.isEmpty(redisHeader) || keyType == null) {
            return false;
        }

        return safeExecute(
            () -> RedisUtils.deleteKeys(buildKeyPattern(keyType)),
            "清空特定类型佣金计算缓存异常",
            keyType.getCode()
        );
    }

    // 初始化相关方法 ----------------------------------------------------------

    /**
     * 缓存佣金算法定义
     *
     * @param dataList 佣金算法定义列表
     * @return 是否成功
     */
    public boolean initParmsDEF(List<WageCalculationDefinition> dataList) {
        if (CollUtil.isEmpty(dataList)) {
            log.warn("初始化佣金算法定义失败：数据为空");
            return false;
        }

        return safeExecute(
            () -> {
                String key = buildKey(KeyType.DEFINITION);
                RedisUtils.setCacheList(key, dataList);
                log.debug("成功缓存佣金算法定义: count={}", dataList.size());
            },
            "缓存佣金算法定义异常"
        );
    }

    /**
     * 初始化计算过程
     *
     * @param def          佣金项
     * @param elementsList 过程对象
     * @return 是否成功
     */
    public boolean initParmsELE(WageCalculationDefinition def, List<WageCalElements> elementsList) {
        if (def == null || StringUtils.isBlank(def.getCalCode()) || elementsList == null) {
            log.warn("初始化计算过程失败：参数不完整");
            return false;
        }

        return safeExecute(
            () -> {
                String key = buildKey(KeyType.ELEMENT, def.getCalCode());
                RedisUtils.setCacheList(key, elementsList);
                log.debug("成功缓存计算过程: calCode={}, count={}", def.getCalCode(), elementsList.size());
            },
            "缓存计算过程异常",
            def.getCalCode()
        );
    }

    /**
     * 初始化佣金与职级关系
     *
     * @param gradeRelationList 佣金与职级关系列表
     * @return 是否成功
     */
    public boolean initParmsGradeRelation(List<WageCalGradeRelation> gradeRelationList) {
        if (CollUtil.isEmpty(gradeRelationList)) {
            log.warn("初始化佣金与职级关系失败：数据为空");
            return false;
        }

        try {
            // 按算法代码分组并批量设置
            Map<String, Map<String, String>> groupedRelations = new HashMap<>();

            for (WageCalGradeRelation relation : gradeRelationList) {
                if (StringUtils.isBlank(relation.getCalCode()) || StringUtils.isBlank(relation.getAgentGrade())) {
                    continue;
                }

                groupedRelations
                    .computeIfAbsent(relation.getCalCode(), k -> new HashMap<>())
                    .put(relation.getAgentGrade(), "1");
            }

            // 批量设置到Redis
            for (Map.Entry<String, Map<String, String>> entry : groupedRelations.entrySet()) {
                String key = buildKey(KeyType.RELATION, entry.getKey());
                RedisUtils.setCacheMap(key, entry.getValue());
            }

            log.debug("成功缓存佣金与职级关系: count={}", gradeRelationList.size());
            return true;
        } catch (Exception e) {
            log.error("缓存佣金与职级关系异常: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 初始化计算人员
     *
     * @param agents 人员信息
     * @param trees  职级信息
     * @return 是否成功
     */
    public boolean initWageCalAgents(Map<String, Laagent> agents, Map<String, Latree> trees) {
        if (agents == null || trees == null) {
            log.warn("初始化计算人员失败：参数为空");
            return false;
        }

        return safeExecute(
            () -> {
                String agentKey = buildKey(KeyType.AGENT);
                String treeKey = buildKey(KeyType.TREE);

                RedisUtils.setCacheMap(agentKey, agents);
                RedisUtils.setCacheMap(treeKey, trees);

                log.debug("成功缓存计算人员信息: 代理人数={}, 职级数={}", agents.size(), trees.size());
            },
            "缓存计算人员信息异常"
        );
    }

    // 定义项、人员、职级数据获取方法 ------------------------------------------------------------

    /**
     * 获取算法定义列表
     *
     * @return 算法定义列表
     */
    public List<WageCalculationDefinition> getDefinitionList() {
        return safeOperation(
            () -> RedisUtils.getCacheList(buildKey(KeyType.DEFINITION)),
            Collections.emptyList()
        );
    }

    /**
     * 获取特定步长的所有定义项，按calOrder排序
     *
     * @param step 步长
     * @return 该步长的所有定义项列表
     */
    public List<WageCalculationDefinition> getDefinitionsByStep(int step) {
        List<WageCalculationDefinition> allDefinitions = getDefinitionList();
        if (CollUtil.isEmpty(allDefinitions)) {
            return Collections.emptyList();
        }

        return allDefinitions.stream()
            .filter(def -> def.getWagecalStep() == step)
            .sorted(Comparator.comparing(WageCalculationDefinition::getCalOrder))
            .collect(Collectors.toList());
    }

    /**
     * 获取人员信息
     *
     * @param agentCode 工号
     * @return 人员信息，不存在返回null
     */
    public Laagent getAgent(String agentCode) {
        if (StringUtils.isBlank(agentCode)) {
            return null;
        }
        return safeOperation(
            () -> RedisUtils.getCacheMapValue(buildKey(KeyType.AGENT), agentCode),
            null
        );
    }

    /**
     * 获取人员职级信息
     *
     * @param agentCode 工号
     * @return 职级信息，不存在返回null
     */
    public Latree getTree(String agentCode) {
        if (StringUtils.isBlank(agentCode)) {
            return null;
        }
        return safeOperation(
            () -> RedisUtils.getCacheMapValue(buildKey(KeyType.TREE), agentCode),
            null
        );
    }

    /**
     * 获取过程数据
     *
     * @param calCode 算法编码
     * @return 过程数据列表，不存在返回空列表
     */
    public List<WageCalElements> getCalElements(String calCode) {
        if (StringUtils.isBlank(calCode)) {
            return Collections.emptyList();
        }
        return safeOperation(
            () -> RedisUtils.getCacheList(buildKey(KeyType.ELEMENT, calCode)),
            Collections.emptyList()
        );
    }

    /**
     * 判断佣金与职级是否存在关系
     *
     * @param calCode 算法编码
     * @param grade   职级
     * @return true存在 false不存在
     */
    public boolean isGradeRelation(String calCode, String grade) {
        if (StringUtils.isBlank(calCode) || StringUtils.isBlank(grade)) {
            return false;
        }
        return safeOperation(
            () -> RedisUtils.getCacheMapValue(buildKey(KeyType.RELATION, calCode), grade) != null,
            false
        );
    }

    // 计算结果相关方法 --------------------------------------------------------

    /**
     * 获取所有人员计算结果状态
     *
     * @return 计算结果状态映射，key为代理人编码，value为计算状态
     */
    public Map<String, String> getCalAgents() {
        return safeOperation(
            () -> RedisUtils.getCacheMap(buildKey(KeyType.AGENT_RESULT)),
            Collections.emptyMap()
        );
    }

    /**
     * 设置人员计算结果状态
     *
     * @param agentCode 工号
     * @param flag      结果 0未计算 1计算完成 -1计算失败
     * @return 是否设置成功
     */
    public boolean setAgentCalResultFlag(String agentCode, String flag) {
        if (StringUtils.isBlank(agentCode)) {
            return false;
        }

        return safeExecute(
            () -> RedisUtils.setCacheMapValue(buildKey(KeyType.AGENT_RESULT), agentCode, flag),
            "设置人员计算结果状态异常",
            agentCode
        );
    }

    /**
     * 设置人员特定步长的计算结果状态
     *
     * @param agentCode 工号
     * @param step      步长
     * @param flag      结果 0未计算 1计算完成 -1计算失败
     * @return 是否设置成功
     */
    public boolean setAgentStepCalResultFlag(String agentCode, int step, String flag) {
        if (StringUtils.isBlank(agentCode)) {
            return false;
        }

        try {
            RedisUtils.setCacheMapValue(
                buildKey(KeyType.AGENT_RESULT, "STEP" + step),
                agentCode,
                flag
            );

            // 如果计算完成或失败，同时增加步长计算完成计数
            if (RESULT_FLAG_COMPLETED.equals(flag) || RESULT_FLAG_FAILED.equals(flag)) {
                incrStepCalResultCount(step);
            }

            return true;
        } catch (Exception e) {
            log.error("设置人员步长计算结果状态异常: agentCode={}, step={}, error={}",
                agentCode, step, e.getMessage());
            return false;
        }
    }

    /**
     * 设置人员计算结果值
     *
     * @param agentCode 工号
     * @param result    结果对象
     * @return 是否设置成功
     */
    public boolean setAgentCalResultVal(String agentCode, WageCalRedisResult result) {
        if (StringUtils.isBlank(agentCode) || result == null || StringUtils.isBlank(result.getWageCode())) {
            return false;
        }

        return safeExecute(
            () -> {
                String key = buildKey(KeyType.CALCULATION_RESULT, agentCode);
                RedisUtils.setCacheMapValue(key, result.getWageCode(), result);
            },
            "设置人员计算结果值异常",
            agentCode, result.getWageCode()
        );
    }

    /**
     * 获取人员所有计算结果
     *
     * @param agentCode 工号
     * @return 结果映射表，key为佣金代码，value为计算结果
     */
    public Map<String, WageCalRedisResult> getCalResultList(String agentCode) {
        if (StringUtils.isBlank(agentCode)) {
            return Collections.emptyMap();
        }
        return safeOperation(
            () -> RedisUtils.getCacheMap(buildKey(KeyType.CALCULATION_RESULT, agentCode)),
            Collections.emptyMap()
        );
    }

    /**
     * 获取佣金计算结果值
     *
     * @param agentCode 工号
     * @param calCode   算法ID
     * @return 计算结果值，不存在返回0
     */
    public BigDecimal getCalWageSum(String agentCode, String calCode) {
        if (StringUtils.isBlank(agentCode) || StringUtils.isBlank(calCode)) {
            return BigDecimal.ZERO;
        }

        try {
            WageCalRedisResult calResult = RedisUtils.getCacheMapValue(
                buildKey(KeyType.CALCULATION_RESULT, agentCode), calCode);

            if (calResult == null) {
                return BigDecimal.ZERO;
            }

            Object resultVal = calResult.getResultVal();
            if (resultVal == null) {
                return BigDecimal.ZERO;
            }

            return new BigDecimal(String.valueOf(resultVal));
        } catch (Exception e) {
            log.warn("获取佣金计算结果值异常: agentCode={}, calCode={}, error={}",
                agentCode, calCode, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 增加计算结果计数
     *
     * @return 增加后的计数值
     */
    public long incrCalResultCount() {
        return safeOperation(
            () -> RedisUtils.incrAtomicValue(buildKey(KeyType.RESULT_COUNT)),
            0L
        );
    }

    /**
     * 获取已经计算完成的人数
     *
     * @return 已经计算完成的人数
     */
    public long getCalResultCount() {
        return safeOperation(
            () -> RedisUtils.getAtomicValue(buildKey(KeyType.RESULT_COUNT)),
            0L
        );
    }

    // 计算参数相关方法 --------------------------------------------------------

    /**
     * 设置计算参数结果 (Map类型)
     *
     * @param redisKey 缓存key
     * @param mapKey   map key
     * @param strVal   值
     * @return 是否设置成功
     */
    public boolean setCalParmsMapVal(String redisKey, String mapKey, String strVal) {
        if (StringUtils.isBlank(redisKey) || StringUtils.isBlank(mapKey)) {
            return false;
        }

        return safeExecute(
            () -> RedisUtils.setCacheMapValue(buildKey(KeyType.PARAMETER, redisKey), mapKey, strVal),
            "设置计算参数Map值异常",
            redisKey, mapKey
        );
    }

    /**
     * 获取计算时结果 (Map类型)
     *
     * @param redisKey 缓存key
     * @param mapKey   map key
     * @return 结果值，不存在返回null
     */
    public String getCalParmsMapVal(String redisKey, String mapKey) {
        if (StringUtils.isBlank(redisKey) || StringUtils.isBlank(mapKey)) {
            return null;
        }

        return safeOperation(
            () -> RedisUtils.getCacheMapValue(buildKey(KeyType.PARAMETER, redisKey), mapKey),
            null
        );
    }

    /**
     * 设置计算参数结果 (字符串类型)
     *
     * @param redisKey 缓存key
     * @param strVal   值
     * @return 是否设置成功
     */
    public boolean setCalParmsVal(String redisKey, String strVal) {
        if (StringUtils.isBlank(redisKey)) {
            return false;
        }

        return safeExecute(
            () -> RedisUtils.setCacheObject(buildKey(KeyType.PARAMETER, redisKey), strVal),
            "设置计算参数值异常",
            redisKey
        );
    }

    /**
     * 获取计算时结果 (字符串类型)
     *
     * @param redisKey 缓存key
     * @return 结果值，不存在返回null
     */
    public String getCalParmsVal(String redisKey) {
        if (StringUtils.isBlank(redisKey)) {
            return null;
        }

        return safeOperation(
            () -> RedisUtils.getCacheObject(buildKey(KeyType.PARAMETER, redisKey)),
            null
        );
    }

    // 步长计算相关方法 --------------------------------------------------------

    /**
     * 设置当前计算步长
     *
     * @param step 步长
     * @return 是否设置成功
     */
    public boolean setCurrentStep(int step) {
        return safeExecute(
            () -> {
                RedisUtils.setCacheObject(buildKey(KeyType.CURRENT_STEP), String.valueOf(step));
                log.debug("设置当前计算步长: step={}", step);
            },
            "设置当前计算步长异常",
            step
        );
    }

    /**
     * 获取当前计算步长
     *
     * @return 当前步长，失败返回0
     */
    public int getCurrentStep() {
        return safeOperation(() -> {
            String step = RedisUtils.getCacheObject(buildKey(KeyType.CURRENT_STEP));
            return StringUtils.isNotEmpty(step) ? Integer.parseInt(step) : 0;
        }, 0);
    }

    /**
     * 增加步长计算完成计数
     *
     * @param step 步长
     * @return 增加后的计数值
     */
    public long incrStepCalResultCount(int step) {
        return safeOperation(
            () -> RedisUtils.incrAtomicValue(buildKey(KeyType.STEP_RESULT_COUNT, String.valueOf(step))),
            0L
        );
    }

    /**
     * 重置步长计算完成计数
     *
     * @param step 步长
     * @return 是否重置成功
     */
    public boolean resetStepCalResultCount(int step) {
        return safeExecute(
            () -> RedisUtils.setCacheObject(buildKey(KeyType.STEP_RESULT_COUNT, String.valueOf(step)), 0),
            "重置步长计算完成计数异常",
            step
        );
    }

    /**
     * 获取步长计算完成计数
     *
     * @param step 步长
     * @return 完成计数，失败返回0
     */
    public long getStepCalResultCount(int step) {
        return safeOperation(
            () -> RedisUtils.getAtomicValue(buildKey(KeyType.STEP_RESULT_COUNT, String.valueOf(step))),
            0L
        );
    }

    /**
     * 设置步长总任务数
     *
     * @param step  步长
     * @param count 总任务数
     * @return 是否设置成功
     */
    public boolean setStepTotalCount(int step, int count) {
        return safeExecute(
            () -> RedisUtils.setCacheObject(
                buildKey(KeyType.STEP_TOTAL_COUNT, String.valueOf(step)),
                String.valueOf(count)
            ),
            "设置步长总任务数异常",
            step, count
        );
    }

    /**
     * 获取步长总任务数
     *
     * @param step 步长
     * @return 总任务数，失败返回0
     */
    public int getStepTotalCount(int step) {
        return safeOperation(() -> {
            String count = RedisUtils.getCacheObject(
                buildKey(KeyType.STEP_TOTAL_COUNT, String.valueOf(step))
            );
            return StringUtils.isNotEmpty(count) ? Integer.parseInt(count) : 0;
        }, 0);
    }

    // 步长列表相关方法 --------------------------------------------------------

    /**
     * 保存步长列表
     *
     * @param steps 步长列表
     * @return 是否成功
     */
    public boolean setStepsList(List<Integer> steps) {
        return safeExecute(
            () -> {
                String key = buildKey(KeyType.PARAMETER, "STEPS_LIST");
                RedisUtils.setCacheObject(key, JsonUtils.toJsonString(steps));
            },
            "保存步长列表异常"
        );
    }

    /**
     * 获取步长列表
     *
     * @return 步长列表
     */
    public List<Integer> getStepsList() {
        return safeOperation(() -> {
            String key = buildKey(KeyType.PARAMETER, "STEPS_LIST");
            String stepsJson = RedisUtils.getCacheObject(key);
            if (StringUtils.isBlank(stepsJson)) {
                return Collections.emptyList();
            }
            return JsonUtils.parseArray(stepsJson, Integer.class);
        }, Collections.emptyList());
    }

    /**
     * 初始化步长信息
     *
     * @param step       步长
     * @param totalCount 总任务数
     * @return 是否成功
     */
    public boolean initStepInfo(int step, int totalCount) {
        try {
            // 设置步长状态
            setStepStatus(step, "PENDING");
            // 设置步长总任务数
            setStepTotalCount(step, totalCount);
            // 重置步长计算完成计数
            resetStepCalResultCount(step);
            return true;
        } catch (Exception e) {
            log.error("初始化步长信息异常: step={}, error={}", step, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 设置步长状态
     *
     * @param step   步长
     * @param status 状态
     * @return 是否成功
     */
    public boolean setStepStatus(int step, String status) {
        return safeExecute(
            () -> {
                String key = buildKey(KeyType.PARAMETER, "STEP_STATUS_" + step);
                RedisUtils.setCacheObject(key, status);
            },
            "设置步长状态异常",
            step, status
        );
    }

    /**
     * 获取步长状态
     *
     * @param step 步长
     * @return 步长状态
     */
    public String getStepStatus(int step) {
        return safeOperation(() -> {
            String key = buildKey(KeyType.PARAMETER, "STEP_STATUS_" + step);
            return RedisUtils.getCacheObject(key);
        }, null);
    }

    // 批量统计相关方法 --------------------------------------------------------

    /**
     * 增加批次处理成功计数
     *
     * @param runId 运行ID
     * @return 增加后的计数值
     */
    public long incrBatchSuccessCount(String runId) {
        try {
            String key = buildKey(KeyType.PARAMETER, BATCH_STATS_KEY + ":" + runId);
            String field = "SUCCESS_COUNT";
            String currentValue = RedisUtils.getCacheMapValue(key, field);
            int newValue = 1;
            if (StringUtils.isNotBlank(currentValue)) {
                newValue = Integer.parseInt(currentValue) + 1;
            }
            RedisUtils.setCacheMapValue(key, field, String.valueOf(newValue));
            return newValue;
        } catch (Exception e) {
            log.error("增加批次成功计数异常: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 增加批次处理失败计数
     *
     * @param runId 运行ID
     * @return 增加后的计数值
     */
    public long incrBatchFailCount(String runId) {
        try {
            String key = buildKey(KeyType.PARAMETER, BATCH_STATS_KEY + ":" + runId);
            String field = "FAILED_COUNT";
            String currentValue = RedisUtils.getCacheMapValue(key, field);
            int newValue = 1;
            if (StringUtils.isNotBlank(currentValue)) {
                newValue = Integer.parseInt(currentValue) + 1;
            }
            RedisUtils.setCacheMapValue(key, field, String.valueOf(newValue));
            return newValue;
        } catch (Exception e) {
            log.error("增加批次失败计数异常: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 减少批次待处理计数
     *
     * @param runId 运行ID
     * @return 减少后的计数值
     */
    public long decrBatchPendingCount(String runId) {
        try {
            String key = buildKey(KeyType.PARAMETER, BATCH_STATS_KEY + ":" + runId + ":PENDING_COUNT");
            String currentValue = RedisUtils.getCacheObject(key);
            int newValue = 0;
            if (StringUtils.isNotBlank(currentValue)) {
                newValue = Math.max(0, Integer.parseInt(currentValue) - 1);
            }
            RedisUtils.setCacheObject(key, String.valueOf(newValue));
            return newValue;
        } catch (Exception e) {
            log.error("减少批次待处理计数异常: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 获取批次成功计数
     *
     * @param runId 运行ID
     * @return 成功计数
     */
    public int getBatchSuccessCount(String runId) {
        try {
            String key = buildKey(KeyType.PARAMETER, BATCH_STATS_KEY + ":" + runId);
            String value = RedisUtils.getCacheMapValue(key, "SUCCESS_COUNT");
            return StringUtils.isNotBlank(value) ? Integer.parseInt(value) : 0;
        } catch (Exception e) {
            log.error("获取批次成功计数异常: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 获取批次失败计数
     *
     * @param runId 运行ID
     * @return 失败计数
     */
    public int getBatchFailCount(String runId) {
        try {
            String key = buildKey(KeyType.PARAMETER, BATCH_STATS_KEY + ":" + runId);
            String value = RedisUtils.getCacheMapValue(key, "FAILED_COUNT");
            return StringUtils.isNotBlank(value) ? Integer.parseInt(value) : 0;
        } catch (Exception e) {
            log.error("获取批次失败计数异常: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 获取批次待处理计数
     *
     * @param runId 运行ID
     * @return 待处理计数
     */
    public int getBatchPendingCount(String runId) {
        try {
            String key = buildKey(KeyType.PARAMETER, BATCH_STATS_KEY + ":" + runId + ":PENDING_COUNT");
            String value = RedisUtils.getCacheObject(key);
            return StringUtils.isNotBlank(value) ? Integer.parseInt(value) : 0;
        } catch (Exception e) {
            log.error("获取批次待处理计数异常: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 增加步长处理成功计数
     *
     * @param runId 运行ID
     * @param step  步长
     * @return 增加后的计数值
     */
    public long incrStepSuccessCount(String runId, int step) {
        try {
            String key = buildKey(KeyType.PARAMETER, STEP_STATS_KEY + ":" + runId + ":" + step);
            String field = "SUCCESS_COUNT";
            String currentValue = RedisUtils.getCacheMapValue(key, field);
            int newValue = 1;
            if (StringUtils.isNotBlank(currentValue)) {
                newValue = Integer.parseInt(currentValue) + 1;
            }
            RedisUtils.setCacheMapValue(key, field, String.valueOf(newValue));
            return newValue;
        } catch (Exception e) {
            log.error("增加步长成功计数异常: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 增加步长处理失败计数
     *
     * @param runId 运行ID
     * @param step  步长
     * @return 增加后的计数值
     */
    public long incrStepFailCount(String runId, int step) {
        try {
            String key = buildKey(KeyType.PARAMETER, STEP_STATS_KEY + ":" + runId + ":" + step);
            String field = "FAILED_COUNT";
            String currentValue = RedisUtils.getCacheMapValue(key, field);
            int newValue = 1;
            if (StringUtils.isNotBlank(currentValue)) {
                newValue = Integer.parseInt(currentValue) + 1;
            }
            RedisUtils.setCacheMapValue(key, field, String.valueOf(newValue));
            return newValue;
        } catch (Exception e) {
            log.error("增加步长失败计数异常: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 减少步长待处理计数
     *
     * @param runId 运行ID
     * @param step  步长
     * @return 减少后的计数值
     */
    public long decrStepPendingCount(String runId, int step) {
        try {
            String key = buildKey(KeyType.PARAMETER, STEP_STATS_KEY + ":" + runId + ":" + step + ":PENDING_COUNT");
            String currentValue = RedisUtils.getCacheObject(key);
            int newValue = 0;
            if (StringUtils.isNotBlank(currentValue)) {
                newValue = Math.max(0, Integer.parseInt(currentValue) - 1);
            }
            RedisUtils.setCacheObject(key, String.valueOf(newValue));
            return newValue;
        } catch (Exception e) {
            log.error("减少步长待处理计数异常: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 获取步长统计信息
     *
     * @param runId 运行ID
     * @param step  步长
     * @return 步长统计信息，包含成功数、失败数、总数
     */
    public Map<String, Integer> getStepStats(String runId, int step) {
        Map<String, Integer> stats = new HashMap<>();
        try {
            String key = buildKey(KeyType.PARAMETER, STEP_STATS_KEY + ":" + runId + ":" + step);
            Map<String, String> redisMap = RedisUtils.getCacheMap(key);

            // 获取成功数和失败数
            int successCount = 0;
            int failCount = 0;
            if (redisMap != null) {
                String successStr = redisMap.get("SUCCESS_COUNT");
                String failStr = redisMap.get("FAILED_COUNT");

                if (StringUtils.isNotBlank(successStr)) {
                    successCount = Integer.parseInt(successStr);
                }

                if (StringUtils.isNotBlank(failStr)) {
                    failCount = Integer.parseInt(failStr);
                }
            }

            // 获取待处理数
            int pendingCount = 0;
            String pendingKey = buildKey(KeyType.PARAMETER, STEP_STATS_KEY + ":" + runId + ":" + step + ":PENDING_COUNT");
            String pendingStr = RedisUtils.getCacheObject(pendingKey);
            if (StringUtils.isNotBlank(pendingStr)) {
                pendingCount = Integer.parseInt(pendingStr);
            }

            stats.put("SUCCESS_COUNT", successCount);
            stats.put("FAILED_COUNT", failCount);
            stats.put("PENDING_COUNT", pendingCount);
            stats.put("TOTAL_COUNT", successCount + failCount + pendingCount);

            return stats;
        } catch (Exception e) {
            log.error("获取步长统计信息异常: {}", e.getMessage());
            return stats;
        }
    }
}
