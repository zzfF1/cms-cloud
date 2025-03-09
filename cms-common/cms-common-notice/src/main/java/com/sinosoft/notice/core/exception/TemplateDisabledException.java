package com.sinosoft.notice.core.exception;

/**
 * 模板停用异常
 */
public class TemplateDisabledException extends NotificationException {
    public TemplateDisabledException(String templateCode) {
        super("TEMPLATE_DISABLED", "通知模板已停用: " + templateCode);
    }
}
