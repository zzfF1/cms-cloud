package com.sinosoft.notice.service;

import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.model.NotificationPayload;

import java.util.List;
import java.util.Map;

/**
 * 通知服务接口
 */
public interface INotificationService {

    /**
     * 发送通知（根据模板规则自动确定接收人）
     *
     * @param templateCode 模板代码
     * @param payload      通知业务负载
     * @param sourceType   来源类型
     * @param sourceId     来源ID
     * @return 通知ID
     */
    Long sendNotification(String templateCode, NotificationPayload payload,
                          String sourceType, String sourceId);

    /**
     * 发送通知（指定接收人）
     *
     * @param templateCode    模板代码
     * @param payload         通知业务负载
     * @param sourceType      来源类型
     * @param sourceId        来源ID
     * @param specificUserIds 指定用户IDs
     * @return 通知ID
     */
    Long sendNotification(String templateCode, NotificationPayload payload,
                          String sourceType, String sourceId, List<Long> specificUserIds);

    /**
     * 获取用户通知列表
     *
     * @param userId   用户ID
     * @param type     通知类型（可选）
     * @param isRead   是否已读（可选）
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 通知列表
     */
    List<SysNotification> getUserNotifications(Long userId, String type, Boolean isRead, int pageNum, int pageSize);

    /**
     * 标记通知为已读
     *
     * @param userId         用户ID
     * @param notificationId 通知ID
     * @return 是否成功
     */
    boolean markAsRead(Long userId, Long notificationId);

    /**
     * 标记所有通知为已读
     *
     * @param userId 用户ID
     * @return 更新的通知数量
     */
    int markAllAsRead(Long userId);

    /**
     * 获取未读通知数量
     *
     * @param userId 用户ID
     * @return 各类型未读通知数量
     */
    Map<String, Integer> getUnreadCount(Long userId);
}
