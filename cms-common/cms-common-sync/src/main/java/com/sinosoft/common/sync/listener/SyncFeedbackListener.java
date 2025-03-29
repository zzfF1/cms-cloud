package com.sinosoft.common.sync.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.sync.model.SyncFeedback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 同步反馈消息监听器
 * <p>
 * 接收处理结果反馈消息并更新同步状态
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SyncFeedbackListener {

    private final ObjectMapper objectMapper;

    /**
     * 处理反馈消息
     * <p>
     * 注意：此方法需要根据你的MQ框架进行适配
     * 假设你使用的是RocketMQ的注解方式监听
     *
     * @param message 消息内容
     * @return 是否处理成功
     */
    // @RocketMQMessageListener(topic = "${sync.feedback.topic}", consumerGroup = "${sync.feedback.consumer-group}")
    public boolean onMessage(String message) {
        try {
            log.debug("收到同步反馈消息: {}", message);

            // 解析反馈消息
            SyncFeedback feedback = objectMapper.readValue(message, SyncFeedback.class);

            // 更新同步记录状态
            return updateSyncRecordStatus(feedback);
        } catch (Exception e) {
            log.error("处理同步反馈消息失败", e);
            return false;
        }
    }

    /**
     * 更新同步记录状态
     *
     * @param feedback 反馈信息
     * @return 是否更新成功
     */
    private boolean updateSyncRecordStatus(SyncFeedback feedback) {
        return true;
//        try {
//            // 查找原同步记录
//            String messageId = feedback.getOriginalMessageId();
//            if (messageId == null || messageId.isEmpty()) {
//                log.warn("反馈消息缺少原消息ID，无法更新状态");
//                return false;
//            }
//
//            // 计算新状态
//            int newStatus = feedback.getSuccess() ? 2 : 3; // 2-接收方确认成功，3-接收方处理失败
//
//            // 更新状态
//            boolean updated = syncRecordService.updateStatusByMessageId(
//                messageId,
//                newStatus,
//                feedback.getProcessTime(),
//                feedback.getSuccess() ? null : feedback.getErrorMessage()
//            );
//
//            if (updated) {
//                log.info("成功更新同步记录状态: messageId={}, status={}", messageId, newStatus);
//            } else {
//                log.warn("未找到对应的同步记录: messageId={}", messageId);
//            }
//
//            return updated;
//        } catch (Exception e) {
//            log.error("更新同步记录状态失败", e);
//            return false;
//        }
    }
}
