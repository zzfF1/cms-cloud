package com.sinosoft.notice.core.strategy.impl;

import com.sinosoft.notice.core.strategy.NotificationChannelStrategy;
import com.sinosoft.notice.core.strategy.NotificationSendCallback;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationDelivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * 系统内通知渠道策略
 */
@Slf4j
@Component
public class SystemNotificationStrategy implements NotificationChannelStrategy {

    @Override
    public String getChannelCode() {
        return "system";
    }

    @Override
    public String getChannelName() {
        return "系统内通知";
    }

    @Override
    public SysNotificationDelivery prepareDelivery(SysNotification notification,
                                                   Long userId,
                                                   String targetAddress,
                                                   Object templateParams) {
        SysNotificationDelivery delivery = new SysNotificationDelivery();
        delivery.setId(System.currentTimeMillis() + new Random().nextInt(1000)); // 简单ID生成
        delivery.setNotificationId(notification.getNotificationId());
        delivery.setUserId(userId);
        delivery.setChannel(getChannelCode());
        delivery.setContent(notification.getContent());
        delivery.setStatus("sent"); // 系统内通知即时发送成功
        delivery.setSendTime(new Date());
        delivery.setCreateTime(new Date());

        return delivery;
    }

    @Override
    public void send(SysNotificationDelivery delivery, NotificationSendCallback callback) {
        // 系统内通知无需额外发送
        if (callback != null) {
            callback.onSuccess(delivery);
        }
    }

    @Override
    public boolean isEnabledForUser(Long userId, Map<String, Object> settings, String notificationType) {
        // 系统内通知默认启用，根据用户设置判断
        String settingKey = notificationType + "_notify_system";
        return settings == null ||
            !settings.containsKey(settingKey) ||
            "1".equals(settings.get(settingKey).toString());
    }
}
