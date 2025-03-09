package com.sinosoft.notice.core.strategy;

import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationDelivery;

/**
 * 通知渠道发送策略接口
 */
public interface NotificationChannelStrategy {
    /**
     * 获取渠道代码
     */
    String getChannelCode();

    /**
     * 获取渠道名称
     */
    String getChannelName();

    /**
     * 准备发送数据
     *
     * @param notification   通知对象
     * @param userId         用户ID
     * @param targetAddress  目标地址（手机号/邮箱）
     * @param templateParams 模板参数
     * @return 准备好的发送记录
     */
    SysNotificationDelivery prepareDelivery(SysNotification notification,
                                            Long userId,
                                            String targetAddress,
                                            Object templateParams);

    /**
     * 发送通知
     *
     * @param delivery 发送记录
     * @param callback 发送回调
     */
    void send(SysNotificationDelivery delivery, NotificationSendCallback callback);

    /**
     * 是否支持该用户
     *
     * @param userId           用户ID
     * @param settings         用户设置
     * @param notificationType 通知类型
     * @return 是否支持
     */
    boolean isEnabledForUser(Long userId,
                             java.util.Map<String, Object> settings,
                             String notificationType);
}
