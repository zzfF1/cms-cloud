package com.sinosoft.notice.core.permission;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 数据权限工具类
 */
@Component
public class DataPermissionUtil {

    @Autowired
    private DataPermissionManager permissionManager;

    /**
     * 应用模板权限过滤到查询条件
     */
    public void applyTemplatePermission(LambdaQueryWrapper<SysNotification> wrapper,
                                        Long userId,
                                        SysNotificationTemplate template) {
        if (template == null) {
            return;
        }

        String handlerType = template.getPermissionHandler();
        if (StringUtils.isEmpty(handlerType)) {
            return;
        }

        DataPermissionHandler handler = permissionManager.getHandler(handlerType);
        if (handler != null) {
            handler.applyPermission(wrapper, userId, template.getPermissionConfig());
        }
    }

    /**
     * 应用多个模板的权限过滤到查询条件
     */
    public void applyMultiTemplatePermission(LambdaQueryWrapper<SysNotification> wrapper,
                                             Long userId,
                                             List<Map<String, Object>> templateInfos) {
        if (templateInfos == null || templateInfos.isEmpty()) {
            return;
        }

        wrapper.and(mainWrapper -> {
            for (Map<String, Object> info : templateInfos) {
                Long templateId = (Long) info.get("templateId");
                String handlerType = (String) info.get("permissionHandler");
                String config = (String) info.get("permissionConfig");

                mainWrapper.or(templateWrapper -> {
                    // 设置模板条件
                    templateWrapper.eq(SysNotification::getTemplateId, templateId);

                    // 应用该模板的权限过滤
                    if (StringUtils.isNotEmpty(handlerType)) {
                        DataPermissionHandler handler = permissionManager.getHandler(handlerType);
                        if (handler != null) {
                            handler.applyPermission(templateWrapper, userId, config);
                        }
                    }
                });
            }
        });
    }
}
