package com.sinosoft.integration.api.core;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 响应抽象类
 *
 * @author: zzf
 * @create: 2025-03-26 15:18
 */
@Data
public abstract class ResponseBaseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 请求结果
     */
    private boolean success;
    /**
     * 状态
     */
    private String code;
    /**
     * 消息
     */
    private String message;
}
