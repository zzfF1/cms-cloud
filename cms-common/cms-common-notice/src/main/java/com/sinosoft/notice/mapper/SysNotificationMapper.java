package com.sinosoft.notice.mapper;


import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.notice.domain.SysNotification;
import org.apache.ibatis.annotations.Param;


import java.util.Date;
import java.util.List;

/**
 * 通知主Mapper接口
 *
 * @author zzf
 * @date 2025-03-07
 */
public interface SysNotificationMapper extends BaseMapperPlus<SysNotification, SysNotification> {

    /**
     * 根据业务键和创建时间查询通知
     * @param businessKey 业务键
     * @param startTime 开始时间
     * @return 通知列表
     */
    List<SysNotification> selectByBusinessKeyAndTime(
            @Param("businessKey") String businessKey,
            @Param("startTime") Date startTime);

    /**
     * 根据业务键、用户ID和创建时间查询通知
     * @param businessKey 业务键
     * @param userId 用户ID
     * @param startTime 开始时间
     * @return 通知列表
     */
    List<SysNotification> selectByBusinessKeyAndUserIdAndTime(
            @Param("businessKey") String businessKey,
            @Param("userId") Long userId,
            @Param("startTime") Date startTime);

    /**
     * 查询已过期但未标记为过期的通知
     * @param now 当前时间
     * @return 过期通知列表
     */
    List<SysNotification> selectExpired(@Param("now") Date now);

    /**
     * 批量更新通知状态
     * @param ids 通知ID列表
     * @param status 状态
     * @return 影响行数
     */
    int updateStatusBatch(@Param("ids") List<Long> ids, @Param("status") String status);

    /**
     * 查询所有正常状态的非高优先级通知
     */
    List<SysNotification> selectNormalNotificationsNotHighPriority();

    /**
     * 查询指定时间范围内创建的通知
     */
    List<SysNotification> selectByCreateTimeRange(
        @Param("startTime") Date startTime,
        @Param("endTime") Date endTime
    );
}
