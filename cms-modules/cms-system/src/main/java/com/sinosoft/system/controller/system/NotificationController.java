package com.sinosoft.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.log.annotation.Log;
import com.sinosoft.common.log.enums.BusinessType;
import com.sinosoft.common.satoken.utils.LoginHelper;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.notice.domain.dto.MarkReadBatchDTO;
import com.sinosoft.notice.domain.dto.NotificationDTO;
import com.sinosoft.notice.domain.dto.NotificationSettingDTO;
import com.sinosoft.notice.domain.dto.TodoResultDTO;
import com.sinosoft.notice.domain.vo.NotificationVO;
import com.sinosoft.notice.domain.vo.NotificationCountVO;
import com.sinosoft.notice.service.INotificationService;
import com.sinosoft.notice.service.INotificationSettingService;
import com.sinosoft.notice.service.ITodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 通知服务 控制器
 *
 * @author zzf
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/notification")
public class NotificationController extends BaseController {

    private final INotificationService notificationService;
    private final INotificationSettingService settingService;
    private final ITodoService todoService;

    /**
     * 获取通知列表
     */
    @GetMapping("/notifications")
    public TableDataInfo<NotificationVO> getNotifications(
        @RequestParam(required = false) String type,
        @RequestParam(required = false) Boolean isRead,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String startTime,
        @RequestParam(required = false) String endTime,
        PageQuery pageQuery) {

        NotificationDTO dto = new NotificationDTO();
        dto.setUserId(LoginHelper.getUserId());
        dto.setType(type);
        dto.setIsRead(isRead);
        dto.setKeyword(keyword);
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);

        return notificationService.getNotifications(dto, pageQuery);
    }

    /**
     * 获取未读通知计数
     */
    @GetMapping("/unread/count")
    public R<NotificationCountVO> getUnreadCount() {
        Map<String, Integer> countMap = notificationService.getUnreadCount(LoginHelper.getUserId());

        NotificationCountVO vo = new NotificationCountVO();
        vo.setTotal(countMap.getOrDefault("total", 0));
        vo.setTodo(countMap.getOrDefault("todo", 0));
        vo.setAlert(countMap.getOrDefault("alert", 0));
        vo.setMessage(countMap.getOrDefault("message", 0));
        vo.setAnnouncement(countMap.getOrDefault("announcement", 0));

        return R.ok(vo);
    }

    /**
     * 标记单条通知为已读
     */
    @PutMapping("/read/{notificationId}")
    public R<Void> markAsRead(@PathVariable Long notificationId) {
        boolean result = notificationService.markAsRead(LoginHelper.getUserId(), notificationId);
        return result ? R.ok() : R.fail("标记已读失败");
    }

    /**
     * 批量标记通知为已读
     */
    @PutMapping("/read/batch")
    public R<Void> batchMarkAsRead(@RequestBody MarkReadBatchDTO dto) {
        int count = notificationService.batchMarkAsRead(LoginHelper.getUserId(), dto.getNotificationIds());
        return count > 0 ? R.ok() : R.fail("批量标记已读失败");
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read/all")
    public R<Void> markAllAsRead(@RequestParam(required = false) String type) {
        int count = notificationService.markAllAsRead(LoginHelper.getUserId(), type);
        return count > 0 ? R.ok() : R.fail("标记全部已读失败");
    }

    /**
     * 获取用户待办任务列表
     */
    @GetMapping("/todo/list")
    public R<List<TodoResultDTO>> getUserTodoList() {
        List<TodoResultDTO> todoList = todoService.getUserTodoList(LoginHelper.getUserId());
        return R.ok(todoList);
    }

    /**
     * 获取用户通知设置
     */
    @GetMapping("/settings")
    public R<Map<String, Object>> getNotificationSettings() {
        Map<String, Object> settings = settingService.getUserSettings(LoginHelper.getUserId());
        return R.ok(settings);
    }

    /**
     * 更新用户通知设置
     */
    @PutMapping("/settings")
    public R<Void> updateNotificationSettings(@RequestBody NotificationSettingDTO setting) {
        boolean result = settingService.updateUserSettings(LoginHelper.getUserId(), setting.toMap());
        return result ? R.ok() : R.fail("更新通知设置失败");
    }

    /**
     * 发送通知（管理接口）
     */
    @SaCheckPermission("notice:notification:send")
    @Log(title = "发送通知", businessType = BusinessType.INSERT)
    @PostMapping("/send")
    public R<Long> sendNotification(@Validated(AddGroup.class) @RequestBody NotificationDTO notification) {
        // 设置当前用户为发送者
        notification.setUserId(LoginHelper.getUserId());
        Long notificationId = notificationService.sendNotification(
            notification.getTemplateCode(),
            notification.buildPayload(),
            notification.getSourceType(),
            notification.getSourceId(),
            notification.getReceiverIds()
        );
        return R.ok(notificationId);
    }
}
