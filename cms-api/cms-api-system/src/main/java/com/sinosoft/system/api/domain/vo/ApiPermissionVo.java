package com.sinosoft.system.api.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * api权限视图对象
 *
 * @author: zzf
 * @create: 2025-02-17 22:22
 */
@Data
public class ApiPermissionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * api地址
     */
    private String api;        // API 地址
    /**
     * 请求方式
     */
    private String method;
}
