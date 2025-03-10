package com.sinosoft.notice.core.permission;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据权限管理器
 */
@Slf4j
@Component
public class DataPermissionManager {

    // 按类型存储的处理器
    private final Map<String, DataPermissionHandler> handlerMap = new HashMap<>();

    @Autowired
    public DataPermissionManager(List<DataPermissionHandler> handlers) {
        // 初始化处理器映射
        for (DataPermissionHandler handler : handlers) {
            handlerMap.put(handler.getType(), handler);
            log.info("注册数据权限处理器: {}", handler.getType());
        }
    }

    /**
     * 获取权限处理器
     */
    public DataPermissionHandler getHandler(String type) {
        return handlerMap.get(type);
    }

    /**
     * 应用权限过滤到查询
     */
    public void applyPermission(LambdaQueryWrapper<SysNotification> wrapper,
                                Long userId,
                                SysNotificationTemplate template) {
        if (template == null) {
            return;
        }

        String handlerType = template.getPermissionHandler();
        String config = template.getPermissionConfig();

        if (StringUtils.isEmpty(handlerType)) {
            return;
        }

        DataPermissionHandler handler = getHandler(handlerType);
        if (handler == null) {
            log.warn("未找到权限处理器: {}", handlerType);
            return;
        }

        // 应用权限过滤
        handler.applyPermission(wrapper, userId, config);
    }

    /**
     * 检查单条通知的权限
     */
    public boolean checkPermission(SysNotification notification,
                                   Long userId,
                                   SysNotificationTemplate template) {
        if (template == null) {
            return true;
        }

        String handlerType = template.getPermissionHandler();
        String config = template.getPermissionConfig();

        if (StringUtils.isEmpty(handlerType)) {
            return true;
        }

        DataPermissionHandler handler = getHandler(handlerType);
        if (handler == null) {
            log.warn("未找到权限处理器: {}", handlerType);
            return false;
        }

        // 检查权限
        return handler.checkPermission(notification, userId, config);
    }
}
