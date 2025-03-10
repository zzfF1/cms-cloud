package com.sinosoft.notice.service;

import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IRecipientService {

    /**
     * 查询通知的接收者
     *
     * @param notification
     * @param template
     * @param specificUserIds
     * @return
     */
    List<Long> findRecipients(SysNotification notification, SysNotificationTemplate template, List<Long> specificUserIds);

    /**
     * 创建通知接收者记录
     *
     * @param notificationId
     * @param userIds
     */
    void createNotificationUserRecords(Long notificationId, List<Long> userIds);
}
