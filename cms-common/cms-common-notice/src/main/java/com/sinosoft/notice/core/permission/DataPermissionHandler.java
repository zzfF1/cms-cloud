package com.sinosoft.notice.core.permission;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sinosoft.notice.domain.SysNotification;

/**
 * 数据权限处理器接口
 */
public interface DataPermissionHandler {
    /**
     * 获取处理器类型标识
     */
    String getType();

    /**
     * 应用权限过滤条件到查询构造器
     * @param wrapper 查询条件构造器
     * @param userId 用户ID
     * @param permissionConfig 权限配置
     */
    void applyPermission(LambdaQueryWrapper<SysNotification> wrapper,
                         Long userId,
                         String permissionConfig);

    /**
     * 检查单条通知的权限
     */
    boolean checkPermission(SysNotification notification, Long userId, String permissionConfig);
}
