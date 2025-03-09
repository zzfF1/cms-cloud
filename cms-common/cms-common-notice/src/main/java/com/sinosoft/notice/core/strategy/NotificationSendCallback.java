package com.sinosoft.notice.core.strategy;

import com.sinosoft.notice.domain.SysNotificationDelivery;

/**
 * 通知发送回调接口
 */
public interface NotificationSendCallback {

    /**
     * 发送成功回调
     *
     * @param delivery 发送记录
     */
    void onSuccess(SysNotificationDelivery delivery);

    /**
     * 发送失败回调
     *
     * @param delivery     发送记录
     * @param errorMessage 错误信息
     */
    void onFailure(SysNotificationDelivery delivery, String errorMessage);
}
