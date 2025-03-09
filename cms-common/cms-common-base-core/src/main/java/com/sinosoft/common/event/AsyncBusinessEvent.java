package com.sinosoft.common.event;

import lombok.Data;
import lombok.ToString;
import com.sinosoft.system.api.model.LoginUser;

import java.io.Serial;
import java.io.Serializable;

/**
 * 异步业务事件
 *
 * @author: zzf
 * @create: 2023-07-04 16:53
 */
@ToString
@Data
public class AsyncBusinessEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 业务代码
     */
    private String businessCode;
    /**
     * 批次号
     */
    private String batchNo;
    /**
     * 业务ID
     */
    private String businessId;
    /**
     * 登录用户
     */
    LoginUser loginUser;
    /**
     * 业务数据
     */
    private String jsonData;
}
