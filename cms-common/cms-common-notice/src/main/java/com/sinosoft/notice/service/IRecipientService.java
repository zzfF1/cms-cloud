package com.sinosoft.notice.service;

import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IRecipientService {
    List<Long> findRecipients(SysNotification notification, SysNotificationTemplate template, List<Long> specificUserIds);

    @Transactional
    void createNotificationUserRecords(Long notificationId, List<Long> userIds);
}
