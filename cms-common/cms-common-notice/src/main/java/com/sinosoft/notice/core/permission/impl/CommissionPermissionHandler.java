package com.sinosoft.notice.core.permission.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sinosoft.notice.core.permission.DataPermissionHandler;
import com.sinosoft.notice.domain.SysNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 手续费权限处理器
 */
@Slf4j
@Component
public class CommissionPermissionHandler implements DataPermissionHandler {

//    @Autowired
//    private SysUserService userService;

    @Override
    public String getType() {
        return "commission";
    }

    @Override
    public void applyPermission(LambdaQueryWrapper<SysNotification> wrapper,
                                Long userId,
                                String permissionConfig) {
//        // 获取用户信息和角色
//        SysUser user = userService.getById(userId);
//        if (user == null) {
//            wrapper.apply("1 = 0");
//            return;
//        }
//
//        List<String> roles = userService.getUserRoleKeys(userId);
//
//        // 财务管理员可以看到所有
//        if (roles.contains("finance_admin")) {
//            return;
//        }
//
//        // 中介机构用户只能看到自己机构的
//        if (roles.contains("agency_user")) {
//            Long agencyId = userService.getUserAgencyId(userId);
//            wrapper.apply("JSON_EXTRACT(business_data, '$.agencyId') = {0}", agencyId);
//            return;
//        }
//
//        // 普通业务员只能看到自己的
//        wrapper.apply("JSON_EXTRACT(business_data, '$.creatorId') = {0}", userId);
    }

    @Override
    public boolean checkPermission(SysNotification notification, Long userId, String permissionConfig) {
//        // 获取用户信息和角色
//        SysUser user = userService.getById(userId);
//        if (user == null) return false;
//
//        List<String> roles = userService.getUserRoleKeys(userId);
//
//        // 财务管理员可以看到所有
//        if (roles.contains("finance_admin")) {
//            return true;
//        }
//
//        try {
//            JSONObject data = JSON.parseObject(notification.getBusinessData());
//
//            // 中介机构用户只能看到自己机构的
//            if (roles.contains("agency_user")) {
//                Long agencyId = userService.getUserAgencyId(userId);
//                Long dataAgencyId = data.getLong("agencyId");
//                return agencyId.equals(dataAgencyId);
//            }
//
//            // 普通业务员只能看到自己的
//            Long creatorId = data.getLong("creatorId");
//            return userId.equals(creatorId);
//        } catch (Exception e) {
//            log.error("解析通知业务数据异常", e);
//            return false;
//        }
        return true;
    }
}
