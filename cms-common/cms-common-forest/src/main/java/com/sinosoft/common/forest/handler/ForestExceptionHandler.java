package com.sinosoft.common.forest.handler;

import cn.hutool.http.HttpStatus;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.forest.exception.ForestException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Forest异常处理器
 *
 * @author AprilWind
 */
@Slf4j
@RestControllerAdvice
public class ForestExceptionHandler {

    /**
     * 请求第三方异常
     */
    @ExceptionHandler(ForestException.class)
    public R<Void> handleForestException(ForestException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',forest请求第三方异常." + requestURI, e.getMessage());
        return R.fail(HttpStatus.HTTP_UNAVAILABLE, "抱歉，服务暂时无法响应您的请求...");
    }

}
