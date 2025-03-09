package com.sinosoft.notice.service;

import com.alibaba.fastjson.JSONObject;
import com.sinosoft.notice.domain.SysNotification;

import java.util.List;

/**
 * @author: zzf
 * @create: 2025-03-07 23:04
 */
public interface ITodoNotificationService {
    List<SysNotification> getTodoNotifications(Long userId, Boolean isProcessed, Integer pageNum, Integer pageSize);

    boolean processTodoNotification(Long userId, Long notificationId, String processNote);

    JSONObject getTodoStatistics(Long userId);
}
