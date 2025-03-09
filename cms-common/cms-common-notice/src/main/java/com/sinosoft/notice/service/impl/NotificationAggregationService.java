package com.sinosoft.notice.service.impl;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateEngine;
import com.alibaba.fastjson2.JSON;
import com.sinosoft.common.sse.dto.SseMessageDto;
import com.sinosoft.common.sse.utils.SseMessageUtils;
import com.sinosoft.notice.core.aggregation.NotificationAggregationRule;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.mapper.SysNotificationMapper;
import com.sinosoft.notice.mapper.SysNotificationUserMapper;
import com.sinosoft.notice.model.dto.NotificationSseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 通知智能聚合服务（使用SSE）
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationAggregationService {

    private final SysNotificationMapper notificationMapper;
    private final SysNotificationUserMapper notificationUserMapper;
    private final TemplateEngine templateEngine;

    /**
     * 默认聚合规则
     */
    private final List<NotificationAggregationRule> defaultRules = Arrays.asList(
        createDefaultRule("announce_rule", "公告聚合",
            Collections.singletonList("announcement"), null, 5, 20, 60),
        createDefaultRule("todo_rule", "待办聚合",
            Collections.singletonList("todo"), null, 3, 10, 30),
        createDefaultRule("alert_rule", "预警聚合",
            Collections.singletonList("alert"), null, 2, 5, 15)
    );

    /**
     * 创建默认聚合规则
     */
    private NotificationAggregationRule createDefaultRule(String ruleId, String ruleName,
                                                          List<String> types, List<String> sources,
                                                          int minCount, int maxCount, int timeWindow) {
        NotificationAggregationRule rule = new NotificationAggregationRule();
        rule.setRuleId(ruleId);
        rule.setRuleName(ruleName);
        rule.setNotificationTypes(types);
        rule.setSourceTypes(sources);
        rule.setMinAggregationCount(minCount);
        rule.setMaxAggregationCount(maxCount);
        rule.setTimeWindowMinutes(timeWindow);
        rule.setAggregatedTitleTemplate("您有${count}条${typeName}需要查看");
        rule.setAggregatedContentTemplate(
            "以下是最近${timeWindow}分钟内的${typeName}汇总：\n\n" +
                "#foreach($item in $items)\n" +
                "${foreach.count}. ${item.title}\n" +
                "#end\n\n" +
                "请及时查看处理。"
        );
        rule.setEnabled(true);
        return rule;
    }

    /**
     * 定时执行通知聚合（每5分钟执行一次）
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Transactional(readOnly = true)
    public void aggregateNotifications() {
        log.info("开始执行通知聚合");

        // 计算时间窗口
        Date now = new Date();

        // 获取所有活跃的聚合规则
        List<NotificationAggregationRule> rules = getActiveRules();

        for (NotificationAggregationRule rule : rules) {
            try {
                // 计算该规则的时间窗口
                Date startTime = new Date(now.getTime() -
                    TimeUnit.MINUTES.toMillis(rule.getTimeWindowMinutes()));

                // 查询时间窗口内符合规则的通知
                List<SysNotification> notifications = findNotificationsForRule(rule, startTime, now);

                if (notifications.size() >= rule.getMinAggregationCount()) {
                    // 按用户分组
                    Map<Long, List<SysNotification>> userNotifications =
                        groupNotificationsByUser(notifications);

                    // 为每个用户创建聚合通知
                    for (Map.Entry<Long, List<SysNotification>> entry : userNotifications.entrySet()) {
                        Long userId = entry.getKey();
                        List<SysNotification> userNotifs = entry.getValue();

                        if (userNotifs.size() >= rule.getMinAggregationCount()) {
                            // 创建聚合通知
                            createAggregatedSseNotification(userId, userNotifs, rule);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("执行通知聚合规则异常, 规则ID: {}", rule.getRuleId(), e);
            }
        }
    }

    /**
     * 获取所有活跃的聚合规则
     */
    private List<NotificationAggregationRule> getActiveRules() {
        // 实际项目中应该从配置中心或数据库获取
        // 这里使用默认规则
        return defaultRules.stream()
            .filter(NotificationAggregationRule::getEnabled)
            .collect(Collectors.toList());
    }

    /**
     * 查询符合规则的通知
     */
    private List<SysNotification> findNotificationsForRule(
        NotificationAggregationRule rule, Date startTime, Date endTime) {

        // 实际项目中应该根据规则条件查询
        // 这里简化实现，仅按时间和类型筛选
        List<SysNotification> result = notificationMapper.selectByCreateTimeRange(startTime, endTime);

        // 按类型筛选
        if (rule.getNotificationTypes() != null && !rule.getNotificationTypes().isEmpty()) {
            result = result.stream()
                .filter(n -> rule.getNotificationTypes().contains(n.getType()))
                .collect(Collectors.toList());
        }

        // 按来源筛选
        if (rule.getSourceTypes() != null && !rule.getSourceTypes().isEmpty()) {
            result = result.stream()
                .filter(n -> rule.getSourceTypes().contains(n.getSourceType()))
                .collect(Collectors.toList());
        }

        return result;
    }

    /**
     * 按用户分组通知
     */
    private Map<Long, List<SysNotification>> groupNotificationsByUser(List<SysNotification> notifications) {
        Map<Long, List<SysNotification>> result = new HashMap<>();

        // 获取所有通知ID
        List<Long> notificationIds = notifications.stream()
            .map(SysNotification::getNotificationId)
            .collect(Collectors.toList());

        // 遍历每个通知ID
        for (Long notificationId : notificationIds) {
            // 查询该通知的所有接收人
            List<Long> userIds = notificationUserMapper.selectUserIdsByNotificationId(notificationId);

            // 找到对应的通知对象
            Optional<SysNotification> notificationOpt = notifications.stream()
                .filter(n -> n.getNotificationId().equals(notificationId))
                .findFirst();

            if (notificationOpt.isPresent()) {
                SysNotification notification = notificationOpt.get();

                // 将通知添加到每个用户的列表中
                for (Long userId : userIds) {
                    result.computeIfAbsent(userId, k -> new ArrayList<>())
                        .add(notification);
                }
            }
        }

        return result;
    }

    /**
     * 创建聚合SSE通知
     */
    private void createAggregatedSseNotification(
        Long userId, List<SysNotification> notifications, NotificationAggregationRule rule) {

        try {
            // 准备渲染数据
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("count", notifications.size());

            // 获取通知类型名称
            String typeName = getTypeDisplayName(notifications.get(0).getType());
            templateData.put("typeName", typeName);

            templateData.put("timeWindow", rule.getTimeWindowMinutes());
            templateData.put("items", notifications);

            // 渲染标题和内容
            String title = renderTemplate(rule.getAggregatedTitleTemplate(), templateData);
            String content = renderTemplate(rule.getAggregatedContentTemplate(), templateData);

            // 聚合元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("ruleId", rule.getRuleId());
            metadata.put("ruleName", rule.getRuleName());
            metadata.put("aggregatedCount", notifications.size());
            metadata.put("notificationIds", notifications.stream()
                .map(SysNotification::getNotificationId)
                .collect(Collectors.toList()));

            // 创建SSE消息
            NotificationSseDto sseDto = NotificationSseDto.builder()
                .type("notification_aggregated")
                .title(title)
                .content(content)
                .notificationType(notifications.get(0).getType())
                .timestamp(System.currentTimeMillis())
                .data(metadata)
                .build();

            // 发送SSE消息
            SseMessageUtils.sendMessage(userId, JSON.toJSONString(sseDto));

            log.info("发送聚合通知SSE消息成功, 用户ID: {}, 规则: {}, 聚合数量: {}",
                userId, rule.getRuleId(), notifications.size());

        } catch (Exception e) {
            log.error("创建聚合通知异常, 用户ID: {}, 规则: {}", userId, rule.getRuleId(), e);
        }
    }

    /**
     * 获取通知类型显示名称
     */
    private String getTypeDisplayName(String type) {
        switch (type) {
            case "todo":
                return "待办事项";
            case "alert":
                return "预警提醒";
            case "announcement":
                return "系统公告";
            default:
                return "通知";
        }
    }

    /**
     * 渲染模板
     */
    private String renderTemplate(String templateStr, Map<String, Object> data) {
        Template template = templateEngine.getTemplate(templateStr);
        return template.render(data);
    }
}
