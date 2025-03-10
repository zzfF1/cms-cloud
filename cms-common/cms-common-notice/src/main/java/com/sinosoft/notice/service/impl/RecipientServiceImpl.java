package com.sinosoft.notice.service.impl;

import com.alibaba.fastjson2.JSON;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.notice.core.permission.DataPermissionHandler;
import com.sinosoft.notice.core.permission.DataPermissionManager;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import com.sinosoft.notice.domain.SysNotificationUser;
import com.sinosoft.notice.mapper.NoticeUserRoleMapper;
import com.sinosoft.notice.mapper.SysNotificationUserMapper;
import com.sinosoft.notice.service.IRecipientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通知接收人服务实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RecipientServiceImpl implements IRecipientService {

    private final SysNotificationUserMapper notificationUserMapper;
    private final NoticeUserRoleMapper noticeUserRoleMapper;
    private final DataPermissionManager permissionManager;

    /**
     * 根据通知和模板信息查找接收人
     */
    @Override
    public List<Long> findRecipients(SysNotification notification,
                                     SysNotificationTemplate template,
                                     Map<String, Object> context) {
        Set<Long> recipients = new HashSet<>();

        if (template == null) {
            log.warn("模板为空，无法确定接收人");
            return new ArrayList<>();
        }

        try {
            // 1. 基于模板角色查找用户
            if (StringUtils.isNotEmpty(template.getRoleIds())) {
                try {
                    List<Long> roleIds = JSON.parseArray(template.getRoleIds(), Long.class);
                    if (roleIds != null && !roleIds.isEmpty()) {
                        recipients.addAll(findRecipientsByRoles(roleIds));
                    }
                } catch (Exception e) {
                    log.error("解析角色ID列表失败: {}", template.getRoleIds(), e);
                }
            }

            // 2. 基于模板权限查找用户
            if (StringUtils.isNotEmpty(template.getMenuPerms())) {
                try {
                    List<String> perms = Arrays.asList(template.getMenuPerms().split(","));
                    if (!perms.isEmpty()) {
                        recipients.addAll(findRecipientsByPermissions(perms));
                    }
                } catch (Exception e) {
                    log.error("解析权限列表失败: {}", template.getMenuPerms(), e);
                }
            }

            // 3. 应用额外的数据权限过滤
            if (StringUtils.isNotEmpty(template.getPermissionHandler()) && notification != null) {
                DataPermissionHandler handler = permissionManager.getHandler(template.getPermissionHandler());
                if (handler != null) {
                    // 过滤不符合数据权限的用户
                    recipients = recipients.stream()
                        .filter(userId -> handler.checkPermission(notification, userId, template.getPermissionConfig()))
                        .collect(Collectors.toSet());
                }
            }

            // 4. 处理上下文中的特殊规则
            if (context != null) {
                // 处理操作者
                if (context.containsKey("operator") && context.get("operator") instanceof String) {
                    String operator = (String) context.get("operator");
                    if (StringUtils.isNotEmpty(operator)) {
                        Long operatorId = getUserIdByUsername(operator);
                        if (operatorId != null) {
                            recipients.add(operatorId);
                        }
                    }
                }

                // 处理特定接收人（如流程相关人）
                if (context.containsKey("relatedUserIds") && context.get("relatedUserIds") instanceof List) {
                    try {
                        @SuppressWarnings("unchecked")
                        List<Long> relatedUserIds = (List<Long>) context.get("relatedUserIds");
                        if (!relatedUserIds.isEmpty()) {
                            recipients.addAll(relatedUserIds);
                        }
                    } catch (Exception e) {
                        log.error("处理相关用户ID列表失败", e);
                    }
                }
            }

            return new ArrayList<>(recipients);
        } catch (Exception e) {
            log.error("查找通知接收人异常", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据角色查找用户
     */
    @Override
    public List<Long> findRecipientsByRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            return noticeUserRoleMapper.selectUserIdsByRoleIds(roleIds);
        } catch (Exception e) {
            log.error("根据角色查找用户异常", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据权限查找用户
     */
    @Override
    public List<Long> findRecipientsByPermissions(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            return noticeUserRoleMapper.selectUserIdsByPerms(permissions);
        } catch (Exception e) {
            log.error("根据权限查找用户异常", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据部门查找用户
     */
    @Override
    public List<Long> findRecipientsByDepartments(List<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            return noticeUserRoleMapper.selectUserIdsByDeptIds(deptIds);
        } catch (Exception e) {
            log.error("根据部门查找用户异常", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据用户名获取用户ID
     */
    @Override
    public Long getUserIdByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }

        try {
            return noticeUserRoleMapper.selectUserIdByUsername(username);
        } catch (Exception e) {
            log.error("根据用户名获取用户ID异常", e);
            return null;
        }
    }

    /**
     * 创建通知用户记录
     */
    @Transactional
    @Override
    public void createNotificationUserRecords(Long notificationId, List<Long> userIds) {
        if (notificationId == null || userIds == null || userIds.isEmpty()) {
            return;
        }

        try {
            List<SysNotificationUser> records = new ArrayList<>();
            Date now = new Date();

            for (Long userId : userIds) {
                SysNotificationUser record = new SysNotificationUser();
                record.setNotificationId(notificationId);
                record.setUserId(userId);
                record.setIsRead("0"); // 未读
                record.setCreateTime(now);

                records.add(record);
            }

            // 批量插入
            for (SysNotificationUser record : records) {
                notificationUserMapper.insert(record);
            }

            log.info("批量创建通知用户记录成功，通知ID: {}, 用户数: {}", notificationId, userIds.size());
        } catch (Exception e) {
            log.error("创建通知用户记录异常", e);
            throw e; // 抛出异常以便事务回滚
        }
    }
}
