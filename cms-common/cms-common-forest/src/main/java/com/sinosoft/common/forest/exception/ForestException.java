package com.sinosoft.common.forest.exception;


import com.sinosoft.common.core.exception.base.BaseException;

import java.io.Serial;

/**
 * Forest第三方请求异常类
 *
 * @author AprilWind
 */
public class ForestException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ForestException(String msg) {
        super("forest", msg);
    }

    public ForestException(String code, String msg) {
        super("forest", code, null, msg);
    }

    public ForestException(String code, Object[] args) {
        super("forest", code, args, null);
    }

}
