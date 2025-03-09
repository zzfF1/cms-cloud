package com.sinosoft.notice.core.exception;

/**
 * 通知错误码常量类
 */
public class NotificationErrorCode {
    // 常规错误
    public static final String SUCCESS = "00000";
    public static final String SYSTEM_ERROR = "B0001";
    public static final String BUSINESS_ERROR = "A0001";

    // 通知模板相关错误
    public static final String TEMPLATE_NOT_FOUND = "A1001";
    public static final String TEMPLATE_DISABLED = "A1002";
    public static final String TEMPLATE_RENDER_ERROR = "A1003";
    public static final String TEMPLATE_PARAM_ERROR = "A1004";

    // 通知发送相关错误
    public static final String NOTIFICATION_SEND_ERROR = "A2001";
    public static final String NOTIFICATION_CHANNEL_ERROR = "A2002";
    public static final String SMS_SEND_ERROR = "A2003";
    public static final String EMAIL_SEND_ERROR = "A2004";

    // 接收人相关错误
    public static final String RECIPIENT_FIND_ERROR = "A3001";
    public static final String RECIPIENT_EMPTY_ERROR = "A3002";

    // 通知合并相关错误
    public static final String MERGE_STRATEGY_ERROR = "A4001";

    // 用户设置相关错误
    public static final String USER_SETTING_ERROR = "A5001";

    // 其他错误
    public static final String UNKNOWN_ERROR = "Z9999";
}
