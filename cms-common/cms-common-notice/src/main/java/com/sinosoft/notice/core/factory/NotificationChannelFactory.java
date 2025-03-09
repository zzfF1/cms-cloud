package com.sinosoft.notice.core.factory;

import com.sinosoft.notice.core.strategy.NotificationChannelStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;

/**
 * 通知渠道策略工厂
 */
@Slf4j
@Component
public class NotificationChannelFactory {

    private final Map<String, NotificationChannelStrategy> strategies = new HashMap<>();

    public NotificationChannelFactory(List<NotificationChannelStrategy> strategyList) {
        for (NotificationChannelStrategy strategy : strategyList) {
            String channelCode = strategy.getChannelCode();
            strategies.put(channelCode, strategy);
            log.info("注册通知渠道策略: {}, {}", channelCode, strategy.getChannelName());
        }
    }

    /**
     * 获取通知渠道策略
     *
     * @param channelCode 渠道代码
     * @return 渠道策略
     */
    public NotificationChannelStrategy getStrategy(String channelCode) {
        NotificationChannelStrategy strategy = strategies.get(channelCode);
        if (strategy == null) {
            throw new IllegalArgumentException("未知的通知渠道: " + channelCode);
        }
        return strategy;
    }

    /**
     * 获取所有可用的渠道策略
     *
     * @return 渠道策略映射表
     */
    public Map<String, NotificationChannelStrategy> getAllStrategies() {
        return new HashMap<>(strategies);
    }

    /**
     * 获取所有可用的渠道代码
     *
     * @return 渠道代码集合
     */
    public Set<String> getAllChannelCodes() {
        return Collections.unmodifiableSet(strategies.keySet());
    }

    /**
     * 获取用户启用的渠道
     *
     * @param userId           用户ID
     * @param userSettings     用户设置
     * @param notificationType 通知类型
     * @return 启用的渠道列表
     */
    public List<NotificationChannelStrategy> getEnabledChannelsForUser(
        Long userId, Map<String, Object> userSettings, String notificationType) {
        return strategies.values().stream()
            .filter(strategy -> strategy.isEnabledForUser(userId, userSettings, notificationType))
            .collect(Collectors.toList());
    }

    /**
     * 判断渠道是否存在
     *
     * @param channelCode 渠道代码
     * @return 是否存在
     */
    public boolean hasChannel(String channelCode) {
        return strategies.containsKey(channelCode);
    }
}
