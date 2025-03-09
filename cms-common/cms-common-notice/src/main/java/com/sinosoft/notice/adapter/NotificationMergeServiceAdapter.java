package com.sinosoft.notice.adapter;

import com.sinosoft.notice.domain.SysNotification;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import com.sinosoft.notice.core.event.DomainEventPublisher;
import com.sinosoft.notice.core.event.NotificationCreatedEvent;
import com.sinosoft.notice.core.service.NotificationMergeDomainService;
import com.sinosoft.notice.model.NotificationPayload;
import com.sinosoft.notice.service.INotificationMergeService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 通知合并服务适配器
 * 连接领域服务与现有实现
 */
@Component
public class NotificationMergeServiceAdapter implements NotificationMergeDomainService {

    private final INotificationMergeService mergeService;
    private final DomainEventPublisher eventPublisher;

    public NotificationMergeServiceAdapter(INotificationMergeService mergeService,
                                           DomainEventPublisher eventPublisher) {
        this.mergeService = mergeService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public SysNotification mergeNotification(SysNotificationTemplate template,
                                             SysNotification notification,
                                             NotificationPayload payload,
                                             Long userId) {
        // 调用现有服务实现
        SysNotification result = mergeService.processNotificationMerge(template,
            payload,
            notification,
            userId);

        // 发布领域事件
        if (result.getParentId() == null) {
            // 如果是新通知，发布创建事件
            eventPublisher.publish(new NotificationCreatedEvent(
                result.getNotificationId(),
                result.getType(),
                result.getTitle(),
                result.getTemplateId(),
                result.getBusinessKey()
            ));
        }

        return result;
    }

    @Override
    public boolean shouldMerge(SysNotificationTemplate template, NotificationPayload payload) {
        String mergeStrategy = getMergeStrategy(template, payload);
        return "update".equals(mergeStrategy) ||
            "count".equals(mergeStrategy) ||
            "list".equals(mergeStrategy);
    }

    private String getMergeStrategy(SysNotificationTemplate template, NotificationPayload payload) {
        // 优先使用负载对象中的策略
        if (payload.getMergeStrategy() != null && !payload.getMergeStrategy().isEmpty()) {
            return payload.getMergeStrategy();
        }

        // 其次使用模板中的策略
        if (template != null && template.getMergeStrategy() != null && !template.getMergeStrategy().isEmpty()) {
            return template.getMergeStrategy();
        }

        // 默认不合并
        return "none";
    }

    @Override
    public String generateMergedTitle(SysNotification notification,
                                      SysNotificationTemplate template,
                                      NotificationPayload payload,
                                      int count) {
        // 根据需要实现合并标题逻辑
        // 可以从现有服务中提取并重构
        return notification.getTitle() + " (" + count + "条)";
    }

    @Override
    public String generateMergedContent(SysNotification notification,
                                        SysNotificationTemplate template,
                                        NotificationPayload payload,
                                        List<SysNotification> items) {
        // 根据需要实现合并内容逻辑
        // 可以从现有服务中提取并重构
        StringBuilder content = new StringBuilder();
        content.append("您有").append(items.size()).append("条相关通知：\n\n");

        for (int i = 0; i < items.size(); i++) {
            content.append(i + 1).append(". ").append(items.get(i).getTitle()).append("\n");
        }

        return content.toString();
    }
}
