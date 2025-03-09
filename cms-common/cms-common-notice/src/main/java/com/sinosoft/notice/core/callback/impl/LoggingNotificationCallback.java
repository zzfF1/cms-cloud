package com.sinosoft.notice.core.callback.impl;

import com.sinosoft.notice.core.callback.NotificationCallback;
import com.sinosoft.notice.core.callback.NotificationCallbackEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 日志记录回调实现
 */
@Slf4j
@Component
public class LoggingNotificationCallback implements NotificationCallback {

    @Override
    public String getCallbackType() {
        return "logging";
    }

    @Override
    public void onNotificationCreated(NotificationCallbackEvent.Created event) {
        log.info("通知创建: ID={}, 类型={}, 模板={}, 接收人数={}",
            event.getNotificationId(), event.getType(), event.getTemplateCode(), event.getRecipientCount());
    }

    @Override
    public void onNotificationRead(NotificationCallbackEvent.Read event) {
        log.info("通知已读: ID={}, 用户={}, 类型={}, 时间={}",
            event.getNotificationId(), event.getUserId(), event.getType(), event.getReadTime());
    }

    @Override
    public void onNotificationProcessed(NotificationCallbackEvent.Processed event) {
        log.info("通知已处理: ID={}, 用户={}, 类型={}, 时间={}, 结果={}",
            event.getNotificationId(), event.getUserId(), event.getType(),
            event.getProcessTime(), event.getProcessResult());
    }

    @Override
    public void onNotificationSent(NotificationCallbackEvent.Sent event) {
        log.info("通知已发送: ID={}, 用户={}, 渠道={}, 时间={}",
            event.getNotificationId(), event.getUserId(), event.getChannel(), event.getSendTime());
    }

    @Override
    public void onNotificationSendFailed(NotificationCallbackEvent.SendFailed event) {
        log.warn("通知发送失败: ID={}, 用户={}, 渠道={}, 时间={}, 错误={}, 重试次数={}",
            event.getNotificationId(), event.getUserId(), event.getChannel(),
            event.getFailTime(), event.getErrorMessage(), event.getRetryCount());
    }

    @Override
    public void onNotificationExpired(NotificationCallbackEvent.Expired event) {
        log.info("通知已过期: ID={}, 类型={}, 时间={}, 未读数={}, 未处理数={}",
            event.getNotificationId(), event.getType(), event.getExpireTime(),
            event.getUnreadCount(), event.getUnprocessedCount());
    }

    @Override
    public void onNotificationCancelled(NotificationCallbackEvent.Cancelled event) {
        log.info("通知已取消: ID={}, 类型={}, 时间={}, 原因={}, 取消人={}",
            event.getNotificationId(), event.getType(), event.getCancelTime(),
            event.getCancelReason(), event.getCancelUser());
    }
}
