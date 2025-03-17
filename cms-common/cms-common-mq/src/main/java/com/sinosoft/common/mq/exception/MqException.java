package com.sinosoft.common.mq.exception;

/**
 * MQ异常类
 */
public class MqException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String code;

    public MqException(String message) {
        super(message);
        this.code = "MQ-500";
    }

    public MqException(String code, String message) {
        super(message);
        this.code = code;
    }

    public MqException(String message, Throwable cause) {
        super(message, cause);
        this.code = "MQ-500";
    }

    public MqException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
