package com.sinosoft.notice.service;

import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.notice.core.todo.TodoResult;
import com.sinosoft.notice.model.NotificationPayload;
import com.sinosoft.notice.model.dto.NotificationDTO;
import com.sinosoft.notice.model.dto.NotificationQueryDTO;

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
     * 查询用户通知列表（分页）
     * 支持预警通知、消息通知、公告通知
     *
     * @param queryDTO 查询条件
     * @return 分页通知列表
     */
    TableDataInfo<NotificationDTO> getNotifications(NotificationQueryDTO queryDTO);

    /**
     * 获取用户待办列表（不分页）
     *
     * @param userId 用户ID
     * @return 待办列表
     */
    List<TodoResult> getUserTodoList(Long userId);

    /**
     * 获取用户未读通知统计
     *
     * @param userId 用户ID
     * @return 未读通知统计（按类型）
     */
    Map<String, Integer> getUnreadCount(Long userId);

    /**
     * 标记通知为已读
     *
     * @param userId 用户ID
     * @param notificationId 通知ID
     * @return 是否成功
     */
    boolean markAsRead(Long userId, Long notificationId);

    /**
     * 批量标记通知为已读
     *
     * @param userId 用户ID
     * @param notificationIds 通知ID列表
     * @return 成功标记的数量
     */
    int batchMarkAsRead(Long userId, List<Long> notificationIds);

    /**
     * 标记所有通知为已读
     *
     * @param userId 用户ID
     * @param type 通知类型（可选）
     * @return 更新的通知数量
     */
    int markAllAsRead(Long userId, String type);
}
