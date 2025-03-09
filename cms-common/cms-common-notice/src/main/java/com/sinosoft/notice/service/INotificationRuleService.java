package com.sinosoft.notice.service;

import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;

public interface INotificationRuleService {
    @Scheduled(fixedDelay = 60000) // 每1分钟执行一次
    void executeScheduleRules();

    void processEventRule(String eventType, Map<String, Object> eventData);

    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    void processExpiredNotifications();
}
