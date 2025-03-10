package com.sinosoft.notice.service.impl;


import com.alibaba.fastjson2.JSON;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.notice.mapper.NoticeUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 流程通知用户查询服务
 *
 * @author zzf
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FlowNotificationUserService {

    private final NoticeUserRoleMapper noticeUserRoleMapper;

    /**
     * 根据角色ID列表获取用户
     *
     * @param roleIds 角色ID列表
     * @return 用户ID列表
     */
    public List<Long> getUsersByRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            // 使用已有Mapper查询
            List<Long> userIds = noticeUserRoleMapper.selectUserIdsByRoleIds(roleIds);
            log.debug("根据角色查询到 {} 个用户", userIds.size());
            return userIds;
        } catch (Exception e) {
            log.error("根据角色查询用户异常", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据权限列表获取用户
     *
     * @param perms 权限列表
     * @return 用户ID列表
     */
    public List<Long> getUsersByPerms(List<String> perms) {
        if (perms == null || perms.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            // 使用Mapper查询
            List<Long> userIds = noticeUserRoleMapper.selectUserIdsByPerms(perms);
            log.debug("根据权限查询到 {} 个用户", userIds.size());
            return userIds;
        } catch (Exception e) {
            log.error("根据权限查询用户异常", e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取下一步处理人
     *
     * @param lcId     流程节点ID
     * @param lcNextId 下一流程节点ID
     * @param dataIds  数据ID列表
     * @return 用户ID列表
     */
    public List<Long> getNextHandlers(Integer lcId, Integer lcNextId, List<String> dataIds) {
        if (lcNextId == null || lcNextId <= 0) {
            return Collections.emptyList();
        }

        try {
            // 1. 先检查是否有直接配置的处理人
            String handlersValue = noticeUserRoleMapper.selectNodeHandlers(lcNextId);
            if (StringUtils.isNotEmpty(handlersValue)) {
                try {
                    return JSON.parseArray(handlersValue, Long.class);
                } catch (Exception e) {
                    log.warn("解析处理人配置异常: {}", e.getMessage());
                }
            }

            // 2. 查询下一节点的处理角色
            List<Long> roleIds = getNodeRoleIds(lcNextId);
            if (!roleIds.isEmpty()) {
                return getUsersByRoles(roleIds);
            }

            // 3. 查询下一节点的处理权限
            List<String> perms = getNodePermissions(lcNextId);
            if (!perms.isEmpty()) {
                return getUsersByPerms(perms);
            }

            log.debug("未找到下一节点的处理人配置，返回空列表");
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("获取下一步处理人异常", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据用户名获取用户ID
     *
     * @param username
     * @return
     */
    public Long getUserIdByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }

        try {
            // 使用Mapper查询
            Long userId = noticeUserRoleMapper.selectUserIdByUsername(username);
            if (userId == null) {
                log.warn("未找到用户: {}", username);
            }
            return userId;
        } catch (Exception e) {
            log.error("根据用户名查询用户ID异常", e);
            return null;
        }
    }

    /**
     * 获取节点角色ID列表
     *
     * @param lcId 流程节点ID
     * @return 角色ID列表
     */
    private List<Long> getNodeRoleIds(Integer lcId) {
        try {
            // 查询角色配置
            String rolesValue = noticeUserRoleMapper.selectNodeRoles(lcId);

            if (StringUtils.isEmpty(rolesValue)) {
                return Collections.emptyList();
            }

            // 尝试解析JSON数组
            try {
                return JSON.parseArray(rolesValue, Long.class);
            } catch (Exception e) {
                log.warn("解析角色配置JSON异常: {}, 尝试简单解析", e.getMessage());

                // 简单解析，适用于 [1,2,3] 格式
                String cleanValue = rolesValue.replace("[", "").replace("]", "").replace(" ", "");
                if (StringUtils.isEmpty(cleanValue)) {
                    return Collections.emptyList();
                }

                String[] parts = cleanValue.split(",");
                List<Long> roleIds = new ArrayList<>();

                for (String part : parts) {
                    if (StringUtils.isNotEmpty(part)) {
                        try {
                            roleIds.add(Long.valueOf(part.trim()));
                        } catch (NumberFormatException nfe) {
                            log.warn("无法解析角色ID: {}", part);
                        }
                    }
                }

                return roleIds;
            }
        } catch (Exception e) {
            log.error("获取节点角色配置异常", e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取节点权限列表
     *
     * @param lcId 流程节点ID
     * @return 权限列表
     */
    private List<String> getNodePermissions(Integer lcId) {
        try {
            // 查询权限配置
            String permsValue = noticeUserRoleMapper.selectNodePerms(lcId);

            if (StringUtils.isEmpty(permsValue)) {
                return Collections.emptyList();
            }

            // 尝试解析JSON数组
            try {
                return JSON.parseArray(permsValue, String.class);
            } catch (Exception e) {
                log.warn("解析权限配置JSON异常: {}, 尝试简单解析", e.getMessage());

                // 简单解析，适用于 ["perm1","perm2"] 格式
                String cleanValue = permsValue.replace("[", "").replace("]", "").replace("\"", "").replace("'", "");
                if (StringUtils.isEmpty(cleanValue)) {
                    return Collections.emptyList();
                }

                String[] parts = cleanValue.split(",");
                List<String> perms = new ArrayList<>();

                for (String part : parts) {
                    String trimmed = part.trim();
                    if (StringUtils.isNotEmpty(trimmed)) {
                        perms.add(trimmed);
                    }
                }

                return perms;
            }
        } catch (Exception e) {
            log.error("获取节点权限配置异常", e);
            return Collections.emptyList();
        }
    }
}
