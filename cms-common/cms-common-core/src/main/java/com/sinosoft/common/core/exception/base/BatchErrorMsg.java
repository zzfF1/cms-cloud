package com.sinosoft.common.core.exception.base;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @program: cms6
 * @description: 批量错误输出
 * @author: zzf
 * @create: 2023-12-19 15:30
 */
@Data
public class BatchErrorMsg implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 错误说明
     */
    String msg;
    /**
     * 子错误
     */
    List<BatchErrorMsg> children;
}
