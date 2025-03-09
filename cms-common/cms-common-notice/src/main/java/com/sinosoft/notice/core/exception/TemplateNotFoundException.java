package com.sinosoft.notice.core.exception;

/**
 * 模板未找到异常
 */
public class TemplateNotFoundException extends NotificationException {
    public TemplateNotFoundException(String templateCode) {
        super("TEMPLATE_NOT_FOUND", "通知模板不存在: " + templateCode);
    }
}
