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
     * 根据用户ID、通知类型和是否已读查询通知ID列表
     */
    List<Long> selectNotificationIdsByUserIdAndType(
        @Param("userId") Long userId,
        @Param("type") String type,
        @Param("isRead") String isRead,
        @Param("offset") int offset,
        @Param("limit") int limit);

    /**
     * 根据类型统计用户通知数量
     */
    int countUserNotificationsByType(
        @Param("userId") Long userId,
        @Param("type") String type,
        @Param("isRead") String isRead);

    /**
     * 标记指定类型的所有通知为已读
     */
    int markAllAsReadByType(
        @Param("userId") Long userId,
        @Param("type") String type,
        @Param("readTime") Date readTime);
}
