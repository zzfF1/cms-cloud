package com.sinosoft.notice.service;

import java.util.Map;

/**
 * 通知设置服务接口
 */
public interface INotificationSettingService {
    /**
     * 获取用户通知设置
     *
     * @param userId 用户ID
     * @return 通知设置信息
     */
    Map<String, Object> getUserSettings(Long userId);

    /**
     * 更新用户通知设置
     *
     * @param userId   用户ID
     * @param settings 设置信息
     * @return 是否更新成功
     */
    boolean updateUserSettings(Long userId, Map<String, Object> settings);

    /**
     * 获取默认通知设置
     *
     * @return 默认设置
     */
    Map<String, Object> getDefaultSettings();
}
