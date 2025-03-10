package com.sinosoft.notice.core.permission.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sinosoft.notice.core.permission.DataPermissionHandler;
import com.sinosoft.notice.domain.SysNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 入职审批权限处理器
 */
@Slf4j
@Component
public class OnboardingPermissionHandler implements DataPermissionHandler {

//    @Autowired
//    private SysUserService userService;

    @Override
    public String getType() {
        return "onboarding";
    }

    @Override
    public void applyPermission(LambdaQueryWrapper<SysNotification> wrapper,
                                Long userId,
                                String permissionConfig) {
//        // 获取用户信息
//        SysUser user = userService.getById(userId);
//        if (user == null) {
//            // 无权限访问任何数据
//            wrapper.apply("1 = 0");
//            return;
//        }
//
//        Long deptId = user.getDeptId();
//        List<String> roles = userService.getUserRoleKeys(userId);
//
//        // 总公司人员可以看到所有
//        if (roles.contains("admin") || roles.contains("hr_admin")) {
//            // 不添加限制条件
//            return;
//        }
//
//        // 分公司只能看到自己部门的
//        wrapper.and(w -> w.apply("JSON_EXTRACT(business_data, '$.orgId') = {0}", deptId)
//            .or()
//            .apply("JSON_EXTRACT(business_data, '$.deptId') = {0}", deptId));
    }

    @Override
    public boolean checkPermission(SysNotification notification, Long userId, String permissionConfig) {
//        // 获取用户信息
//        SysUser user = userService.getById(userId);
//        if (user == null) return false;
//
//        Long deptId = user.getDeptId();
//        List<String> roles = userService.getUserRoleKeys(userId);
//
//        // 总公司人员可以看到所有
//        if (roles.contains("admin") || roles.contains("hr_admin")) {
//            return true;
//        }
//
//        // 分公司只能看到自己部门的
//        try {
//            JSONObject data = JSON.parseObject(notification.getBusinessData());
//            Long orgId = data.getLong("orgId");
//            Long notificationDeptId = data.getLong("deptId");
//
//            return deptId.equals(orgId) || deptId.equals(notificationDeptId);
//        } catch (Exception e) {
//            log.error("解析通知业务数据异常", e);
//            return false;
//        }
        return true;
    }
}
