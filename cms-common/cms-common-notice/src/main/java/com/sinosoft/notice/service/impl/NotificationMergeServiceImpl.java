package com.sinosoft.notice.service.impl;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateEngine;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationMerge;
import com.sinosoft.notice.domain.SysNotificationMergeConfig;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import com.sinosoft.notice.mapper.SysNotificationMapper;
import com.sinosoft.notice.mapper.SysNotificationMergeConfigMapper;
import com.sinosoft.notice.mapper.SysNotificationMergeMapper;
import com.sinosoft.notice.mapper.SysNotificationUserMapper;
import com.sinosoft.notice.model.NotificationPayload;
import com.sinosoft.notice.service.INotificationMergeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 通知合并服务实现类
 *
 * @author: zzf
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationMergeServiceImpl implements INotificationMergeService {
    private final SysNotificationMapper notificationMapper;
    private final SysNotificationMergeMapper mergeMapper;
    private final SysNotificationMergeConfigMapper mergeConfigMapper;
    private final SysNotificationUserMapper notificationUserMapper;
    private final TemplateEngine templateEngine;

    /**
     * 处理通知合并（使用负载对象）
     *
     * @param template     通知模板
     * @param payload      通知业务负载
     * @param notification 待处理的通知
     * @param userId       用户ID（可选，为空时按照权限规则匹配用户）
     * @return 处理后的通知
     */
    @Transactional
    @Override
    public SysNotification processNotificationMerge(
        SysNotificationTemplate template,
        NotificationPayload payload,
        SysNotification notification,
        Long userId) {

        // 1. 检查是否需要进行合并处理
        String mergeStrategy = getMergeStrategy(template, payload);
        if (!"update".equals(mergeStrategy) &&
            !"count".equals(mergeStrategy) &&
            !"list".equals(mergeStrategy)) {
            log.debug("通知不需要合并处理, mergeStrategy: {}", mergeStrategy);
            return notification;
        }

        // 2. 生成业务键
        String businessKey = generateBusinessKey(template, payload);
        if (StringUtils.isEmpty(businessKey)) {
            log.debug("无法生成有效的业务键，跳过合并处理");
            return notification;
        }

        // 设置业务键
        notification.setBusinessKey(businessKey);

        // 3. 获取合并配置
        SysNotificationMergeConfig mergeConfig = getOrCreateMergeConfig(template.getTemplateCode());

        // 4. 查找符合合并条件的现有通知
        Date mergeWindowStart = calculateMergeWindowStart(mergeConfig);
        List<SysNotification> existingNotifications = findExistingNotifications(
            businessKey, userId, mergeConfig, mergeWindowStart);

        // 5. 如果没有找到可合并的通知，直接返回原通知
        if (existingNotifications == null || existingNotifications.isEmpty()) {
            log.debug("未找到可合并的通知，创建新通知");
            return notification;
        }

        // 取最近的一条通知作为基准
        SysNotification existingNotification = existingNotifications.get(0);
        log.debug("找到可合并的通知，ID: {}", existingNotification.getNotificationId());

        // 6. 根据合并策略执行不同的处理
        switch (mergeStrategy) {
            case "update":
                return updateExistingNotification(existingNotification, notification);
            case "count":
                return mergeWithCount(existingNotification, notification, template, payload);
            case "list":
                return mergeAsList(existingNotification, notification, template, payload, mergeConfig);
            default:
                return notification;
        }
    }

    /**
     * 获取合并策略
     */
    private String getMergeStrategy(SysNotificationTemplate template, NotificationPayload payload) {
        // 优先使用负载对象中的策略
        if (StringUtils.isNotEmpty(payload.getMergeStrategy())) {
            return payload.getMergeStrategy();
        }

        // 其次使用模板中的策略
        if (template != null && StringUtils.isNotEmpty(template.getMergeStrategy())) {
            return template.getMergeStrategy();
        }

        // 默认不合并
        return "none";
    }

    /**
     * 获取或创建合并配置
     */
    private SysNotificationMergeConfig getOrCreateMergeConfig(String templateCode) {
        SysNotificationMergeConfig mergeConfig = mergeConfigMapper.selectByTemplateCode(templateCode);

        if (mergeConfig == null) {
            mergeConfig = createDefaultConfig(templateCode);
        }

        return mergeConfig;
    }

    /**
     * 计算合并时间窗口起始时间
     */
    private Date calculateMergeWindowStart(SysNotificationMergeConfig mergeConfig) {
        return Date.from(
            LocalDateTime.now()
                .minusHours(mergeConfig.getMergeWithinHours())
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    /**
     * 查找现有可合并通知
     */
    private List<SysNotification> findExistingNotifications(
        String businessKey, Long userId, SysNotificationMergeConfig mergeConfig, Date mergeWindowStart) {

        List<SysNotification> existingNotifications;

        // 根据配置决定是否按用户维度合并
        if ("1".equals(mergeConfig.getMergeByUser()) && userId != null) {
            existingNotifications = notificationMapper.selectByBusinessKeyAndUserIdAndTime(
                businessKey,
                userId,
                mergeWindowStart);
        } else {
            existingNotifications = notificationMapper.selectByBusinessKeyAndTime(
                businessKey,
                mergeWindowStart);
        }

        return existingNotifications;
    }

    /**
     * 生成业务键
     */
    private String generateBusinessKey(SysNotificationTemplate template, NotificationPayload payload) {
        // 优先使用负载对象中的业务键或生成的业务键
        if (StringUtils.isNotEmpty(payload.getBusinessKey())) {
            return payload.getBusinessKey();
        }

        // 如果负载对象中有业务键模板，使用它生成业务键
        if (StringUtils.isNotEmpty(payload.getBusinessKeyTemplate())) {
            payload.setBusinessKeyTemplate(payload.getBusinessKeyTemplate());
            String generatedKey = payload.generateBusinessKey();
            if (StringUtils.isNotEmpty(generatedKey)) {
                return generatedKey;
            }
        }

        // 否则使用模板中的业务键模板
        if (StringUtils.isEmpty(template.getBusinessKeyTpl())) {
            return null;
        }

        try {
            // 使用模板引擎渲染业务键
            Template tpl = templateEngine.getTemplate(template.getBusinessKeyTpl());
            return tpl.render(payload.toTemplateParams());
        } catch (Exception e) {
            log.error("生成业务键异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 创建默认合并配置
     */
    private SysNotificationMergeConfig createDefaultConfig(String templateCode) {
        SysNotificationMergeConfig config = new SysNotificationMergeConfig();
        config.setId(getNextMergeConfigId());
        config.setTemplateCode(templateCode);
        config.setMergeWithinHours(24);  // 默认24小时内合并
        config.setMaxMergeCount(10);     // 默认最多合并10条
        config.setMergeByUser("1");      // 默认按用户合并
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());

        mergeConfigMapper.insert(config);
        return config;
    }

    /**
     * 获取合并配置ID
     */
    private Long getNextMergeConfigId() {
        // 实际项目中应该使用ID生成器或数据库序列
        return System.currentTimeMillis();
    }

    /**
     * 更新现有通知
     */
    private SysNotification updateExistingNotification(SysNotification existingNotification, SysNotification newNotification) {
        // 保留原有ID、创建时间和租户ID，更新其他内容
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

    /**
     * 计数合并策略实现
     */
    private SysNotification mergeWithCount(
        SysNotification existingNotification,
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

    /**
     * 列表合并策略实现
     */
    private SysNotification mergeAsList(
        SysNotification existingNotification,
        SysNotification newNotification,
        SysNotificationTemplate template,
        NotificationPayload payload,
        SysNotificationMergeConfig mergeConfig) {

        // 1. 检查是否已达到最大合并数量
        List<SysNotificationMerge> details = mergeMapper.selectByParentId(
            existingNotification.getNotificationId());

        if (details.size() >= mergeConfig.getMaxMergeCount()) {
            // 已达最大合并数量，创建新通知
            log.info("已达到最大合并数量 {}, 创建新通知", mergeConfig.getMaxMergeCount());
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

        // 4. 更新标题和内容
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
        if (StringUtils.isEmpty(mergeTitleTemplate) && StringUtils.isNotEmpty(template.getMergeTitleTpl())) {
            mergeTitleTemplate = template.getMergeTitleTpl();
        }

        if (StringUtils.isEmpty(mergeContentTemplate) && StringUtils.isNotEmpty(template.getMergeContentTpl())) {
            mergeContentTemplate = template.getMergeContentTpl();
        }

        // 更新标题
        if (StringUtils.isNotEmpty(mergeTitleTemplate)) {
            Template titleTpl = templateEngine.getTemplate(mergeTitleTemplate);
            String mergedTitle = titleTpl.render(mergeData);
            notification.setTitle(mergedTitle);
        }

        // 更新内容
        if (StringUtils.isNotEmpty(mergeContentTemplate)) {
            Template contentTpl = templateEngine.getTemplate(mergeContentTemplate);
            String mergedContent = contentTpl.render(mergeData);
            notification.setContent(mergedContent);
        }
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
        if (StringUtils.isEmpty(mergeTitleTemplate) && StringUtils.isNotEmpty(template.getMergeTitleTpl())) {
            mergeTitleTemplate = template.getMergeTitleTpl();
        }

        // 更新标题
        if (StringUtils.isNotEmpty(mergeTitleTemplate)) {
            Template titleTpl = templateEngine.getTemplate(mergeTitleTemplate);
            String mergedTitle = titleTpl.render(mergeData);
            notification.setTitle(mergedTitle);
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
        if (StringUtils.isEmpty(businessType)) {
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
     * 更新业务数据
     */
    private void updateBusinessData(SysNotification existingNotification, SysNotification newNotification, int mergedCount) {
        try {
            JSONObject existingData = JSON.parseObject(existingNotification.getBusinessData());
            JSONObject newData = JSON.parseObject(newNotification.getBusinessData());

            // 简单合并，新数据覆盖旧数据
            if (existingData == null) {
                existingData = new JSONObject();
            }

            if (newData != null) {
                existingData.putAll(newData);
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
     * 更新列表合并业务数据
     */
    private void updateBusinessDataForList(SysNotification existingNotification, int mergedCount) {
        try {
            JSONObject existingData = JSON.parseObject(existingNotification.getBusinessData());
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
        mergeDetail.setId(System.currentTimeMillis()); // 实际使用时替换为ID生成器
        mergeDetail.setParentId(parentNotification.getNotificationId());
        mergeDetail.setChildId(childNotification.getNotificationId());
        mergeDetail.setMergedAt(new Date());
        mergeDetail.setDisplayOrder(displayOrder);
        mergeMapper.insert(mergeDetail);
    }
}
