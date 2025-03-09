package com.sinosoft.common.log.event;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录事件
 *
 * @author zzf
 */

@Data
public class LogininforEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 登录状态 0成功 1失败
     */
    private String status;

    /**
     * 提示消息
     */
    private String message;

    /**
     * 其他参数
     */
    private Object[] args;

}
