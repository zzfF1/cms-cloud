package com.sinosoft.notice.core.strategy.impl;

import com.sinosoft.notice.core.strategy.NotificationChannelStrategy;
import com.sinosoft.notice.core.strategy.NotificationSendCallback;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationDelivery;
import com.sinosoft.notice.service.impl.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * 邮件通知渠道策略
 */
@Slf4j
@Component
public class EmailNotificationStrategy implements NotificationChannelStrategy {

    private final EmailService emailService;

    public EmailNotificationStrategy(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public String getChannelCode() {
        return "email";
    }

    @Override
    public String getChannelName() {
        return "邮件通知";
    }

    @Override
    public SysNotificationDelivery prepareDelivery(SysNotification notification,
                                                   Long userId,
                                                   String targetAddress,
                                                   Object templateParams) {
        if (templateParams == null || !(templateParams instanceof Map)) {
            throw new IllegalArgumentException("Email template params must be a Map");
        }

        // 根据模板和参数渲染邮件内容
        String[] emailContent = renderEmailContent(notification, (Map<String, Object>) templateParams);
        String subject = emailContent[0];
        String body = emailContent[1];

        SysNotificationDelivery delivery = new SysNotificationDelivery();
        delivery.setId(System.currentTimeMillis() + new Random().nextInt(1000));
        delivery.setNotificationId(notification.getNotificationId());
        delivery.setUserId(userId);
        delivery.setChannel(getChannelCode());
        delivery.setContent(subject + "\n" + body);
        delivery.setStatus("pending");
        delivery.setTargetAddress(targetAddress);
        delivery.setCreateTime(new Date());

        return delivery;
    }

    @Override
    public void send(SysNotificationDelivery delivery, NotificationSendCallback callback) {
        try {
            log.debug("处理邮件通知, ID: {}, 目标: {}", delivery.getId(), maskEmail(delivery.getTargetAddress()));

            // 解析邮件内容（主题和正文）
            String[] contentParts = parseEmailContent(delivery.getContent());
            String subject = contentParts[0];
            String content = contentParts[1];

            boolean success = emailService.send(delivery.getTargetAddress(), subject, content);

            if (success) {
                delivery.setStatus("sent");
                delivery.setSendTime(new Date());
                log.info("邮件发送成功, ID: {}", delivery.getId());

                if (callback != null) {
                    callback.onSuccess(delivery);
                }
            } else {
                log.warn("邮件发送失败, ID: {}", delivery.getId());
                if (callback != null) {
                    callback.onFailure(delivery, "发送失败");
                }
            }
        } catch (Exception e) {
            log.error("邮件发送异常, ID: {}", delivery.getId(), e);
            if (callback != null) {
                callback.onFailure(delivery, "发送异常: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean isEnabledForUser(Long userId, Map<String, Object> settings, String notificationType) {
        // 邮件通知需要明确启用
        String settingKey = notificationType + "_notify_email";
        return settings != null &&
            settings.containsKey(settingKey) &&
            "1".equals(settings.get(settingKey).toString());
    }

    /**
     * 渲染邮件内容
     */
    private String[] renderEmailContent(SysNotification notification, Map<String, Object> params) {
        // 实际实现应该使用模板引擎渲染
        // 这里简单返回通知标题和内容
        return new String[]{notification.getTitle(), notification.getContent()};
    }

    /**
     * 解析邮件内容（主题和正文）
     */
    private String[] parseEmailContent(String content) {
        if (content == null || content.isEmpty()) {
            return new String[]{"", ""};
        }

        int firstNewLine = content.indexOf('\n');
        if (firstNewLine > 0) {
            String subject = content.substring(0, firstNewLine).trim();
            String body = content.substring(firstNewLine + 1).trim();
            return new String[]{subject, body};
        } else {
            return new String[]{content, ""};
        }
    }

    /**
     * 邮箱脱敏
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }

        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (local.length() <= 2) {
            return email;
        }

        String maskedLocal = local.substring(0, 1) + "****" + local.substring(local.length() - 1);
        return maskedLocal + domain;
    }
}
