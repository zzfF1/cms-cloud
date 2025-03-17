package com.sinosoft.common.mq.util;

import com.sinosoft.common.mq.core.MqMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * MQ追踪工具类
 */
public class MqTraceUtil {

    private static final Logger logger = LoggerFactory.getLogger(MqTraceUtil.class);

    private static final String TRACE_ID_KEY = "X-Trace-Id";
    private static final String SPAN_ID_KEY = "X-Span-Id";
    private static final String PARENT_SPAN_ID_KEY = "X-Parent-Span-Id";
    private static final String MDC_TRACE_ID_KEY = "traceId";
    private static final String MDC_SPAN_ID_KEY = "spanId";

    /**
     * 为消息添加追踪信息
     */
    public void addTraceInfo(MqMessage message) {
        if (message == null) {
            return;
        }

        try {
            // 获取当前线程的TraceId，如果没有则创建
            String traceId = MDC.get(MDC_TRACE_ID_KEY);
            if (!StringUtils.hasText(traceId)) {
                traceId = generateTraceId();
                MDC.put(MDC_TRACE_ID_KEY, traceId);
            }

            // 获取当前线程的SpanId，作为父SpanId
            String parentSpanId = MDC.get(MDC_SPAN_ID_KEY);
            if (!StringUtils.hasText(parentSpanId)) {
                parentSpanId = generateSpanId();
            }

            // 生成新的SpanId
            String spanId = generateSpanId();

            // 添加到消息头
            message.addHeader(TRACE_ID_KEY, traceId);
            message.addHeader(SPAN_ID_KEY, spanId);
            message.addHeader(PARENT_SPAN_ID_KEY, parentSpanId);

            logger.debug("Added trace info to message: traceId={}, spanId={}, parentSpanId={}",
                traceId, spanId, parentSpanId);
        } catch (Exception e) {
            logger.warn("Failed to add trace info to message", e);
        }
    }

    /**
     * 从消息中提取追踪信息并设置到MDC
     */
    public void extractTraceInfo(MqMessage message) {
        if (message == null) {
            return;
        }

        try {
            Object traceId = message.getHeader(TRACE_ID_KEY);
            Object spanId = message.getHeader(SPAN_ID_KEY);

            if (traceId != null) {
                MDC.put(MDC_TRACE_ID_KEY, traceId.toString());
            }

            if (spanId != null) {
                // 将接收到的SpanId设置为当前的父SpanId
                String newSpanId = generateSpanId();
                MDC.put(MDC_SPAN_ID_KEY, newSpanId);
                message.addHeader(PARENT_SPAN_ID_KEY, spanId.toString());
            }

            logger.debug("Extracted trace info from message: traceId={}, spanId={}",
                MDC.get(MDC_TRACE_ID_KEY), MDC.get(MDC_SPAN_ID_KEY));
        } catch (Exception e) {
            logger.warn("Failed to extract trace info from message", e);
        }
    }

    /**
     * 清理MDC中的追踪信息
     */
    public void clearTraceInfo() {
        try {
            MDC.remove(MDC_TRACE_ID_KEY);
            MDC.remove(MDC_SPAN_ID_KEY);
        } catch (Exception e) {
            logger.warn("Failed to clear trace info", e);
        }
    }

    /**
     * 生成TraceId
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成SpanId
     */
    private String generateSpanId() {
        return Long.toHexString(System.currentTimeMillis()) +
            Long.toHexString(Thread.currentThread().getId()) +
            Integer.toHexString(UUID.randomUUID().hashCode() & 0xFFFF);
    }
}
