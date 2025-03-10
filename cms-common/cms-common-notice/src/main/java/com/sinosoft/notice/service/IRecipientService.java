package com.sinosoft.notice.service;

import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 通知接收人服务接口
 */
public interface IRecipientService {
    /**
     * 根据通知和模板信息查找接收人
     *
     * @param notification 通知对象
     * @param template 通知模板
     * @param context 上下文参数（可选）
     * @return 接收人用户ID列表
     */
    List<Long> findRecipients(SysNotification notification, SysNotificationTemplate template, Map<String, Object> context);

    /**
     * 根据角色查找用户
     *
     * @param roleIds 角色ID列表
     * @return 用户ID列表
     */
    List<Long> findRecipientsByRoles(List<Long> roleIds);

    /**
     * 根据权限查找用户
     *
     * @param permissions 权限标识列表
     * @return 用户ID列表
     */
    List<Long> findRecipientsByPermissions(List<String> permissions);

    /**
     * 根据部门查找用户
     *
     * @param deptIds 部门ID列表
     * @return 用户ID列表
     */
    List<Long> findRecipientsByDepartments(List<Long> deptIds);

    /**
     * 根据用户名获取用户ID
     *
     * @param username 用户名
     * @return 用户ID
     */
    Long getUserIdByUsername(String username);

    /**
     * 创建通知用户记录
     *
     * @param notificationId 通知ID
     * @param userIds 用户ID列表
     */
    void createNotificationUserRecords(Long notificationId, List<Long> userIds);
}
