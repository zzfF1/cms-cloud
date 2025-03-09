package com.sinosoft.notice.core.priority.impl;

import com.sinosoft.notice.core.priority.NotificationPriorityStrategy;
import com.sinosoft.notice.domain.SysNotification;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 响应率低优先级调整策略
 * 当通知发送后一段时间，响应率低于预期时升级优先级
 */
@Component
public class LowResponseRateStrategy implements NotificationPriorityStrategy {

    /**
     * 响应率检查时间阈值（小时）
     */
    private static final int RESPONSE_CHECK_HOURS = 12;

    /**
     * 响应率阈值（小于该比例需要升级）
     */
    private static final double RESPONSE_RATE_THRESHOLD = 0.3;

    @Override
    public String getStrategyName() {
        return "low_response_rate";
    }

    @Override
    public boolean shouldUpgradePriority(SysNotification notification,
                                         Date currentTime,
                                         int readCount,
                                         int totalCount) {
        // 1. 如果已经是高优先级，无需升级
        if ("high".equals(notification.getPriority())) {
            return false;
        }

        // 2. 检查通知发送后的时间
        if (notification.getCreateTime() == null) {
            return false;
        }

        long elapsedMillis = currentTime.getTime() - notification.getCreateTime().getTime();
        long elapsedHours = TimeUnit.MILLISECONDS.toHours(elapsedMillis);

        // 3. 检查响应率（已读率）
        double responseRate = totalCount > 0 ? (double) readCount / totalCount : 0;

        // 如果发送后一定时间，响应率仍然很低，则需要升级
        return elapsedHours >= RESPONSE_CHECK_HOURS && responseRate < RESPONSE_RATE_THRESHOLD;
    }

    @Override
    public String getSuggestedPriority(SysNotification notification) {
        // 如果当前是低优先级，升级为中优先级；如果是中优先级，升级为高优先级
        return "low".equals(notification.getPriority()) ? "medium" : "high";
    }
}
