package com.sinosoft.notice.core.strategy;

import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import com.sinosoft.notice.model.NotificationPayload;

import java.util.Date;
import java.util.List;

/**
 * 通知合并策略接口
 */
public interface NotificationMergeStrategy {
    /**
     * 获取策略代码
     */
    String getStrategyCode();

    /**
     * 获取策略名称
     */
    String getStrategyName();

    /**
     * 处理通知合并
     *
     * @param existingNotification 现有通知
     * @param newNotification      新通知
     * @param template             通知模板
     * @param payload              通知业务负载
     * @return 处理后的通知
     */
    SysNotification merge(SysNotification existingNotification,
                          SysNotification newNotification,
                          SysNotificationTemplate template,
                          NotificationPayload payload);

    /**
     * 查找可合并的现有通知
     *
     * @param businessKey 业务键
     * @param userId      用户ID
     * @param startTime   起始时间
     * @return 可合并的通知
     */
    List<SysNotification> findMergeableNotifications(String businessKey,
                                                     Long userId,
                                                     Date startTime);
}
