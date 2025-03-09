package com.sinosoft.notice.core.strategy.impl;

import com.sinosoft.notice.core.strategy.NotificationChannelStrategy;
import com.sinosoft.notice.core.strategy.NotificationSendCallback;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationDelivery;
import com.sinosoft.notice.service.impl.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * 短信通知渠道策略
 */
@Slf4j
@Component
public class SmsNotificationStrategy implements NotificationChannelStrategy {

    private final SmsService smsService;

    public SmsNotificationStrategy(SmsService smsService) {
        this.smsService = smsService;
    }

    @Override
    public String getChannelCode() {
        return "sms";
    }

    @Override
    public String getChannelName() {
        return "短信通知";
    }

    @Override
    public SysNotificationDelivery prepareDelivery(SysNotification notification,
                                                   Long userId,
                                                   String targetAddress,
                                                   Object templateParams) {
        if (templateParams == null || !(templateParams instanceof Map)) {
            throw new IllegalArgumentException("SMS template params must be a Map");
        }

        // 根据模板和参数渲染短信内容
        String smsContent = renderSmsContent(notification, (Map<String, Object>) templateParams);

        SysNotificationDelivery delivery = new SysNotificationDelivery();
        delivery.setId(System.currentTimeMillis() + new Random().nextInt(1000));
        delivery.setNotificationId(notification.getNotificationId());
        delivery.setUserId(userId);
        delivery.setChannel(getChannelCode());
        delivery.setContent(smsContent);
        delivery.setStatus("pending");
        delivery.setTargetAddress(targetAddress);
        delivery.setCreateTime(new Date());

        return delivery;
    }

    @Override
    public void send(SysNotificationDelivery delivery, NotificationSendCallback callback) {
        try {
            log.debug("发送短信通知, ID: {}, 目标: {}", delivery.getId(), maskMobile(delivery.getTargetAddress()));
            boolean success = smsService.send(delivery.getTargetAddress(), delivery.getContent());

            if (success) {
                delivery.setStatus("sent");
                delivery.setSendTime(new Date());
                log.info("短信发送成功, ID: {}", delivery.getId());

                if (callback != null) {
                    callback.onSuccess(delivery);
                }
            } else {
                log.warn("短信发送失败, ID: {}", delivery.getId());
                if (callback != null) {
                    callback.onFailure(delivery, "发送失败");
                }
            }
        } catch (Exception e) {
            log.error("短信发送异常, ID: {}", delivery.getId(), e);
            if (callback != null) {
                callback.onFailure(delivery, "发送异常: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean isEnabledForUser(Long userId, Map<String, Object> settings, String notificationType) {
        // 短信通知需要明确启用
        String settingKey = notificationType + "_notify_sms";
        return settings != null &&
            settings.containsKey(settingKey) &&
            "1".equals(settings.get(settingKey).toString());
    }

    /**
     * 渲染短信内容
     */
    private String renderSmsContent(SysNotification notification, Map<String, Object> params) {
        // 实际实现应该使用模板引擎渲染
        // 这里简单返回通知内容
        return notification.getContent();
    }

    /**
     * 手机号脱敏
     */
    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 11) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }
}
