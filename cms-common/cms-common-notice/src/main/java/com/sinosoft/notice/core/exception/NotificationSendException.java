package com.sinosoft.notice.core.exception;


/**
 * 通知发送异常
 */
public class NotificationSendException extends NotificationException {
    public NotificationSendException(String message) {
        super("NOTIFICATION_SEND_ERROR", "发送通知异常: " + message);
    }

    public NotificationSendException(String message, Throwable cause) {
        super("NOTIFICATION_SEND_ERROR", "发送通知异常: " + message, cause);
    }
}
