package com.sinosoft.notice.core.model;

import com.sinosoft.notice.core.enums.NotificationPriority;
import com.sinosoft.notice.core.enums.NotificationStatus;
import com.sinosoft.notice.core.enums.NotificationType;
import com.sinosoft.notice.domain.SysNotification;

import java.util.Calendar;
import java.util.Date;

/**
 * 通知实体增强工具类
 */
public class NotificationDomainService {

    /**
     * 判断通知是否过期
     */
    public static boolean isExpired(SysNotification notification) {
        if (notification.getExpirationDate() == null) {
            return false;
        }
        return notification.getExpirationDate().before(new Date());
    }

    /**
     * 标记通知为已过期
     */
    public static void markAsExpired(SysNotification notification) {
        notification.setStatus(NotificationStatus.EXPIRED.getCode());
        notification.setUpdateTime(new Date());
    }

    /**
     * 设置通知为合并通知
     */
    public static void markAsMerged(SysNotification notification, int count) {
        notification.setIsMerged("1");
        notification.setMergedCount(count);
        notification.setUpdateTime(new Date());
    }

    /**
     * 设置通知过期时间（天数）
     */
    public static void setExpirationDays(SysNotification notification, Integer days) {
        if (days == null || days <= 0) {
            notification.setExpirationDate(null);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        notification.setExpirationDate(calendar.getTime());
    }

    /**
     * 获取通知类型枚举
     */
    public static NotificationType getType(SysNotification notification) {
        return NotificationType.fromCode(notification.getType());
    }

    /**
     * 获取通知优先级枚举
     */
    public static NotificationPriority getPriority(SysNotification notification) {
        return NotificationPriority.fromCode(notification.getPriority());
    }

    /**
     * 获取通知状态枚举
     */
    public static NotificationStatus getStatus(SysNotification notification) {
        return NotificationStatus.fromCode(notification.getStatus());
    }

    /**
     * 判断是否为合并通知
     */
    public static boolean isMerged(SysNotification notification) {
        return "1".equals(notification.getIsMerged());
    }
}
