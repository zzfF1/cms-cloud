package com.sinosoft.notice.core.exception;

/**
 * 模板渲染异常
 */
public class TemplateRenderException extends NotificationException {
    public TemplateRenderException(String message, Throwable cause) {
        super("TEMPLATE_RENDER_ERROR", "渲染模板异常: " + message, cause);
    }
}
