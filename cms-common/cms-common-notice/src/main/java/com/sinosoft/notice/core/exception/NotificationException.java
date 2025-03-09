package com.sinosoft.notice.core.exception;


import lombok.Getter;

/**
 * 通知系统异常基类
 */
@Getter
public class NotificationException extends RuntimeException {

    /**
     * 错误码
     */
    private final String errorCode;

    /**
     * 错误消息
     */
    private final String errorMessage;

    /**
     * 构造方法
     *
     * @param errorCode    错误码
     * @param errorMessage 错误消息
     */
    public NotificationException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 构造方法
     *
     * @param errorCode    错误码
     * @param errorMessage 错误消息
     * @param cause        原因
     */
    public NotificationException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
