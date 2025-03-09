package com.sinosoft.notice.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface INotificationDeliveryService {
    @Scheduled(fixedDelay = 60000) // 每1分钟执行一次
    void processSmsQueue();

    @Scheduled(fixedDelay = 60000) // 每1分钟执行一次
    void processEmailQueue();
}
