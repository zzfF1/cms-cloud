package com.sinosoft.notice.core.priority;

import com.sinosoft.notice.domain.SysNotification;
import java.util.Date;

/**
 * 通知优先级调整策略接口
 */
public interface NotificationPriorityStrategy {

    /**
     * 策略名称
     */
    String getStrategyName();

    /**
     * 判断是否需要升级优先级
     *
     * @param notification 通知对象
     * @param currentTime 当前时间
     * @param readCount 已读数
     * @param totalCount 总接收人数
     * @return 是否需要升级
     */
    boolean shouldUpgradePriority(SysNotification notification,
                                  Date currentTime,
                                  int readCount,
                                  int totalCount);

    /**
     * 获取建议的优先级
     *
     * @param notification 通知对象
     * @return 优先级代码
     */
    String getSuggestedPriority(SysNotification notification);
}
