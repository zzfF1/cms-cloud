package com.sinosoft.notice.core.factory;

import com.sinosoft.notice.core.strategy.NotificationMergeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 通知合并策略工厂
 */
@Slf4j
@Component
public class NotificationMergeStrategyFactory {

    private final Map<String, NotificationMergeStrategy> strategies = new HashMap<>();

    public NotificationMergeStrategyFactory(List<NotificationMergeStrategy> strategyList) {
        for (NotificationMergeStrategy strategy : strategyList) {
            String strategyCode = strategy.getStrategyCode();
            strategies.put(strategyCode, strategy);
            log.info("注册通知合并策略: {}, {}", strategyCode, strategy.getStrategyName());
        }
    }

    /**
     * 获取通知合并策略
     *
     * @param strategyCode 策略代码
     * @return 合并策略
     */
    public Optional<NotificationMergeStrategy> getStrategy(String strategyCode) {
        if (strategyCode == null || strategyCode.equals("none")) {
            return Optional.empty();
        }

        NotificationMergeStrategy strategy = strategies.get(strategyCode);
        if (strategy == null) {
            log.warn("未知的通知合并策略: {}", strategyCode);
            return Optional.empty();
        }
        return Optional.of(strategy);
    }

    /**
     * 获取所有可用的合并策略
     *
     * @return 合并策略映射表
     */
    public Map<String, NotificationMergeStrategy> getAllStrategies() {
        return new HashMap<>(strategies);
    }

    /**
     * 判断策略是否存在
     *
     * @param strategyCode 策略代码
     * @return 是否存在
     */
    public boolean hasStrategy(String strategyCode) {
        return strategyCode != null && strategies.containsKey(strategyCode);
    }
}
