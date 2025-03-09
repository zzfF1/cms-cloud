package com.sinosoft.notice.core.exception;

import com.sinosoft.common.core.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 通知系统全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class NotificationExceptionHandler {

    /**
     * 处理通知系统异常
     */
    @ExceptionHandler(NotificationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleNotificationException(NotificationException e) {
        log.error("通知系统异常: {}", e.getErrorMessage(), e);
        return R.fail(e.getErrorMessage());
    }

    /**
     * 处理模板未找到异常
     */
    @ExceptionHandler(TemplateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<Void> handleTemplateNotFoundException(TemplateNotFoundException e) {
        log.error("模板未找到: {}", e.getErrorMessage());
        return R.fail(e.getErrorMessage());
    }

    /**
     * 处理模板停用异常
     */
    @ExceptionHandler(TemplateDisabledException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleTemplateDisabledException(TemplateDisabledException e) {
        log.error("模板已停用: {}", e.getErrorMessage());
        return R.fail(e.getErrorMessage());
    }

    /**
     * 处理模板渲染异常
     */
    @ExceptionHandler(TemplateRenderException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleTemplateRenderException(TemplateRenderException e) {
        log.error("渲染模板异常: {}", e.getErrorMessage(), e);
        return R.fail(e.getErrorMessage());
    }

    /**
     * 处理通知发送异常
     */
    @ExceptionHandler(NotificationSendException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleNotificationSendException(NotificationSendException e) {
        log.error("发送通知异常: {}", e.getErrorMessage(), e);
        return R.fail(e.getErrorMessage());
    }

    /**
     * 处理接收人查找异常
     */
    @ExceptionHandler(RecipientFindException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleRecipientFindException(RecipientFindException e) {
        log.error("查找接收人异常: {}", e.getErrorMessage(), e);
        return R.fail(e.getErrorMessage());
    }
}
