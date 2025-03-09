package com.sinosoft.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.sinosoft.notice.core.callback.NotificationCallbackEvent;
import com.sinosoft.notice.core.callback.NotificationCallbackManager;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationDelivery;
import com.sinosoft.notice.mapper.SysNotificationUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 通知回调服务
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationCallbackService {

    private final NotificationCallbackManager callbackManager;
    private final SysNotificationUserMapper notificationUserMapper;
    private final SseNotificationService sseNotificationService;

    /**
     * 处理通知创建回调
     *
     * @param notification   通知对象
     * @param recipientCount 接收人数量
     * @param templateCode   模板代码
     */
    public void handleNotificationCreated(SysNotification notification, int recipientCount, String templateCode) {
        try {
            NotificationCallbackEvent.Created event = new NotificationCallbackEvent.Created();
            event.setNotificationId(notification.getNotificationId());
            event.setTemplateId(notification.getTemplateId());
            event.setTemplateCode(templateCode);
            event.setType(notification.getType());
            event.setBusinessKey(notification.getBusinessKey());

            // 解析业务数据
            if (notification.getBusinessData() != null && !notification.getBusinessData().isEmpty()) {
                event.setBusinessData(JSON.parseObject(notification.getBusinessData()));
            }

            event.setRecipientCount(recipientCount);
            event.setCreateTime(notification.getCreateTime());

            // 触发回调
            callbackManager.fireNotificationCreated(event);
        } catch (Exception e) {
            log.error("处理通知创建回调异常, 通知ID: {}", notification.getNotificationId(), e);
        }
    }

    /**
     * 处理通知读取回调
     *
     * @param notification 通知对象
     * @param userId       用户ID
     * @param readTime     读取时间
     */
    public void handleNotificationRead(SysNotification notification, Long userId, Date readTime) {
        try {
            NotificationCallbackEvent.Read event = new NotificationCallbackEvent.Read();
            event.setNotificationId(notification.getNotificationId());
            event.setUserId(userId);
            event.setType(notification.getType());
            event.setBusinessKey(notification.getBusinessKey());
            event.setReadTime(readTime);

            // 触发回调
            callbackManager.fireNotificationRead(event);
        } catch (Exception e) {
            log.error("处理通知读取回调异常, 通知ID: {}, 用户ID: {}",
                notification.getNotificationId(), userId, e);
        }
    }

    /**
     * 处理通知处理回调
     *
     * @param notification  通知对象
     * @param userId        用户ID
     * @param processTime   处理时间
     * @param processNote   处理备注
     * @param processResult 处理结果
     */
    public void handleNotificationProcessed(SysNotification notification, Long userId,
                                            Date processTime, String processNote, String processResult) {
        try {
            NotificationCallbackEvent.Processed event = new NotificationCallbackEvent.Processed();
            event.setNotificationId(notification.getNotificationId());
            event.setUserId(userId);
            event.setType(notification.getType());
            event.setBusinessKey(notification.getBusinessKey());
            event.setProcessTime(processTime);
            event.setProcessNote(processNote);
            event.setProcessResult(processResult);

            // 触发回调
            callbackManager.fireNotificationProcessed(event);
        } catch (Exception e) {
            log.error("处理通知处理回调异常, 通知ID: {}, 用户ID: {}",
                notification.getNotificationId(), userId, e);
        }
    }

    /**
     * 处理通知发送回调
     *
     * @param delivery 发送记录
     */
    public void handleNotificationSent(SysNotificationDelivery delivery) {
        try {
            NotificationCallbackEvent.Sent event = new NotificationCallbackEvent.Sent();
            event.setNotificationId(delivery.getNotificationId());
            event.setUserId(delivery.getUserId());
            event.setChannel(delivery.getChannel());
            event.setTargetAddress(delivery.getTargetAddress());
            event.setSendTime(delivery.getSendTime());
            event.setDeliveryId(delivery.getId());

            // 触发回调
            callbackManager.fireNotificationSent(event);
        } catch (Exception e) {
            log.error("处理通知发送回调异常, 发送记录ID: {}", delivery.getId(), e);
        }
    }

    /**
     * 处理通知发送失败回调
     *
     * @param delivery     发送记录
     * @param errorMessage 错误信息
     */
    public void handleNotificationSendFailed(SysNotificationDelivery delivery, String errorMessage) {
        try {
            NotificationCallbackEvent.SendFailed event = new NotificationCallbackEvent.SendFailed();
            event.setNotificationId(delivery.getNotificationId());
            event.setUserId(delivery.getUserId());
            event.setChannel(delivery.getChannel());
            event.setTargetAddress(delivery.getTargetAddress());
            event.setFailTime(delivery.getLastRetryTime() != null ?
                delivery.getLastRetryTime() : new Date());
            event.setErrorMessage(errorMessage);
            event.setRetryCount(delivery.getRetryCount());
            event.setDeliveryId(delivery.getId());

            // 触发回调
            callbackManager.fireNotificationSendFailed(event);
        } catch (Exception e) {
            log.error("处理通知发送失败回调异常, 发送记录ID: {}", delivery.getId(), e);
        }
    }

    /**
     * 处理通知过期回调
     *
     * @param notification 通知对象
     */
    @Transactional(readOnly = true)
    public void handleNotificationExpired(SysNotification notification) {
        try {
            // 查询未读和未处理数量
            int unreadCount = notificationUserMapper.countByNotificationIdAndIsRead(
                notification.getNotificationId(), "0");
            int unprocessedCount = 0;
            if ("todo".equals(notification.getType())) {
                unprocessedCount = notificationUserMapper.countByNotificationIdAndIsProcessed(
                    notification.getNotificationId(), "0");
            }

            NotificationCallbackEvent.Expired event = new NotificationCallbackEvent.Expired();
            event.setNotificationId(notification.getNotificationId());
            event.setType(notification.getType());
            event.setBusinessKey(notification.getBusinessKey());
            event.setExpireTime(notification.getExpirationDate());
            event.setUnreadCount(unreadCount);
            event.setUnprocessedCount(unprocessedCount);

            // 触发回调
            callbackManager.fireNotificationExpired(event);
        } catch (Exception e) {
            log.error("处理通知过期回调异常, 通知ID: {}", notification.getNotificationId(), e);
        }
    }

    /**
     * 处理通知取消回调
     *
     * @param notification 通知对象
     * @param cancelReason 取消原因
     * @param cancelUser   取消用户
     */
    public void handleNotificationCancelled(SysNotification notification,
                                            String cancelReason, Long cancelUser) {
        try {
            NotificationCallbackEvent.Cancelled event = new NotificationCallbackEvent.Cancelled();
            event.setNotificationId(notification.getNotificationId());
            event.setType(notification.getType());
            event.setBusinessKey(notification.getBusinessKey());
            event.setCancelTime(new Date());
            event.setCancelReason(cancelReason);
            event.setCancelUser(cancelUser);

            // 触发回调
            callbackManager.fireNotificationCancelled(event);
        } catch (Exception e) {
            log.error("处理通知取消回调异常, 通知ID: {}", notification.getNotificationId(), e);
        }
    }
}
