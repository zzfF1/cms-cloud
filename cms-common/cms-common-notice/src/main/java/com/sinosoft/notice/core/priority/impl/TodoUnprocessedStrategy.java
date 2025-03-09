package com.sinosoft.notice.core.priority.impl;

import com.sinosoft.notice.core.priority.NotificationPriorityStrategy;
import com.sinosoft.notice.domain.SysNotification;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 待办未处理优先级调整策略
 * 当待办通知长时间未处理时升级优先级
 */
@Component
public class TodoUnprocessedStrategy implements NotificationPriorityStrategy {

    /**
     * 未处理检查时间阈值（小时）
     */
    private static final int UNPROCESSED_CHECK_HOURS = 48;

    /**
     * 处理率阈值（小于该比例需要升级）
     */
    private static final double PROCESS_RATE_THRESHOLD = 0.5;

    @Override
    public String getStrategyName() {
        return "todo_unprocessed";
    }

    @Override
    public boolean shouldUpgradePriority(SysNotification notification,
                                         Date currentTime,
                                         int readCount,
                                         int totalCount) {
        // 1. 只对待办类型通知有效
        if (!"todo".equals(notification.getType())) {
            return false;
        }

        // 2. 如果已经是高优先级，无需升级
        if ("high".equals(notification.getPriority())) {
            return false;
        }

        // 3. 检查通知发送后的时间
        if (notification.getCreateTime() == null) {
            return false;
        }

        long elapsedMillis = currentTime.getTime() - notification.getCreateTime().getTime();
        long elapsedHours = TimeUnit.MILLISECONDS.toHours(elapsedMillis);

        // 这里简化实现，使用读取率作为处理率的近似
        // 实际实现中应该使用真实的处理数据
        double processRate = totalCount > 0 ? (double) readCount / totalCount : 0;

        // 如果发送后较长时间，处理率仍然很低，则需要升级
        return elapsedHours >= UNPROCESSED_CHECK_HOURS && processRate < PROCESS_RATE_THRESHOLD;
    }

    @Override
    public String getSuggestedPriority(SysNotification notification) {
        return "high";
    }
}
