package com.sinosoft.notice.core.exception;

/**
 * 接收人查找异常
 */
public class RecipientFindException extends NotificationException {
    public RecipientFindException(String message) {
        super("RECIPIENT_FIND_ERROR", "查找接收人异常: " + message);
    }

    public RecipientFindException(String message, Throwable cause) {
        super("RECIPIENT_FIND_ERROR", "查找接收人异常: " + message, cause);
    }
}
