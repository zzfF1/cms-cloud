package com.sinosoft.notice.mapper;


import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.notice.domain.SysNotificationDelivery;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 通知发送记录Mapper接口
 *
 * @author zzf
 * @date 2025-03-07
 */
public interface SysNotificationDeliveryMapper extends BaseMapperPlus<SysNotificationDelivery, SysNotificationDelivery> {

    /**
     * 查询待处理的短信通知
     *
     * @param limit 限制数量
     * @return 短信通知列表
     */
    List<SysNotificationDelivery> selectPendingSms(@Param("limit") int limit);

    /**
     * 查询待处理的邮件通知
     *
     * @param limit 限制数量
     * @return 邮件通知列表
     */
    List<SysNotificationDelivery> selectPendingEmails(@Param("limit") int limit);

    /**
     * 根据通知ID查询发送记录
     *
     * @param notificationId 通知ID
     * @return 发送记录列表
     */
    List<SysNotificationDelivery> selectByNotificationId(Long notificationId);

    /**
     * 根据用户ID查询发送记录
     *
     * @param userId 用户ID
     * @return 发送记录列表
     */
    List<SysNotificationDelivery> selectByUserId(Long userId);


    /**
     * 查询通知-用户对应关系
     *
     * @param notificationIds 通知ID列表
     * @return 用户-通知映射列表
     */
    List<Map<String, Object>> selectUserNotificationMappings(
        @Param("notificationIds") List<Long> notificationIds);
}
