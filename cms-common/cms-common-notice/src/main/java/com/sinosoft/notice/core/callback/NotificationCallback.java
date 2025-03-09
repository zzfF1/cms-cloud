package com.sinosoft.notice.core.callback;

/**
 * 通知回调接口
 */
public interface NotificationCallback {

    /**
     * 获取回调类型
     *
     * @return 回调类型
     */
    String getCallbackType();

    /**
     * 通知创建回调
     *
     * @param event 通知创建事件
     */
    void onNotificationCreated(NotificationCallbackEvent.Created event);

    /**
     * 通知读取回调
     *
     * @param event 通知读取事件
     */
    void onNotificationRead(NotificationCallbackEvent.Read event);

    /**
     * 通知处理回调
     *
     * @param event 通知处理事件
     */
    void onNotificationProcessed(NotificationCallbackEvent.Processed event);

    /**
     * 通知发送回调
     *
     * @param event 通知发送事件
     */
    void onNotificationSent(NotificationCallbackEvent.Sent event);

    /**
     * 通知发送失败回调
     *
     * @param event 通知发送失败事件
     */
    void onNotificationSendFailed(NotificationCallbackEvent.SendFailed event);

    /**
     * 通知过期回调
     *
     * @param event 通知过期事件
     */
    void onNotificationExpired(NotificationCallbackEvent.Expired event);

    /**
     * 通知取消回调
     *
     * @param event 通知取消事件
     */
    void onNotificationCancelled(NotificationCallbackEvent.Cancelled event);
}
