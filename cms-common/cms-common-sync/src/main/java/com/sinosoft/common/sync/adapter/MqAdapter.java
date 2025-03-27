package com.sinosoft.common.sync.adapter;

import com.sinosoft.common.mq.core.MessageSender;
import com.sinosoft.common.mq.manager.MqManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "mq", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MqAdapter {

    private final MqManager mqManager;

    /**
     * 发送同步消息
     */
    public String sendMessageWithProperties(String topic, String tag, String messageBody,
                                            String keys, Map<String, String> properties) {
        try {
            // 获取消息发送器
            MessageSender sender;
            if (topic.startsWith("rabbit_")) {
                sender = mqManager.getRabbitSender("default");
            } else {
                sender = mqManager.getKafkaSender("default");
            }

            // 发送消息
            // 注意：现有MessageSender接口不支持properties和tag，
            // 如果需要这些功能，可能需要扩展MessageSender
            boolean success;
            if (tag != null && !tag.isEmpty()) {
                // 使用tag作为routingKey
                success = sender.send(topic, tag, messageBody);
            } else {
                success = sender.send(topic, messageBody);
            }

            // 返回消息ID
            if (success) {
                String messageId = UUID.randomUUID().toString();
                log.debug("MQ消息发送成功: topic={}, messageId={}", topic, messageId);
                return messageId;
            } else {
                log.error("MQ消息发送失败: topic={}", topic);
                return null;
            }
        } catch (Exception e) {
            log.error("发送MQ消息异常: topic={}, error={}", topic, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 简化版发送方法
     */
    public String sendMessage(String topic, String messageBody) {
        return sendMessageWithProperties(topic, null, messageBody, null, null);
    }
}
