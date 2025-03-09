package com.sinosoft.notice.core.strategy.impl;

import com.sinosoft.notice.core.strategy.NotificationMergeStrategy;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationMerge;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import com.sinosoft.notice.mapper.SysNotificationMapper;
import com.sinosoft.notice.mapper.SysNotificationMergeMapper;
import com.sinosoft.notice.mapper.SysNotificationUserMapper;
import com.sinosoft.notice.model.NotificationPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateEngine;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * 更新合并策略
 */
@Slf4j
@Component
public class UpdateMergeStrategy implements NotificationMergeStrategy {

    private final SysNotificationMapper notificationMapper;
    private final SysNotificationUserMapper notificationUserMapper;

    public UpdateMergeStrategy(SysNotificationMapper notificationMapper,
                               SysNotificationUserMapper notificationUserMapper) {
        this.notificationMapper = notificationMapper;
        this.notificationUserMapper = notificationUserMapper;
    }

    @Override
    public String getStrategyCode() {
        return "update";
    }

    @Override
    public String getStrategyName() {
        return "更新已有";
    }

    @Override
    public SysNotification merge(SysNotification existingNotification,
                                 SysNotification newNotification,
                                 SysNotificationTemplate template,
                                 NotificationPayload payload) {
        // 保留原有ID、创建时间等信息，更新内容
        Long notificationId = existingNotification.getNotificationId();
        Date createTime = existingNotification.getCreateTime();
        Long createBy = existingNotification.getCreateBy();
        Long createDept = existingNotification.getCreateDept();

        // 复制新通知的属性到现有通知
        existingNotification.setTitle(newNotification.getTitle());
        existingNotification.setContent(newNotification.getContent());
        existingNotification.setPriority(newNotification.getPriority());
        existingNotification.setActions(newNotification.getActions());
        existingNotification.setAttachments(newNotification.getAttachments());
        existingNotification.setBusinessData(newNotification.getBusinessData());
        existingNotification.setExpirationDate(newNotification.getExpirationDate());
        existingNotification.setUpdateBy(newNotification.getUpdateBy());
        existingNotification.setUpdateTime(newNotification.getUpdateTime());

        // 恢复原有字段
        existingNotification.setNotificationId(notificationId);
        existingNotification.setCreateTime(createTime);
        existingNotification.setCreateBy(createBy);
        existingNotification.setCreateDept(createDept);

        // 如果原通知已过期，重新激活
        if ("1".equals(existingNotification.getStatus())) {
            existingNotification.setStatus("0");
        }

        // 将所有接收记录标记为未读
        notificationUserMapper.markAllUnread(notificationId);

        // 更新通知
        notificationMapper.updateById(existingNotification);
        log.info("更新现有通知，ID: {}", notificationId);

        return existingNotification;
    }

    @Override
    public List<SysNotification> findMergeableNotifications(String businessKey, Long userId, Date startTime) {
        if (userId != null) {
            return notificationMapper.selectByBusinessKeyAndUserIdAndTime(businessKey, userId, startTime);
        } else {
            return notificationMapper.selectByBusinessKeyAndTime(businessKey, startTime);
        }
    }
}
