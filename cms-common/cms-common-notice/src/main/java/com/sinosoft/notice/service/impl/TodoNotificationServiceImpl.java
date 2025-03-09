package com.sinosoft.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationMerge;
import com.sinosoft.notice.domain.SysNotificationUser;
import com.sinosoft.notice.mapper.SysNotificationMapper;
import com.sinosoft.notice.mapper.SysNotificationMergeMapper;
import com.sinosoft.notice.mapper.SysNotificationUserMapper;
import com.sinosoft.notice.service.ITodoNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 待办通知处理服务实现类
 *
 * @author: zzf
 * @create: 2025-03-07 23:04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TodoNotificationServiceImpl implements ITodoNotificationService {

    private final SysNotificationMapper notificationMapper;
    private final SysNotificationUserMapper notificationUserMapper;
    private final SysNotificationMergeMapper mergeMapper;

    /**
     * 获取用户的待办通知列表
     *
     * @param userId      用户ID
     * @param isProcessed 是否已处理（null表示全部）
     * @param pageNum     页码
     * @param pageSize    每页条数
     * @return 待办通知列表
     */
    @Override
    public List<SysNotification> getTodoNotifications(Long userId, Boolean isProcessed, Integer pageNum, Integer pageSize) {

        // 查询条件
        SysNotificationUser query = new SysNotificationUser();
        query.setUserId(userId);

        if (isProcessed != null) {
            query.setIsProcessed(isProcessed ? "1" : "0");
        }

        // 分页参数
        int offset = (pageNum - 1) * pageSize;

        // 查询符合条件的通知
        List<SysNotificationUser> notificationUsers = notificationUserMapper.selectTodoNotifications(
            query, offset, pageSize);

        if (notificationUsers == null || notificationUsers.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取通知ID列表
        List<Long> notificationIds = new ArrayList<>();
        for (SysNotificationUser user : notificationUsers) {
            notificationIds.add(user.getNotificationId());
        }

        // 查询通知详情
        List<SysNotification> notifications = notificationMapper.selectBatchIds(notificationIds);

        // 只返回类型为"todo"的通知
        return notifications.stream()
            .filter(n -> "todo".equals(n.getType()))
            .toList();
    }

    /**
     * 标记待办通知为已处理
     *
     * @param userId         用户ID
     * @param notificationId 通知ID
     * @param processNote    处理备注
     * @return 是否处理成功
     */
    @Transactional
    @Override
    public boolean processTodoNotification(Long userId, Long notificationId, String processNote) {

        // 1. 查找通知接收记录
        SysNotificationUser recipient = notificationUserMapper.selectByNotificationIdAndUserId(
            notificationId, userId);

        if (recipient == null) {
            throw new ServiceException("通知接收记录不存在");
        }

        // 2. 验证通知类型
        SysNotification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new ServiceException("通知不存在");
        }

        if (!"todo".equals(notification.getType())) {
            throw new ServiceException("只有待办类型的通知可以标记为已处理");
        }

        // 3. 检查是否为合并通知
        if ("1".equals(notification.getIsMerged())) {
            // 提取子项ID (如果存在)
            Long childId = getChildIdFromProcessNote(processNote);

            if (childId != null) {
                // 处理合并通知的单个子项
                return processMergedNotificationItem(recipient, notification, childId, processNote);
            } else {
                // 处理整个合并通知
                return processEntireMergedNotification(recipient, notification, processNote);
            }
        } else {
            // 4. 处理普通通知
            if ("1".equals(recipient.getIsProcessed())) {
                // 已经处理过，不需要重复处理
                return false;
            }

            // 标记为已读
            recipient.setIsRead("1");
            if (recipient.getReadTime() == null) {
                recipient.setReadTime(new Date());
            }

            // 标记为已处理
            recipient.setIsProcessed("1");
            recipient.setProcessTime(new Date());
            recipient.setProcessNote(processNote);

            // 更新接收记录
            notificationUserMapper.updateById(recipient);
            log.info("处理待办通知，用户ID: {}, 通知ID: {}", userId, notificationId);

            return true;
        }
    }

    /**
     * 从处理备注中提取子项ID
     */
    private Long getChildIdFromProcessNote(String processNote) {
        if (StringUtils.isEmpty(processNote)) {
            return null;
        }

        // 处理备注格式：itemId:123
        if (processNote.startsWith("itemId:")) {
            try {
                String idStr = processNote.substring(7);
                return Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                log.error("解析子项ID异常: {}", e.getMessage());
                return null;
            }
        }

        return null;
    }

    /**
     * 处理合并通知的单个子项
     */
    private boolean processMergedNotificationItem(
        SysNotificationUser recipient,
        SysNotification notification,
        Long childId,
        String processNote) {

        // 1. 检查子项是否属于该合并通知
        SysNotificationMerge mergeDetail = mergeMapper.selectByParentAndChildId(
            notification.getNotificationId(), childId);

        if (mergeDetail == null) {
            throw new ServiceException("指定的子项不属于该通知");
        }

        // 2. 从业务数据中提取处理状态信息
        JSONObject businessData;
        try {
            if (StringUtils.isEmpty(notification.getBusinessData())) {
                businessData = new JSONObject();
            } else {
                businessData = JSON.parseObject(notification.getBusinessData());
            }

            // 确保项目列表存在
            if (!businessData.containsKey("items")) {
                businessData.put("items", new JSONArray());
            }

            JSONArray items = businessData.getJSONArray("items");

            // 查找或创建子项信息
            JSONObject childItem = null;
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                if (childId.equals(item.getLong("id"))) {
                    childItem = item;
                    break;
                }
            }

            // 如果找不到子项，创建一个新的
            if (childItem == null) {
                childItem = new JSONObject();
                childItem.put("id", childId);
                items.add(childItem);
            }

            // 更新子项处理状态
            childItem.put("processed", true);
            childItem.put("processTime", new Date());
            childItem.put("processNote", processNote);

            // 计算已处理项数量
            int totalItems = items.size();
            int processedItems = 0;
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                if (Boolean.TRUE.equals(item.getBoolean("processed"))) {
                    processedItems++;
                }
            }

            businessData.put("processedCount", processedItems);
            businessData.put("totalCount", totalItems);

            // 3. 更新通知业务数据
            notification.setBusinessData(businessData.toString());
            notificationMapper.updateById(notification);

            // 4. 如果全部处理完，标记整个通知为已处理
            if (processedItems >= totalItems) {
                recipient.setIsProcessed("1");
                recipient.setProcessTime(new Date());
                recipient.setProcessNote("全部子项已处理");
            }

            // 5. 确保标记为已读
            recipient.setIsRead("1");
            if (recipient.getReadTime() == null) {
                recipient.setReadTime(new Date());
            }

            // 6. 更新接收记录
            notificationUserMapper.updateById(recipient);
            log.info("处理合并通知子项，通知ID: {}, 子项ID: {}, 处理进度: {}/{}",
                notification.getNotificationId(), childId, processedItems, totalItems);

            return true;

        } catch (Exception e) {
            log.error("处理合并通知子项异常: {}", e.getMessage());
            throw new ServiceException("处理合并通知子项失败：" + e.getMessage());
        }
    }

    /**
     * 处理整个合并通知
     */
    private boolean processEntireMergedNotification(
        SysNotificationUser recipient,
        SysNotification notification,
        String processNote) {

        // 检查是否已处理
        if ("1".equals(recipient.getIsProcessed())) {
            return false;
        }

        try {
            // 1. 更新业务数据，标记所有子项为已处理
            JSONObject businessData;
            if (StringUtils.isEmpty(notification.getBusinessData())) {
                businessData = new JSONObject();
            } else {
                businessData = JSON.parseObject(notification.getBusinessData());
            }

            // 查找所有子通知
            List<SysNotificationMerge> mergeDetails = mergeMapper.selectByParentId(
                notification.getNotificationId());

            // 更新业务数据中的子项状态
            if (businessData.containsKey("items")) {
                JSONArray items = businessData.getJSONArray("items");
                for (int i = 0; i < items.size(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    item.put("processed", true);
                    item.put("processTime", new Date());
                    item.put("processNote", "批量处理");
                }
            } else {
                // 如果没有items字段，创建一个包含所有子项的数组
                JSONArray items = new JSONArray();
                for (SysNotificationMerge detail : mergeDetails) {
                    JSONObject item = new JSONObject();
                    item.put("id", detail.getChildId());
                    item.put("processed", true);
                    item.put("processTime", new Date());
                    item.put("processNote", "批量处理");
                    items.add(item);
                }
                businessData.put("items", items);
            }

            // 更新处理统计信息
            businessData.put("processedCount", mergeDetails.size());
            businessData.put("totalCount", mergeDetails.size());
            businessData.put("allProcessed", true);

            // 更新通知业务数据
            notification.setBusinessData(businessData.toString());
            notificationMapper.updateById(notification);

            // 2. 标记为已读和已处理
            recipient.setIsRead("1");
            if (recipient.getReadTime() == null) {
                recipient.setReadTime(new Date());
            }

            recipient.setIsProcessed("1");
            recipient.setProcessTime(new Date());
            recipient.setProcessNote(processNote);

            // 更新接收记录
            notificationUserMapper.updateById(recipient);
            log.info("处理整个合并通知，通知ID: {}, 子项数量: {}", notification.getNotificationId(), mergeDetails.size());

            return true;

        } catch (Exception e) {
            log.error("处理整个合并通知异常: {}", e.getMessage());
            throw new ServiceException("处理合并通知失败：" + e.getMessage());
        }
    }

    /**
     * 获取待办通知统计
     *
     * @param userId 用户ID
     * @return 统计信息
     */
    @Override
    public JSONObject getTodoStatistics(Long userId) {

        // 查询未处理和已处理的待办数量
        int unprocessedCount = notificationUserMapper.countUserTodoByStatus(userId, "0");
        int processedCount = notificationUserMapper.countUserTodoByStatus(userId, "1");

        // 查询高优先级未处理待办数量
        int highPriorityCount = notificationUserMapper.countUserHighPriorityTodo(userId);

        // 查询即将过期的待办数量（7天内）
        Date expirationDate = new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);
        int expiringCount = notificationUserMapper.countUserExpiringTodo(userId, expirationDate);

        // 构建统计结果
        JSONObject result = new JSONObject();
        result.put("unprocessedCount", unprocessedCount);
        result.put("processedCount", processedCount);
        result.put("totalCount", unprocessedCount + processedCount);
        result.put("highPriorityCount", highPriorityCount);
        result.put("expiringCount", expiringCount);

        return result;
    }
}
