package com.sinosoft.notice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import com.sinosoft.notice.domain.SysNotificationUser;
import com.sinosoft.notice.mapper.SysNotificationUserMapper;
import com.sinosoft.notice.service.IRecipientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import com.alibaba.fastjson2.JSON;

/**
 * 通知接收人筛选服务实现类
 *
 * @author: zzf
 * @create: 2025-03-07 22:58
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RecipientServiceImpl implements IRecipientService {

    private final SysNotificationUserMapper notificationUserMapper;

    /**
     * 查找通知接收人
     *
     * @param notification    通知对象
     * @param template        通知模板
     * @param specificUserIds 指定用户IDs（可选）
     * @return 用户ID列表
     */
    @Override
    public List<Long> findRecipients(SysNotification notification, SysNotificationTemplate template, List<Long> specificUserIds) {
        // 1. 如果指定了用户ID列表，直接返回
        if (specificUserIds != null && !specificUserIds.isEmpty()) {
            log.info("使用指定的接收人列表，数量: {}", specificUserIds.size());
            return filterActiveUsers(specificUserIds);
        }

        // 2. 如果是公告类型的通知，发送给所有活跃用户（可根据实际需求调整）
        if ("announcement".equals(notification.getType())) {
            log.info("公告类型通知，查找所有活跃用户");
            return findAllActiveUsers();
        }

        // 3. 根据角色ID和权限过滤用户
        Set<Long> recipientUserIds = new HashSet<>();

        // 3.1 按角色筛选用户
        List<Long> roleUserIds = findUsersByRoles(parseJsonLongArray(notification.getRoleIds()));
        recipientUserIds.addAll(roleUserIds);

        // 3.2 按菜单权限筛选用户
        List<String> menuPerms = parseJsonStringArray(notification.getMenuPerms());
        if (!menuPerms.isEmpty()) {
            List<Long> permUserIds = findUsersByMenuPerms(menuPerms);

            // 如果已经有角色筛选结果，则取交集；否则直接添加
            if (!roleUserIds.isEmpty()) {
                recipientUserIds.retainAll(new HashSet<>(permUserIds));
            } else {
                recipientUserIds.addAll(permUserIds);
            }
        }

        // 3.3 按数据范围筛选用户（如有必要）
        if (notification.getDataScopeSql() != null && !notification.getDataScopeSql().isEmpty()) {
            List<Long> dataScopeUserIds = findUsersByDataScope(notification.getDataScopeSql());

            // 如果已经有筛选结果，则取交集；否则直接添加
            if (!recipientUserIds.isEmpty()) {
                recipientUserIds.retainAll(new HashSet<>(dataScopeUserIds));
            } else {
                recipientUserIds.addAll(dataScopeUserIds);
            }
        }

        // 如果没有找到符合条件的用户，返回空列表
        if (recipientUserIds.isEmpty()) {
            log.warn("没有找到符合条件的接收人");
            return Collections.emptyList();
        }

        // 过滤非活跃用户
        List<Long> result = filterActiveUsers(new ArrayList<>(recipientUserIds));
        log.info("找到符合条件的接收人数量: {}", result.size());
        return result;
    }

    /**
     * 批量创建通知-用户关联记录
     *
     * @param notificationId 通知ID
     * @param userIds        用户ID列表
     */
    @Transactional
    @Override
    public void createNotificationUserRecords(Long notificationId, List<Long> userIds) {
        if (userIds.isEmpty()) {
            return;
        }

        List<SysNotificationUser> batchRecords = new ArrayList<>();
        Date now = new Date();

        for (Long userId : userIds) {
            SysNotificationUser record = new SysNotificationUser();
            record.setId(System.currentTimeMillis() + new Random().nextInt(1000)); // 简单生成ID，实际应使用ID生成器
            record.setNotificationId(notificationId);
            record.setUserId(userId);
            record.setIsRead("0"); // 未读
            record.setIsProcessed("0"); // 未处理
            record.setCreateTime(now);
            batchRecords.add(record);
        }

        notificationUserMapper.insertBatch(batchRecords);
        log.info("批量创建通知-用户关联记录，通知ID: {}, 用户数量: {}", notificationId, userIds.size());
    }

    /**
     * 查找所有活跃用户
     */
    private List<Long> findAllActiveUsers() {
        return Collections.emptyList();
    }

    /**
     * 按角色筛选用户
     */
    private List<Long> findUsersByRoles(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    /**
     * 按菜单权限筛选用户
     */
    private List<Long> findUsersByMenuPerms(List<String> perms) {
        if (perms.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    /**
     * 按数据范围SQL筛选用户
     */
    private List<Long> findUsersByDataScope(String dataScopeSql) {
        Map<String, Object> params = new HashMap<>();
        params.put("dataScopeSql", dataScopeSql);
        return Collections.emptyList();
    }

    /**
     * 过滤活跃用户
     */
    private List<Long> filterActiveUsers(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    /**
     * 解析JSON长整型数组
     */
    private List<Long> parseJsonLongArray(String jsonArray) {
        if (jsonArray == null || jsonArray.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return JSON.parseArray(jsonArray, Long.class);
        } catch (Exception e) {
            log.error("解析JSON数组异常: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 解析JSON字符串数组
     */
    private List<String> parseJsonStringArray(String jsonArray) {
        if (jsonArray == null || jsonArray.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return JSON.parseArray(jsonArray, String.class);
        } catch (Exception e) {
            log.error("解析JSON数组异常: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
