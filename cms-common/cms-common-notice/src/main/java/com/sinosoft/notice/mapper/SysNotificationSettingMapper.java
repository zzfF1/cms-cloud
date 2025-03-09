package com.sinosoft.notice.mapper;


import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.notice.domain.SysNotificationSetting;
import org.apache.ibatis.annotations.Param;

/**
 * 通知设置Mapper接口
 *
 * @author zzf
 * @date 2025-03-07
 */
public interface SysNotificationSettingMapper extends BaseMapperPlus<SysNotificationSetting, SysNotificationSetting> {
    /**
     * 根据用户ID查询设置
     *
     * @param userId 用户ID
     * @return 设置对象
     */
    SysNotificationSetting selectByUserId(
        @Param("userId") Long userId);
}
