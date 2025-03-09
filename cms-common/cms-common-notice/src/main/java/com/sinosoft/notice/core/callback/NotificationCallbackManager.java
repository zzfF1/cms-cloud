package com.sinosoft.notice.core.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知回调管理器
 */
@Slf4j
@Component
public class NotificationCallbackManager {

    /**
     * 回调注册表 (类型 -> 回调实现)
     */
    private final Map<String, NotificationCallback> callbacks = new ConcurrentHashMap<>();

    /**
     * 构造方法，自动注册所有回调实现
     *
     * @param callbackList 回调实现列表
     */
    @Autowired
    public NotificationCallbackManager(List<NotificationCallback> callbackList) {
        if (callbackList != null) {
            for (NotificationCallback callback : callbackList) {
                registerCallback(callback);
            }
        }
    }

    /**
     * 注册回调
     *
     * @param callback 回调实现
     */
    public void registerCallback(NotificationCallback callback) {
        if (callback != null) {
            String type = callback.getCallbackType();
            callbacks.put(type, callback);
            log.info("注册通知回调: {}", type);
        }
    }

    /**
     * 触发通知创建回调
     *
     * @param event 创建事件
     */
    public void fireNotificationCreated(NotificationCallbackEvent.Created event) {
        callbacks.values().forEach(callback -> {
            try {
                callback.onNotificationCreated(event);
            } catch (Exception e) {
                log.error("通知创建回调异常, 类型: {}, 通知ID: {}",
                    callback.getCallbackType(), event.getNotificationId(), e);
            }
        });
    }

    /**
     * 触发通知读取回调
     *
     * @param event 读取事件
     */
    public void fireNotificationRead(NotificationCallbackEvent.Read event) {
        callbacks.values().forEach(callback -> {
            try {
                callback.onNotificationRead(event);
            } catch (Exception e) {
                log.error("通知读取回调异常, 类型: {}, 通知ID: {}",
                    callback.getCallbackType(), event.getNotificationId(), e);
            }
        });
    }

    /**
     * 触发通知处理回调
     *
     * @param event 处理事件
     */
    public void fireNotificationProcessed(NotificationCallbackEvent.Processed event) {
        callbacks.values().forEach(callback -> {
            try {
                callback.onNotificationProcessed(event);
            } catch (Exception e) {
                log.error("通知处理回调异常, 类型: {}, 通知ID: {}",
                    callback.getCallbackType(), event.getNotificationId(), e);
            }
        });
    }

    /**
     * 触发通知发送回调
     *
     * @param event 发送事件
     */
    public void fireNotificationSent(NotificationCallbackEvent.Sent event) {
        callbacks.values().forEach(callback -> {
            try {
                callback.onNotificationSent(event);
            } catch (Exception e) {
                log.error("通知发送回调异常, 类型: {}, 通知ID: {}",
                    callback.getCallbackType(), event.getNotificationId(), e);
            }
        });
    }

    /**
     * 触发通知发送失败回调
     *
     * @param event 发送失败事件
     */
    public void fireNotificationSendFailed(NotificationCallbackEvent.SendFailed event) {
        callbacks.values().forEach(callback -> {
            try {
                callback.onNotificationSendFailed(event);
            } catch (Exception e) {
                log.error("通知发送失败回调异常, 类型: {}, 通知ID: {}",
                    callback.getCallbackType(), event.getNotificationId(), e);
            }
        });
    }

    /**
     * 触发通知过期回调
     *
     * @param event 过期事件
     */
    public void fireNotificationExpired(NotificationCallbackEvent.Expired event) {
        callbacks.values().forEach(callback -> {
            try {
                callback.onNotificationExpired(event);
            } catch (Exception e) {
                log.error("通知过期回调异常, 类型: {}, 通知ID: {}",
                    callback.getCallbackType(), event.getNotificationId(), e);
            }
        });
    }

    /**
     * 触发通知取消回调
     *
     * @param event 取消事件
     */
    public void fireNotificationCancelled(NotificationCallbackEvent.Cancelled event) {
        callbacks.values().forEach(callback -> {
            try {
                callback.onNotificationCancelled(event);
            } catch (Exception e) {
                log.error("通知取消回调异常, 类型: {}, 通知ID: {}",
                    callback.getCallbackType(), event.getNotificationId(), e);
            }
        });
    }
}
