package com.sinosoft.notice.mapper;


import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.notice.domain.SysNotificationUser;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


/**
 * 通知接收人Mapper接口
 *
 * @author zzf
 * @date 2025-03-07
 */
public interface SysNotificationUserMapper extends BaseMapperPlus<SysNotificationUser, SysNotificationUser> {

    /**
     * 根据通知ID和用户ID查询
     *
     * @param notificationId 通知ID
     * @param userId         用户ID
     * @return 通知用户关联对象
     */
    SysNotificationUser selectByNotificationIdAndUserId(
            @Param("notificationId") Long notificationId,
            @Param("userId") Long userId);

    /**
     * 查询用户通知列表
     *
     * @param query  查询条件
     * @param offset 偏移量
     * @param limit  限制数量
     * @return 通知用户关联列表
     */
    List<SysNotificationUser> selectUserNotifications(
            @Param("query") SysNotificationUser query,
            @Param("offset") int offset,
            @Param("limit") int limit);

    /**
     * 按类型查询用户通知列表
     *
     * @param query  查询条件
     * @param type   通知类型
     * @param offset 偏移量
     * @param limit  限制数量
     * @return 通知用户关联列表
     */
    List<SysNotificationUser> selectUserNotificationsByType(
            @Param("query") SysNotificationUser query,
            @Param("type") String type,
            @Param("offset") int offset,
            @Param("limit") int limit);

    /**
     * 查询用户待办通知列表
     *
     * @param query  查询条件
     * @param offset 偏移量
     * @param limit  限制数量
     * @return 通知用户关联列表
     */
    List<SysNotificationUser> selectTodoNotifications(
            @Param("query") SysNotificationUser query,
            @Param("offset") int offset,
            @Param("limit") int limit);

    /**
     * 标记所有通知为已读
     *
     * @param userId   用户ID
     * @param readTime 阅读时间
     * @return 影响行数
     */
    int markAllAsRead(
            @Param("userId") Long userId,
            @Param("readTime") Date readTime);

    /**
     * 标记通知为未读
     *
     * @param notificationId 通知ID
     * @return 影响行数
     */
    int markAllUnread(
            @Param("notificationId") Long notificationId);

    /**
     * 统计用户未读通知数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    int countUserUnread(
            @Param("userId") Long userId);

    /**
     * 按类型统计用户未读通知数量
     *
     * @param userId 用户ID
     * @param type   通知类型
     * @return 未读数量
     */
    int countUnreadByType(
            @Param("userId") Long userId,
            @Param("type") String type);

    /**
     * 统计用户待办通知数量
     *
     * @param userId      用户ID
     * @param isProcessed 处理状态
     * @return 待办数量
     */
    int countUserTodoByStatus(
            @Param("userId") Long userId,
            @Param("isProcessed") String isProcessed);

    /**
     * 统计用户高优先级待办数量
     *
     * @param userId 用户ID
     * @return 高优先级待办数量
     */
    int countUserHighPriorityTodo(
            @Param("userId") Long userId);

    /**
     * 统计用户即将过期的待办数量
     *
     * @param userId         用户ID
     * @param expirationDate 过期时间
     * @return 即将过期待办数量
     */
    int countUserExpiringTodo(
            @Param("userId") Long userId,
            @Param("expirationDate") Date expirationDate);

    /**
     * 根据通知ID统计接收人数量
     *
     * @param notificationId 通知ID
     * @return 接收人数量
     */
    int countByNotificationId(@Param("notificationId") Long notificationId);

    /**
     * 根据通知ID和读取状态统计接收人数量
     *
     * @param notificationId 通知ID
     * @param isRead 是否已读
     * @return 接收人数量
     */
    int countByNotificationIdAndIsRead(
        @Param("notificationId") Long notificationId,
        @Param("isRead") String isRead);

    /**
     * 根据通知ID和处理状态统计接收人数量
     *
     * @param notificationId 通知ID
     * @param isProcessed 是否已处理
     * @return 接收人数量
     */
    int countByNotificationIdAndIsProcessed(
        @Param("notificationId") Long notificationId,
        @Param("isProcessed") String isProcessed);

    /**
     * 根据通知ID查询用户ID列表
     *
     * @param notificationId 通知ID
     * @return 用户ID列表
     */
    List<Long> selectUserIdsByNotificationId(@Param("notificationId") Long notificationId);
}
