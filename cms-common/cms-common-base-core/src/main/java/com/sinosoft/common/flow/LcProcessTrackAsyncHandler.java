package com.sinosoft.common.flow;

import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.event.LcProcTrackEvent;
import com.sinosoft.common.service.ILcMainService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程轨迹异步处理器
 * <p>
 * 在主事务提交后异步保存流程轨迹记录，并发送通知
 *
 * @author: zzf
 * @create: 2025-03-08
 */
@Slf4j
@Component
public class LcProcessTrackAsyncHandler {

    @Autowired
    private ILcMainService lcMainService;

    /**
     * 事务提交后异步处理流程轨迹保存
     *
     * @param event 流程轨迹事件
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProcessTrackAfterCommit(LcProcTrackEvent event) {
        try {
            log.debug("异步处理流程轨迹记录, 流程类型: {}, 流程ID: {}, 操作类型: {}, 数据数量: {}",
                event.getLcSerialNo(), event.getLcId(), event.getCzType(),
                event.getIdsList() != null ? event.getIdsList().size() : 0);

            // 在新事务中保存流程轨迹
            lcMainService.insertLcProcess(event);



            log.debug("异步流程轨迹记录保存完成");
        } catch (Exception e) {
            // 异步处理异常不应影响主流程，但需要记录以便排查问题
            log.error("异步保存流程轨迹失败", e);
        }
    }

}
