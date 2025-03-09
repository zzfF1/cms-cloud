package com.sinosoft.notice.service;


import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import com.sinosoft.notice.model.NotificationPayload;

public interface INotificationMergeService {
    /**
     * 处理通知合并
     *
     * @param template     通知模板
     * @param payload      通知业务负载
     * @param notification 待处理的通知
     * @param userId       用户ID（可选，为空时按照权限规则匹配用户）
     * @return 处理后的通知
     */
    SysNotification processNotificationMerge(
        SysNotificationTemplate template,
        NotificationPayload payload,
        SysNotification notification,
        Long userId);
}
