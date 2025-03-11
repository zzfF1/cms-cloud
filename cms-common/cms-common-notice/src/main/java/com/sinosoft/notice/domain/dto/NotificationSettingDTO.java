package com.sinosoft.notice.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知设置数据传输对象
 *
 * @author zzf
 */
@Data
@NoArgsConstructor
public class NotificationSettingDTO {

    /**
     * 待办通知-系统通知开关（0关闭 1开启）
     */
    private Boolean todoNotifySystem;

    /**
     * 待办通知-短信通知开关（0关闭 1开启）
     */
    private Boolean todoNotifySms;

    /**
     * 待办通知-邮件通知开关（0关闭 1开启）
     */
    private Boolean todoNotifyEmail;

    /**
     * 预警通知-系统通知开关（0关闭 1开启）
     */
    private Boolean alertNotifySystem;

    /**
     * 预警通知-短信通知开关（0关闭 1开启）
     */
    private Boolean alertNotifySms;

    /**
     * 预警通知-邮件通知开关（0关闭 1开启）
     */
    private Boolean alertNotifyEmail;

    /**
     * 公告通知-系统通知开关（0关闭 1开启）
     */
    private Boolean announceNotifySystem;

    /**
     * 公告通知-邮件通知开关（0关闭 1开启）
     */
    private Boolean announceNotifyEmail;

    /**
     * 免打扰开始时间
     */
    private String doNotDisturbStart;

    /**
     * 免打扰结束时间
     */
    private String doNotDisturbEnd;

    /**
     * 转换为Map对象
     */
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        // 待办通知设置
        result.put("todoNotifySystem", booleanToString(todoNotifySystem));
        result.put("todoNotifySms", booleanToString(todoNotifySms));
        result.put("todoNotifyEmail", booleanToString(todoNotifyEmail));

        // 预警通知设置
        result.put("alertNotifySystem", booleanToString(alertNotifySystem));
        result.put("alertNotifySms", booleanToString(alertNotifySms));
        result.put("alertNotifyEmail", booleanToString(alertNotifyEmail));

        // 公告通知设置
        result.put("announceNotifySystem", booleanToString(announceNotifySystem));
        result.put("announceNotifyEmail", booleanToString(announceNotifyEmail));

        // 免打扰时间
        result.put("doNotDisturbStart", doNotDisturbStart);
        result.put("doNotDisturbEnd", doNotDisturbEnd);

        return result;
    }

    /**
     * 布尔值转换为字符串("1"/"0")
     */
    private String booleanToString(Boolean value) {
        return Boolean.TRUE.equals(value) ? "1" : "0";
    }
}
