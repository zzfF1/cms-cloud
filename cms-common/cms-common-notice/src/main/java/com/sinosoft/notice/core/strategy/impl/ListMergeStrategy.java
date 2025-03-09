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

import java.util.*;

/**
 * 列表合并策略
 */
@Slf4j
@Component
public class ListMergeStrategy implements NotificationMergeStrategy {

    private final SysNotificationMapper notificationMapper;
    private final SysNotificationUserMapper notificationUserMapper;
    private final SysNotificationMergeMapper mergeMapper;
    private final TemplateEngine templateEngine;

    public ListMergeStrategy(SysNotificationMapper notificationMapper,
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
        return "list";
    }

    @Override
    public String getStrategyName() {
        return "列表合并";
    }

    @Override
    public SysNotification merge(SysNotification existingNotification,
                                 SysNotification newNotification,
                                 SysNotificationTemplate template,
                                 NotificationPayload payload) {
        // 1. 检查是否已达到最大合并数量
        List<SysNotificationMerge> details = mergeMapper.selectByParentId(
            existingNotification.getNotificationId());

        int maxMergeCount = getMaxMergeCount(template);
        if (details.size() >= maxMergeCount) {
            // 已达最大合并数量，创建新通知
            log.info("已达到最大合并数量 {}, 创建新通知", maxMergeCount);
            return newNotification;
        }

        // 2. 更新计数和合并标记
        int currentCount = existingNotification.getMergedCount() != null ?
            existingNotification.getMergedCount() : 1;
        existingNotification.setMergedCount(currentCount + 1);
        existingNotification.setIsMerged("1");

        // 3. 准备合并数据
        Map<String, Object> mergeData = payload.toTemplateParams();
        mergeData.put("count", currentCount + 1);

        // 4. 更新标题
        updateMergedTitleForList(existingNotification, template, mergeData, payload);

        // 5. 查找所有子通知以构建列表内容
        List<SysNotification> childNotifications = findChildNotifications(existingNotification, details);

        // 添加新通知到列表
        childNotifications.add(newNotification);

        // 6. 构建列表内容
        updateContentForListMerge(existingNotification, childNotifications, payload);

        // 7. 保留最高优先级
        if ("high".equals(newNotification.getPriority()) && !"high".equals(existingNotification.getPriority())) {
            existingNotification.setPriority("high");
        }

        // 8. 保留最晚的过期时间
        updateExpirationDate(existingNotification, newNotification);

        // 9. 更新业务数据
        updateBusinessDataForList(existingNotification, currentCount + 1);

        // 10. 标记所有接收记录为未读
        notificationUserMapper.markAllUnread(existingNotification.getNotificationId());

        // 11. 保存子通知
        newNotification.setParentId(existingNotification.getNotificationId());
        notificationMapper.insert(newNotification);

        // 12. 创建合并详情记录
        createMergeDetail(existingNotification, newNotification, currentCount + 1);

        // 13. 更新主通知
        notificationMapper.updateById(existingNotification);
        log.info("合并通知(列表模式)，主通知ID: {}, 子通知ID: {}, 合并后数量: {}",
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
     * 获取最大合并数量
     */
    private int getMaxMergeCount(SysNotificationTemplate template) {
        // 从模板或配置中获取最大合并数量
        // 这里简单返回固定值，实际应从模板或配置中读取
        return 10;
    }

    /**
     * 更新列表合并标题
     */
    private void updateMergedTitleForList(
        SysNotification notification, SysNotificationTemplate template,
        Map<String, Object> mergeData, NotificationPayload payload) {

        // 优先使用负载对象中的合并标题模板
        String mergeTitleTemplate = payload.getMergeTitleTemplate();

        // 如果负载对象中没有，则使用模板中的
        if (isEmpty(mergeTitleTemplate) && !isEmpty(template.getMergeTitleTpl())) {
            mergeTitleTemplate = template.getMergeTitleTpl();
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
    }

    /**
     * 查找子通知
     */
    private List<SysNotification> findChildNotifications(
        SysNotification parentNotification, List<SysNotificationMerge> details) {

        if (details.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取子通知ID列表
        List<Long> childIds = new ArrayList<>();
        for (SysNotificationMerge detail : details) {
            childIds.add(detail.getChildId());
        }

        // 查询子通知
        return notificationMapper.selectBatchIds(childIds);
    }

    /**
     * 更新列表合并内容
     */
    private void updateContentForListMerge(
        SysNotification parentNotification, List<SysNotification> childNotifications,
        NotificationPayload payload) {

        StringBuilder listContent = new StringBuilder();
        int count = childNotifications.size();

        String businessType = payload.getBusinessType();
        if (isEmpty(businessType)) {
            // 尝试从业务数据中获取
            Map<String, Object> templateParams = payload.toTemplateParams();
            if (templateParams.containsKey("businessTypeName")) {
                businessType = templateParams.get("businessTypeName").toString();
            } else {
                businessType = "业务";
            }
        }

        listContent.append("您有").append(count).append("条").append(businessType).append("需要处理：\n\n");

        // 添加子通知标题列表
        for (int i = 0; i < childNotifications.size(); i++) {
            SysNotification child = childNotifications.get(i);
            listContent.append(i + 1).append(". ").append(child.getTitle()).append("\n");
        }

        // 添加操作提示
        listContent.append("\n请及时处理，谢谢！");

        parentNotification.setContent(listContent.toString());
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
     * 更新列表合并业务数据
     */
    private void updateBusinessDataForList(SysNotification existingNotification, int mergedCount) {
        try {
            JSONObject existingData = null;
            if (!isEmpty(existingNotification.getBusinessData())) {
                existingData = JSON.parseObject(existingNotification.getBusinessData());
            }

            if (existingData == null) {
                existingData = new JSONObject();
            }

            // 添加列表合并信息
            existingData.put("itemCount", mergedCount);
            existingData.put("lastUpdatedAt", new Date());

            existingNotification.setBusinessData(existingData.toString());
        } catch (Exception e) {
            log.error("更新列表合并业务数据异常: {}", e.getMessage());
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
