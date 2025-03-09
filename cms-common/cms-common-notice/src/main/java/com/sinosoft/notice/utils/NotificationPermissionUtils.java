package com.sinosoft.notice.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.satoken.utils.LoginHelper;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import com.sinosoft.system.api.model.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 通知权限判断工具类
 *
 * @author: zzf
 * @create: 2025-03-07 23:03
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationPermissionUtils {
//
//
//    /**
//     * 判断当前用户是否有权限接收指定通知
//     *
//     * @param notification 通知
//     * @return 是否有权限
//     */
//    public boolean hasPermission(SysNotification notification) {
//        LoginUser loginUser = LoginHelper.getLoginUser();
//        if (loginUser == null) {
//            return false;
//        }
//
//        // 1. 公告类型通知，所有用户都可以接收
//        if ("announcement".equals(notification.getType())) {
//            return true;
//        }
//
//        // 2. 检查角色权限
//        if (StringUtils.isNotEmpty(notification.getRoleIds())) {
//            if (!hasRolePermission(notification.getRoleIds(), loginUser)) {
//                return false;
//            }
//        }
//
//        // 3. 检查菜单权限
//        if (StringUtils.isNotEmpty(notification.getMenuPerms())) {
//            if (!hasMenuPermission(notification.getMenuPerms(), loginUser)) {
//                return false;
//            }
//        }
//
//        // 4. 数据范围权限检查（如果需要更复杂的数据范围判断，可以在这里实现）
//
//        return true;
//    }
//
//    /**
//     * 检查用户是否具有通知模板所需要的权限
//     *
//     * @param template 通知模板
//     * @return 是否有权限
//     */
//    public boolean hasPermissionForTemplate(SysNotificationTemplate template) {
//        LoginUser loginUser = LoginHelper.getLoginUser();
//        if (loginUser == null) {
//            return false;
//        }
//
//        // 公告类型通知，所有用户都可以接收
//        if ("announcement".equals(template.getType())) {
//            return true;
//        }
//
//        // 检查角色权限
//        if (StringUtils.isNotEmpty(template.getRoleIds())) {
//            if (!hasRolePermission(template.getRoleIds(), loginUser)) {
//                return false;
//            }
//        }
//
//        // 检查菜单权限
//        if (StringUtils.isNotEmpty(template.getMenuPerms())) {
//            if (!hasMenuPermission(template.getMenuPerms(), loginUser)) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    /**
//     * 检查用户是否具有所需角色权限
//     *
//     * @param roleIdsJson 角色ID列表JSON
//     * @param loginUser   当前登录用户
//     * @return 是否有权限
//     */
//    private boolean hasRolePermission(String roleIdsJson, LoginUser loginUser) {
//        try {
//            List<Long> requiredRoleIds = JSON.parseArray(roleIdsJson, Long.class);
//            if (requiredRoleIds.isEmpty()) {
//                return true;
//            }
//
//            // 获取用户角色列表
//            Set<String> userRoleKeys = roleService.selectRolePermissionByUserId(loginUser.getUserId());
//
//            // 超级管理员拥有所有权限
//            if (userRoleKeys.contains("admin")) {
//                return true;
//            }
//
//            // 获取角色ID列表
//            Set<Long> userRoleIds = loginUser.getRoles().stream()
//                .map(role -> role.getRoleId())
//                .collect(Collectors.toSet());
//
//            // 检查是否有交集
//            for (Long roleId : requiredRoleIds) {
//                if (userRoleIds.contains(roleId)) {
//                    return true;
//                }
//            }
//
//            return false;
//        } catch (Exception e) {
//            log.error("解析角色权限异常: {}", e.getMessage());
//            return false;
//        }
//    }
//
//    /**
//     * 检查用户是否具有所需菜单权限
//     *
//     * @param menuPermsJson 菜单权限列表JSON
//     * @param loginUser     当前登录用户
//     * @return 是否有权限
//     */
//    private boolean hasMenuPermission(String menuPermsJson, LoginUser loginUser) {
//        try {
//            List<String> requiredPerms = JSON.parseArray(menuPermsJson, String.class);
//            if (requiredPerms.isEmpty()) {
//                return true;
//            }
//
//            // 获取用户权限列表
//            Set<String> userPerms = menuService.selectMenuPermsByUserId(loginUser.getUserId());
//
//            // 超级管理员拥有所有权限
//            if (userPerms.contains("*:*:*")) {
//                return true;
//            }
//
//            // 检查是否拥有所需权限
//            for (String perm : requiredPerms) {
//                if (hasPermExact(userPerms, perm)) {
//                    return true;
//                }
//            }
//
//            return false;
//        } catch (Exception e) {
//            log.error("解析菜单权限异常: {}", e.getMessage());
//            return false;
//        }
//    }
//
//    /**
//     * 判断是否具有某个权限
//     *
//     * @param permissions 权限集合
//     * @param permission  待判断权限
//     * @return 是否具有权限
//     */
//    private boolean hasPermExact(Collection<String> permissions, String permission) {
//        return permissions.stream().anyMatch(x -> {
//            // 精确匹配
//            if (StringUtils.equals(x, permission)) {
//                return true;
//            }
//
//            // 通配符匹配
//            if (wildcardMatch(permission, x)) {
//                return true;
//            }
//
//            return false;
//        });
//    }
//
//    /**
//     * 通配符匹配
//     *
//     * @param str     待匹配的字符串
//     * @param pattern 通配符表达式
//     * @return 是否匹配
//     */
//    private boolean wildcardMatch(String str, String pattern) {
//        if (str == null || pattern == null) {
//            return false;
//        }
//
//        // 如果模式中包含 * 号
//        if (pattern.contains("*")) {
//            // 特殊处理 *:*:* 这种常见于权限系统的通配符格式
//            if ("*:*:*".equals(pattern)) {
//                return true;
//            }
//
//            // 基础通配符匹配实现
//            String[] parts = pattern.split("\\*");
//            int startIdx = 0;
//
//            for (String part : parts) {
//                if (part.isEmpty()) {
//                    continue;
//                }
//
//                int idx = str.indexOf(part, startIdx);
//                if (idx == -1) {
//                    return false;
//                }
//
//                startIdx = idx + part.length();
//            }
//
//            // 如果模式以 * 结尾，可以匹配任何剩余部分
//            // 否则，必须精确匹配到字符串结尾
//            return pattern.endsWith("*") || startIdx == str.length();
//        }
//
//        // 如果是常见的权限表达式格式 system:user:view
//        if (pattern.contains(":")) {
//            String[] patternParts = pattern.split(":");
//            String[] strParts = str.split(":");
//
//            if (patternParts.length != strParts.length) {
//                return false;
//            }
//
//            for (int i = 0; i < patternParts.length; i++) {
//                if (!"*".equals(patternParts[i]) && !patternParts[i].equals(strParts[i])) {
//                    return false;
//                }
//            }
//
//            return true;
//        }
//
//        // 简单精确匹配
//        return pattern.equals(str);
//    }
//
//    /**
//     * 将通知模板的权限信息填充到通知对象
//     *
//     * @param notification 通知对象
//     * @param template     通知模板
//     */
//    public void fillPermissionInfo(SysNotification notification, SysNotificationTemplate template) {
//        notification.setRoleIds(template.getRoleIds());
//        notification.setMenuPerms(template.getMenuPerms());
//        notification.setDataScopeSql(generateDataScopeSql(template.getDataScope()));
//    }
//
//    /**
//     * 根据数据范围配置生成SQL条件
//     *
//     * @param dataScopeJson 数据范围配置JSON
//     * @return 数据范围SQL
//     */
//    private String generateDataScopeSql(String dataScopeJson) {
//        if (StringUtils.isEmpty(dataScopeJson)) {
//            return null;
//        }
//
//        try {
//            // 这里可以根据实际需求解析数据范围配置，生成对应的SQL条件
//            // 示例只是一个简单的实现，实际情况可能更复杂
//
//            JSONArray scopeConfig = JSON.parseArray(dataScopeJson);
//
//            // 简单示例：解析部门数据权限
//            StringBuilder sql = new StringBuilder();
//            for (int i = 0; i < scopeConfig.size(); i++) {
//                String scopeType = scopeConfig.getJSONObject(i).getString("type");
//
//                if ("dept".equals(scopeType)) {
//                    Long deptId = scopeConfig.getJSONObject(i).getLong("deptId");
//                    if (deptId != null) {
//                        if (sql.length() > 0) {
//                            sql.append(" OR ");
//                        }
//                        sql.append("(u.dept_id = ").append(deptId).append(")");
//                    }
//                }
//
//                if ("deptTree".equals(scopeType)) {
//                    Long deptId = scopeConfig.getJSONObject(i).getLong("deptId");
//                    if (deptId != null) {
//                        if (sql.length() > 0) {
//                            sql.append(" OR ");
//                        }
//                        sql.append("(d.dept_id = ").append(deptId)
//                            .append(" OR d.ancestors LIKE '%,").append(deptId).append(",%')");
//                    }
//                }
//            }
//
//            return sql.toString();
//        } catch (Exception e) {
//            log.error("解析数据范围异常: {}", e.getMessage());
//            return null;
//        }
//    }
}
