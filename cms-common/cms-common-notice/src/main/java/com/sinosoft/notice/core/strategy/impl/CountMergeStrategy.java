package com.sinosoft.notice.core.strategy.impl;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateEngine;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 计数合并策略
 */
@Slf4j
@Component
public class CountMergeStrategy implements NotificationMergeStrategy {

    private final SysNotificationMapper notificationMapper;
    private final SysNotificationUserMapper notificationUserMapper;
    private final SysNotificationMergeMapper mergeMapper;
    private final TemplateEngine templateEngine;

    public CountMergeStrategy(SysNotificationMapper notificationMapper,
                              SysNotificationUserMapper notificationUserMapper,
                              SysNotificationMergeMapper mergeMapper,
                              TemplateEngine templateEngine) {
        this.notificationMapper = notificationMapper;
        this.notificationUserMapper = notificationUserMapper;
        this.mergeMapper = mergeMapper;
        this.templateEngine = templateEngine;
    }

    @Override
    public String getStrategyCode() {
        return "count";
    }

    @Override
    public String getStrategyName() {
        return "计数合并";
    }

    @Override
    public SysNotification merge(SysNotification existingNotification,
                                 SysNotification newNotification,
                                 SysNotificationTemplate template,
                                 NotificationPayload payload) {
        // 1. 更新计数
        int currentCount = existingNotification.getMergedCount() != null ?
            existingNotification.getMergedCount() : 1;
        existingNotification.setMergedCount(currentCount + 1);
        existingNotification.setIsMerged("1");

        // 2. 准备合并数据
        Map<String, Object> mergeData = payload.toTemplateParams();
        mergeData.put("count", currentCount + 1);

        // 3. 更新标题和内容（使用合并模板）
        updateMergedTitleAndContent(existingNotification, template, mergeData, payload);

        // 4. 保留最高优先级
        if ("high".equals(newNotification.getPriority()) && !"high".equals(existingNotification.getPriority())) {
            existingNotification.setPriority("high");
        }

        // 5. 保留最晚的过期时间
        updateExpirationDate(existingNotification, newNotification);

        // 6. 更新业务数据（合并或使用最新的）
        updateBusinessData(existingNotification, newNotification, currentCount + 1);

        // 7. 将所有接收记录标记为未读
        notificationUserMapper.markAllUnread(existingNotification.getNotificationId());

        // 8. 保存子通知记录
        newNotification.setParentId(existingNotification.getNotificationId());
        notificationMapper.insert(newNotification);

        // 9. 创建合并详情记录
        createMergeDetail(existingNotification, newNotification, currentCount + 1);

        // 10. 更新主通知
        notificationMapper.updateById(existingNotification);
        log.info("合并通知(计数模式)，主通知ID: {}, 子通知ID: {}, 合并后数量: {}",
            existingNotification.getNotificationId(), newNotification.getNotificationId(), currentCount + 1);

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

    /**
     * 更新合并后的标题和内容
     */
    private void updateMergedTitleAndContent(
        SysNotification notification, SysNotificationTemplate template,
        Map<String, Object> mergeData, NotificationPayload payload) {

        // 优先使用负载对象中的合并模板
        String mergeTitleTemplate = payload.getMergeTitleTemplate();
        String mergeContentTemplate = payload.getMergeContentTemplate();

        // 如果负载对象中没有，则使用模板中的
        if (isEmpty(mergeTitleTemplate) && !isEmpty(template.getMergeTitleTpl())) {
            mergeTitleTemplate = template.getMergeTitleTpl();
        }

        if (isEmpty(mergeContentTemplate) && !isEmpty(template.getMergeContentTpl())) {
            mergeContentTemplate = template.getMergeContentTpl();
        }

        // 更新标题
        if (!isEmpty(mergeTitleTemplate)) {
            try {
                Template titleTpl = templateEngine.getTemplate(mergeTitleTemplate);
                String mergedTitle = titleTpl.render(mergeData);
                notification.setTitle(mergedTitle);
            } catch (Exception e) {
                log.error("渲染合并标题异常: {}", e.getMessage());
                // 使用默认标题格式
                notification.setTitle(notification.getTitle() + " (" + mergeData.get("count") + "项)");
            }
        } else {
            // 使用默认标题格式
            notification.setTitle(notification.getTitle() + " (" + mergeData.get("count") + "项)");
        }

        // 更新内容
        if (!isEmpty(mergeContentTemplate)) {
            try {
                Template contentTpl = templateEngine.getTemplate(mergeContentTemplate);
                String mergedContent = contentTpl.render(mergeData);
                notification.setContent(mergedContent);
            } catch (Exception e) {
                log.error("渲染合并内容异常: {}", e.getMessage());
                // 保留原内容
            }
        }
    }

    /**
     * 更新过期时间
     */
    private void updateExpirationDate(SysNotification existingNotification, SysNotification newNotification) {
        if (newNotification.getExpirationDate() != null &&
            (existingNotification.getExpirationDate() == null ||
                newNotification.getExpirationDate().after(existingNotification.getExpirationDate()))) {
            existingNotification.setExpirationDate(newNotification.getExpirationDate());
        }
    }

    /**
     * 更新业务数据
     */
    private void updateBusinessData(SysNotification existingNotification, SysNotification newNotification, int mergedCount) {
        try {
            JSONObject existingData = null;
            if (!isEmpty(existingNotification.getBusinessData())) {
                existingData = JSON.parseObject(existingNotification.getBusinessData());
            }

            if (existingData == null) {
                existingData = new JSONObject();
            }

            // 合并新通知的业务数据
            if (!isEmpty(newNotification.getBusinessData())) {
                JSONObject newData = JSON.parseObject(newNotification.getBusinessData());
                if (newData != null) {
                    existingData.putAll(newData);
                }
            }

            // 添加合并信息
            existingData.put("mergedCount", mergedCount);
            existingData.put("lastMergedAt", new Date());

            existingNotification.setBusinessData(existingData.toString());
        } catch (Exception e) {
            log.error("合并业务数据异常: {}", e.getMessage());
            existingNotification.setBusinessData(newNotification.getBusinessData());
        }
    }

    /**
     * 创建合并详情记录
     */
    private void createMergeDetail(SysNotification parentNotification, SysNotification childNotification, int displayOrder) {
        SysNotificationMerge mergeDetail = new SysNotificationMerge();
        mergeDetail.setId(System.currentTimeMillis() + new Random().nextInt(1000)); // 简单ID生成
        mergeDetail.setParentId(parentNotification.getNotificationId());
        mergeDetail.setChildId(childNotification.getNotificationId());
        mergeDetail.setMergedAt(new Date());
        mergeDetail.setDisplayOrder(displayOrder);
        mergeMapper.insert(mergeDetail);
    }

    /**
     * 判断字符串是否为空
     */
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
