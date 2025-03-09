package com.sinosoft.notice.core.service;

import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import com.sinosoft.notice.model.NotificationPayload;

import java.util.List;
import java.util.Date;

/**
 * 通知合并领域服务
 */
public interface NotificationMergeDomainService {

    /**
     * 处理通知合并
     *
     * @param template     通知模板
     * @param notification 待处理的通知
     * @param payload      通知业务负载
     * @param userId       用户ID（可选）
     * @return 处理后的通知
     */
    SysNotification mergeNotification(SysNotificationTemplate template,
                                      SysNotification notification,
                                      NotificationPayload payload,
                                      Long userId);

    /**
     * 判断通知是否需要合并
     *
     * @param template 通知模板
     * @param payload  通知业务负载
     * @return 是否需要合并
     */
    boolean shouldMerge(SysNotificationTemplate template, NotificationPayload payload);

    /**
     * 生成合并标题
     *
     * @param notification 原通知
     * @param template     通知模板
     * @param payload      业务负载
     * @param count        合并数量
     * @return 合并后的标题
     */
    String generateMergedTitle(SysNotification notification,
                               SysNotificationTemplate template,
                               NotificationPayload payload,
                               int count);

    /**
     * 生成合并内容
     *
     * @param notification 原通知
     * @param template     通知模板
     * @param payload      业务负载
     * @param items        子项通知列表
     * @return 合并后的内容
     */
    String generateMergedContent(SysNotification notification,
                                 SysNotificationTemplate template,
                                 NotificationPayload payload,
                                 List<SysNotification> items);
}
