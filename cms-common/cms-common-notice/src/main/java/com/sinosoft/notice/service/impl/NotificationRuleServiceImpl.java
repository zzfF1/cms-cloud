package com.sinosoft.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.redis.utils.RedisUtils;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationRule;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import com.sinosoft.notice.mapper.SysNotificationMapper;
import com.sinosoft.notice.mapper.SysNotificationRuleMapper;
import com.sinosoft.notice.mapper.SysNotificationTemplateMapper;
import com.sinosoft.notice.service.INotificationRuleService;
import com.sinosoft.notice.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 通知规则服务实现类
 *
 * @author: zzf
 * @create: 2025-03-08 00:06
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationRuleServiceImpl implements INotificationRuleService {

    /**
     * 规则执行锁
     */
    private static final String RULE_EXECUTION_LOCK_KEY = "notification:rule_execution:lock";
    /**
     * 过期通知处理锁
     */
    private static final String EXPIRED_NOTIFICATION_LOCK_KEY = "notification:expired_notification:lock";
    /**
     * 锁过期时间（秒）
     */
    private static final long LOCK_EXPIRE_TIME = 300;

    private final SysNotificationRuleMapper ruleMapper;
    private final SysNotificationTemplateMapper templateMapper;
    private final SysNotificationMapper notificationMapper;
    private final INotificationService notificationService;
    private final JdbcTemplate jdbcTemplate;
    private ExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * 执行定时规则（定时任务）
     */
    @Scheduled(fixedDelay = 60000)
    @Override
    public void executeScheduleRules() {
        // 尝试获取分布式锁，避免集群环境下重复处理
        boolean locked = RedisUtils.setObjectIfAbsent(RULE_EXECUTION_LOCK_KEY, "", Duration.ofSeconds(LOCK_EXPIRE_TIME));
        if (!locked) {
            log.debug("规则执行任务正在其他节点执行，跳过");
            return;
        }
        try {
            log.info("开始执行通知规则");
            // 当前时间
            LocalDateTime now = LocalDateTime.now();
            // 查询所有启用的规则
            List<SysNotificationRule> rules = ruleMapper.selectActiveRules();
            if (rules.isEmpty()) {
                log.debug("没有启用的通知规则");
                return;
            }
            log.info("找到 {} 条启用的通知规则", rules.size());
            // 处理每条规则
            for (SysNotificationRule rule : rules) {
                try {
                    processRule(rule, now);
                } catch (Exception e) {
                    log.error("处理规则异常, 规则ID: {}", rule.getRuleId(), e);
                }
            }
        } catch (Exception e) {
            log.error("执行通知规则异常", e);
        } finally {
            // 释放锁
            RedisUtils.deleteObject(RULE_EXECUTION_LOCK_KEY);
        }
    }

    /**
     * 处理单条规则
     */
    @Transactional
    protected void processRule(SysNotificationRule rule, LocalDateTime now) {
        log.debug("处理规则: {}, ID: {}", rule.getRuleName(), rule.getRuleId());

        // 跳过非定时规则
        if (!"schedule".equals(rule.getRuleType())) {
            return;
        }
        // 验证cron表达式
        String cronExpression = rule.getTriggerSchedule();
        if (StringUtils.isEmpty(cronExpression)) {
            log.warn("规则没有设置cron表达式, 规则ID: {}", rule.getRuleId());
            return;
        }
        try {
            // 解析cron表达式
            CronExpression cron = CronExpression.parse(cronExpression);

            // 获取上次执行时间并转换为LocalDateTime
            LocalDateTime lastExecTime = null;
            if (rule.getLastExecTime() != null) {
                lastExecTime = rule.getLastExecTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            }

            // 如果没有上次执行时间，或者当前时间在上次执行时间之后的下一个执行时间点
            if (lastExecTime == null ||
                now.isAfter(cron.next(lastExecTime))) {
                log.info("规则满足执行条件, 规则ID: {}", rule.getRuleId());
                // 执行规则
                executeRule(rule);
                // 更新上次执行时间
                rule.setLastExecTime(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));
                ruleMapper.updateById(rule);
            }
        } catch (Exception e) {
            log.error("解析或执行规则异常, 规则ID: {}", rule.getRuleId(), e);
        }
    }

    /**
     * 执行规则
     */
    protected void executeRule(SysNotificationRule rule) {
        log.info("执行规则: {}, ID: {}", rule.getRuleName(), rule.getRuleId());

        // 获取关联的通知模板
        SysNotificationTemplate template = templateMapper.selectById(rule.getTemplateId());
        if (template == null) {
            log.error("通知模板不存在, ID: {}", rule.getTemplateId());
            return;
        }

        // 解析数据源配置
        JSONObject dataSource = null;
        try {
            if (StringUtils.isNotEmpty(rule.getDataSource())) {
                dataSource = JSON.parseObject(rule.getDataSource());
            }
        } catch (Exception e) {
            log.error("解析数据源配置异常, 规则ID: {}", rule.getRuleId(), e);
            return;
        }

        if (dataSource == null) {
            log.warn("规则未配置数据源, 规则ID: {}", rule.getRuleId());
            return;
        }

        // 根据不同数据源类型处理
        String dataSourceType = dataSource.getString("type");
        if ("sql".equals(dataSourceType)) {
            processSqlDataSource(rule, template, dataSource);
        } else {
            log.warn("不支持的数据源类型: {}, 规则ID: {}", dataSourceType, rule.getRuleId());
        }
    }

    /**
     * 处理SQL数据源
     */
    private void processSqlDataSource(SysNotificationRule rule, SysNotificationTemplate template, JSONObject dataSource) {
        // 获取SQL语句
        String sql = dataSource.getString("sql");
        if (StringUtils.isEmpty(sql)) {
            log.warn("SQL数据源未配置SQL语句, 规则ID: {}", rule.getRuleId());
            return;
        }

        // 执行SQL查询
        try {
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);
            log.info("SQL查询结果: {} 条记录, 规则ID: {}", resultList.size(), rule.getRuleId());

            // 解析来源类型配置
            String sourceType = dataSource.getString("sourceType");
            String sourceIdField = dataSource.getString("sourceIdField");

            // 为每条记录生成通知
            for (Map<String, Object> data : resultList) {
                // 提取来源ID
                String sourceId = null;
                if (StringUtils.isNotEmpty(sourceIdField) && data.containsKey(sourceIdField)) {
                    Object sourceIdObj = data.get(sourceIdField);
                    if (sourceIdObj != null) {
                        sourceId = sourceIdObj.toString();
                    }
                }

                // 发送通知
                try {
//                    notificationService.sendNotification(
//                        template.getTemplateCode(),
//                        data,
//                        sourceType,
//                        sourceId,
//                        null
//                    );
                } catch (Exception e) {
                    log.error("发送通知异常, 规则ID: {}, 数据: {}", rule.getRuleId(), data, e);
                }
            }
        } catch (Exception e) {
            log.error("执行SQL查询异常, 规则ID: {}, SQL: {}", rule.getRuleId(), sql, e);
        }
    }

    /**
     * 处理事件规则
     *
     * @param eventType 事件类型
     * @param eventData 事件数据
     */
    @Override
    public void processEventRule(String eventType, Map<String, Object> eventData) {
        if (eventType == null || eventData == null) {
            return;
        }
        log.info("处理事件规则, 事件类型: {}", eventType);
        // 查询匹配事件类型的规则
        List<SysNotificationRule> rules = ruleMapper.selectByEventType(eventType);
        if (rules.isEmpty()) {
            log.debug("没有匹配的事件规则, 事件类型: {}", eventType);
            return;
        }
        // 处理每条规则
        for (SysNotificationRule rule : rules) {
            try {
                // 检查是否为事件规则
                if (!"event".equals(rule.getRuleType())) {
                    continue;
                }
                // 检查是否启用
                if (!"0".equals(rule.getStatus())) {
                    continue;
                }
                // 检查条件是否满足
                if (evaluateCondition(rule.getTriggerCondition(), eventData)) {
                    log.info("事件规则条件满足, 规则ID: {}", rule.getRuleId());
                    // 获取关联的通知模板
                    SysNotificationTemplate template = templateMapper.selectById(rule.getTemplateId());
                    if (template == null) {
                        log.error("通知模板不存在, ID: {}", rule.getTemplateId());
                        continue;
                    }
                    // 发送通知
                    String sourceType = eventType;
                    String sourceId = null;
                    if (eventData.containsKey("id")) {
                        sourceId = eventData.get("id").toString();
                    }
//                    notificationService.sendNotification(
//                        template.getTemplateCode(),
//                        eventData,
//                        sourceType,
//                        sourceId,
//                        null
//                    );
                    // 更新上次执行时间
                    rule.setLastExecTime(new Date());
                    ruleMapper.updateById(rule);
                }
            } catch (Exception e) {
                log.error("处理事件规则异常, 规则ID: {}", rule.getRuleId(), e);
            }
        }
    }

    /**
     * 评估条件表达式
     */
    private boolean evaluateCondition(String condition, Map<String, Object> data) {
        if (StringUtils.isEmpty(condition)) {
            return true;  // 没有条件，默认为真
        }
        try {
            // 解析表达式
            Expression expression = expressionParser.parseExpression(condition);
            // 创建求值上下文
            StandardEvaluationContext context = new StandardEvaluationContext();
            // 设置变量
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                context.setVariable(entry.getKey(), entry.getValue());
            }
            // 求值
            return Boolean.TRUE.equals(expression.getValue(context, Boolean.class));
        } catch (Exception e) {
            log.error("评估条件表达式异常: {}, 条件: {}", e.getMessage(), condition);
            return false;
        }
    }

    /**
     * 处理过期通知（定时任务）
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Override
    public void processExpiredNotifications() {
        // 尝试获取分布式锁，避免集群环境下重复处理
        boolean locked = RedisUtils.setObjectIfAbsent(EXPIRED_NOTIFICATION_LOCK_KEY, "", Duration.ofSeconds(LOCK_EXPIRE_TIME));
        if (!locked) {
            log.debug("过期通知处理任务正在其他节点执行，跳过");
            return;
        }
        try {
            log.info("开始处理过期通知");
            // 获取当前时间
            Date now = new Date();
            // 查询已过期但未标记为过期的通知
            List<SysNotification> expiredNotifications = notificationMapper.selectExpired(now);
            if (expiredNotifications.isEmpty()) {
                log.debug("没有需要处理的过期通知");
                return;
            }
            log.info("找到 {} 条过期通知", expiredNotifications.size());
            // 批量更新通知状态为已过期
            List<Long> expiredIds = new ArrayList<>();
            for (SysNotification notification : expiredNotifications) {
                expiredIds.add(notification.getNotificationId());
            }
            int updateCount = notificationMapper.updateStatusBatch(expiredIds, "1"); // 1-过期
            log.info("已将 {} 条通知标记为过期", updateCount);
        } catch (Exception e) {
            log.error("处理过期通知异常", e);
        } finally {
            // 释放锁
            RedisUtils.deleteObject(EXPIRED_NOTIFICATION_LOCK_KEY);
        }
    }
}
