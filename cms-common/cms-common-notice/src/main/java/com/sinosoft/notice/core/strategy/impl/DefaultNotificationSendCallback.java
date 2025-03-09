package com.sinosoft.notice.core.strategy.impl;

import com.sinosoft.notice.core.event.DomainEventPublisher;
import com.sinosoft.notice.core.event.NotificationDeliveryFailedEvent;
import com.sinosoft.notice.core.event.NotificationDeliverySuccessEvent;
import com.sinosoft.notice.core.strategy.NotificationSendCallback;
import com.sinosoft.notice.domain.SysNotificationDelivery;
import com.sinosoft.notice.mapper.SysNotificationDeliveryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 默认通知发送回调实现
 */
@Slf4j
@Component
public class DefaultNotificationSendCallback implements NotificationSendCallback {

    private final SysNotificationDeliveryMapper deliveryMapper;
    private final DomainEventPublisher eventPublisher;

    /**
     * 最大重试次数
     */
    private static final int SMS_MAX_RETRY = 3;
    private static final int EMAIL_MAX_RETRY = 3;
    private static final int DEFAULT_MAX_RETRY = 1;

    public DefaultNotificationSendCallback(SysNotificationDeliveryMapper deliveryMapper,
                                           DomainEventPublisher eventPublisher) {
        this.deliveryMapper = deliveryMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onSuccess(SysNotificationDelivery delivery) {
        delivery.setStatus("sent");
        delivery.setSendTime(new Date());
        deliveryMapper.updateById(delivery);

        log.info("通知发送成功, ID: {}, 渠道: {}, 目标: {}",
            delivery.getId(), delivery.getChannel(), maskSensitiveInfo(delivery.getTargetAddress()));

        // 发布送达成功事件
        eventPublisher.publish(new NotificationDeliverySuccessEvent(
            delivery.getNotificationId(),
            delivery.getUserId(),
            delivery.getChannel()
        ));
    }

    @Override
    public void onFailure(SysNotificationDelivery delivery, String errorMessage) {
        delivery.setErrorMessage(errorMessage);

        // 增加重试次数
        int retryCount = delivery.getRetryCount() == null ? 0 : delivery.getRetryCount();
        retryCount++;
        delivery.setRetryCount(retryCount);
        delivery.setLastRetryTime(new Date());

        // 判断是否达到最大重试次数
        int maxRetry = getMaxRetryForChannel(delivery.getChannel());
        if (retryCount >= maxRetry) {
            delivery.setStatus("failed");
            log.warn("通知发送失败达到最大重试次数 {}, ID: {}", maxRetry, delivery.getId());

            // 发布送达失败事件
            eventPublisher.publish(new NotificationDeliveryFailedEvent(
                delivery.getNotificationId(),
                delivery.getUserId(),
                delivery.getChannel(),
                errorMessage
            ));
        } else {
            delivery.setStatus("pending");
            log.info("通知发送失败, 将重试, 当前重试次数: {}, ID: {}", retryCount, delivery.getId());
        }

        deliveryMapper.updateById(delivery);
    }

    /**
     * 获取渠道最大重试次数
     */
    private int getMaxRetryForChannel(String channel) {
        switch (channel) {
            case "sms":
                return SMS_MAX_RETRY;
            case "email":
                return EMAIL_MAX_RETRY;
            default:
                return DEFAULT_MAX_RETRY;
        }
    }

    /**
     * 脱敏处理
     */
    private String maskSensitiveInfo(String info) {
        if (info == null) {
            return null;
        }

        // 邮箱脱敏
        if (info.contains("@")) {
            int atIndex = info.indexOf('@');
            if (atIndex <= 1) {
                return info;
            }

            String local = info.substring(0, atIndex);
            String domain = info.substring(atIndex);

            if (local.length() <= 2) {
                return info;
            }

            String maskedLocal = local.substring(0, 1) + "****" + local.substring(local.length() - 1);
            return maskedLocal + domain;
        }

        // 手机号脱敏
        if (info.length() >= 11) {
            return info.substring(0, 3) + "****" + info.substring(7);
        }

        return info;
    }
}
