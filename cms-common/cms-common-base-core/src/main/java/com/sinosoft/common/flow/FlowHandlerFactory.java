package com.sinosoft.common.flow;

import com.sinosoft.common.core.utils.SpringUtils;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * 流程处理器工厂
 *
 * @author zzf
 * @create 2023-07-02
 */
@Slf4j
@Component
public class FlowHandlerFactory {

    /**
     * 获取流程检查处理器
     *
     * @param handlerClass 处理器类名
     * @return 流程检查处理器
     */
    public ILcCheckHandler getCheckHandler(String handlerClass) {
        try {
            return SpringUtils.getBean(handlerClass, ILcCheckHandler.class);
        } catch (Exception e) {
            log.error("获取检查处理器失败: {}", handlerClass, e);
            throw new RuntimeException("获取检查处理器失败: " + handlerClass, e);
        }
    }

    /**
     * 获取流程动作处理器
     *
     * @param handlerClass 处理器类名
     * @return 流程动作处理器
     */
    public ILcDzHandler getActionHandler(String handlerClass) {
        try {
            return SpringUtils.getBean(handlerClass, ILcDzHandler.class);
        } catch (Exception e) {
            log.error("获取动作处理器失败: {}", handlerClass, e);
            throw new RuntimeException("获取动作处理器失败: " + handlerClass, e);
        }
    }
}
