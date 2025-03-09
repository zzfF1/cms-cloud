package com.sinosoft.notice.service.impl;

import com.sinosoft.common.redis.utils.RedisUtils;
import com.sinosoft.notice.domain.SysNotificationDelivery;
import com.sinosoft.notice.mapper.SysNotificationDeliveryMapper;
import com.sinosoft.notice.service.INotificationDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;
import java.util.List;

/**
 * @program: cms-cloud
 * @description:
 * @author: zzf
 * @create: 2025-03-08 00:03
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationDeliveryServiceImpl implements INotificationDeliveryService {
    /**
     * 短信队列锁
     */
    private static final String SMS_QUEUE_LOCK_KEY = "notification:sms_queue:lock";

    /**
     * 邮件队列锁
     */
    private static final String EMAIL_QUEUE_LOCK_KEY = "notification:email_queue:lock";

    /**
     * 队列处理锁过期时间（秒）
     */
    private static final long LOCK_EXPIRE_TIME = 300;

    /**
     * 短信发送失败重试次数
     */
    private static final int SMS_MAX_RETRY = 3;

    /**
     * 邮件发送失败重试次数
     */
    private static final int EMAIL_MAX_RETRY = 3;

    /**
     * 每次批处理的数量
     */
    private static final int BATCH_SIZE = 50;

    private final SysNotificationDeliveryMapper deliveryMapper;
    private final SmsService smsService;
    private final EmailService emailService;

    /**
     * 处理短信发送队列（定时任务）
     */
    @Scheduled(fixedDelay = 60000)
    @Override
    public void processSmsQueue() {
        // 尝试获取分布式锁，避免集群环境下重复处理
        boolean locked = RedisUtils.setObjectIfAbsent(SMS_QUEUE_LOCK_KEY, "", Duration.ofSeconds(LOCK_EXPIRE_TIME));
        if (!locked) {
            log.debug("短信队列处理任务正在其他节点执行，跳过");
            return;
        }

        try {
            log.info("开始处理短信发送队列");

            // 查询待处理的短信通知
            List<SysNotificationDelivery> pendingSms = deliveryMapper.selectPendingSms(BATCH_SIZE);
            if (pendingSms.isEmpty()) {
                log.debug("没有待处理的短信通知");
                return;
            }

            log.info("找到 {} 条待处理短信通知", pendingSms.size());

            // 批量处理
            for (SysNotificationDelivery delivery : pendingSms) {
                processSingleSms(delivery);
            }
        } catch (Exception e) {
            log.error("处理短信队列异常", e);
        } finally {
            // 释放锁
            RedisUtils.deleteObject(SMS_QUEUE_LOCK_KEY);
        }
    }

    /**
     * 处理单条短信发送
     */
    @Async
    @Transactional
    protected void processSingleSms(SysNotificationDelivery delivery) {
        log.debug("处理短信通知, ID: {}, 目标: {}", delivery.getId(), maskMobile(delivery.getTargetAddress()));

        try {
            // 发送短信
            boolean success = smsService.send(delivery.getTargetAddress(), delivery.getContent());

            // 更新发送状态
            if (success) {
                delivery.setStatus("sent");
                delivery.setSendTime(new Date());
                log.info("短信发送成功, ID: {}", delivery.getId());
            } else {
                handleSendFailure(delivery, "短信发送失败", SMS_MAX_RETRY);
            }
        } catch (Exception e) {
            log.error("短信发送异常, ID: {}", delivery.getId(), e);
            handleSendFailure(delivery, "发送异常: " + e.getMessage(), SMS_MAX_RETRY);
        } finally {
            // 更新记录
            deliveryMapper.updateById(delivery);
        }
    }

    /**
     * 处理邮件发送队列（定时任务）
     */
    @Scheduled(fixedDelay = 60000)
    @Override
    public void processEmailQueue() {
        // 尝试获取分布式锁，避免集群环境下重复处理
        boolean locked = RedisUtils.setObjectIfAbsent(EMAIL_QUEUE_LOCK_KEY, "", Duration.ofSeconds(LOCK_EXPIRE_TIME));
        if (!locked) {
            log.debug("邮件队列处理任务正在其他节点执行，跳过");
            return;
        }
        try {
            log.info("开始处理邮件发送队列");
            // 查询待处理的邮件通知
            List<SysNotificationDelivery> pendingEmails = deliveryMapper.selectPendingEmails(BATCH_SIZE);
            if (pendingEmails.isEmpty()) {
                log.debug("没有待处理的邮件通知");
                return;
            }
            log.info("找到 {} 条待处理邮件通知", pendingEmails.size());
            // 批量处理
            for (SysNotificationDelivery delivery : pendingEmails) {
                processSingleEmail(delivery);
            }
        } catch (Exception e) {
            log.error("处理邮件队列异常", e);
        } finally {
            // 释放锁
            RedisUtils.deleteObject(EMAIL_QUEUE_LOCK_KEY);
        }
    }

    /**
     * 处理单条邮件发送
     */
    @Async
    @Transactional
    protected void processSingleEmail(SysNotificationDelivery delivery) {
        log.debug("处理邮件通知, ID: {}, 目标: {}", delivery.getId(), maskEmail(delivery.getTargetAddress()));
        try {
            // 解析邮件内容
            String[] contentParts = parseEmailContent(delivery.getContent());
            String subject = contentParts[0];
            String content = contentParts[1];
            // 发送邮件
            boolean success = emailService.send(delivery.getTargetAddress(), subject, content);

            // 更新发送状态
            if (success) {
                delivery.setStatus("sent");
                delivery.setSendTime(new Date());
                log.info("邮件发送成功, ID: {}", delivery.getId());
            } else {
                handleSendFailure(delivery, "邮件发送失败", EMAIL_MAX_RETRY);
            }
        } catch (Exception e) {
            log.error("邮件发送异常, ID: {}", delivery.getId(), e);
            handleSendFailure(delivery, "发送异常: " + e.getMessage(), EMAIL_MAX_RETRY);
        } finally {
            // 更新记录
            deliveryMapper.updateById(delivery);
        }
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
     * 处理发送失败
     */
    private void handleSendFailure(SysNotificationDelivery delivery, String errorMessage, int maxRetry) {
        delivery.setErrorMessage(errorMessage);
        // 增加重试次数
        int retryCount = delivery.getRetryCount() == null ? 0 : delivery.getRetryCount();
        retryCount++;
        delivery.setRetryCount(retryCount);
        delivery.setLastRetryTime(new Date());
        // 判断是否达到最大重试次数
        if (retryCount >= maxRetry) {
            delivery.setStatus("failed");
            log.warn("发送失败达到最大重试次数 {}, ID: {}", maxRetry, delivery.getId());
        } else {
            delivery.setStatus("pending");
            log.info("发送失败, 将重试, 当前重试次数: {}, ID: {}", retryCount, delivery.getId());
        }
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

    /**
     * SMS服务接口
     */
    @Service
    public static class SmsService {

        /**
         * 发送短信
         *
         * @param mobile  手机号
         * @param content 内容
         * @return 是否发送成功
         */
        public boolean send(String mobile, String content) {
            // 实际项目中，这里应该调用SMS服务商提供的API
            // 例如阿里云、腾讯云等短信服务

            // 示例代码，模拟发送过程
            log.info("发送短信到: {}, 内容: {}", mobile, content);
            try {
                // 模拟发送耗时
                Thread.sleep(200);

                // 模拟发送成功
                return true;
            } catch (Exception e) {
                log.error("短信发送异常", e);
                return false;
            }
        }
    }

    /**
     * Email服务接口
     */
    @Service
    public static class EmailService {

        /**
         * 发送邮件
         *
         * @param email   邮箱地址
         * @param subject 主题
         * @param content 内容
         * @return 是否发送成功
         */
        public boolean send(String email, String subject, String content) {
            // 实际项目中，这里应该使用JavaMail或Spring Mail发送邮件

            // 示例代码，模拟发送过程
            log.info("发送邮件到: {}, 主题: {}", email, subject);
            try {
                // 模拟发送耗时
                Thread.sleep(300);
                // 模拟发送成功
                return true;
            } catch (Exception e) {
                log.error("邮件发送异常", e);
                return false;
            }
        }
    }
}
