package com.sinosoft.notice.core.priority.impl;

import com.sinosoft.notice.core.priority.NotificationPriorityStrategy;
import com.sinosoft.notice.domain.SysNotification;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 过期接近优先级调整策略
 * 当通知即将过期但尚未被多数人读取时，升级优先级
 */
@Component
public class ExpirationApproachingStrategy implements NotificationPriorityStrategy {

    /**
     * 即将过期阈值（小时）
     */
    private static final int EXPIRATION_THRESHOLD_HOURS = 24;

    /**
     * 读取率阈值（小于该比例需要升级）
     */
    private static final double READ_RATE_THRESHOLD = 0.5;

    @Override
    public String getStrategyName() {
        return "expiration_approaching";
    }

    @Override
    public boolean shouldUpgradePriority(SysNotification notification,
                                         Date currentTime,
                                         int readCount,
                                         int totalCount) {
        // 1. 检查通知是否有过期时间
        if (notification.getExpirationDate() == null) {
            return false;
        }

        // 2. 如果已经是高优先级，无需升级
        if ("high".equals(notification.getPriority())) {
            return false;
        }

        // 3. 检查是否即将过期
        long remainingMillis = notification.getExpirationDate().getTime() - currentTime.getTime();
        long remainingHours = TimeUnit.MILLISECONDS.toHours(remainingMillis);

        // 4. 检查读取率
        double readRate = totalCount > 0 ? (double) readCount / totalCount : 0;

        // 如果即将过期且读取率低，则需要升级
        return remainingHours <= EXPIRATION_THRESHOLD_HOURS && readRate < READ_RATE_THRESHOLD;
    }

    @Override
    public String getSuggestedPriority(SysNotification notification) {
        return "high";
    }
}

