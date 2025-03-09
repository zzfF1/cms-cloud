package com.sinosoft.common.core.exception.base;

import com.sinosoft.common.core.utils.StringUtils;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: cms6
 * @description: 详细错误
 * @author: zzf
 * @create: 2023-12-15 14:24
 */
@Data
public class CErrorInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 前缀
     */
    private String prefix;
    /**
     * 错误值
     */
    private String value;
    /**
     * 错误说明
     */
    private String message;
    /**
     * 案例结果
     */
    private String example;

    public String getErrorMsg() {
        StringBuilder msg = new StringBuilder();
        //前缀不为空
        if (StringUtils.isNotBlank(prefix)) {
            msg.append("{").append(prefix).append("}");
        }
        //值不为空
        if (StringUtils.isNotBlank(value)) {
            msg.append("[").append(value).append("]");
        }
        if (StringUtils.isNotBlank(message)) {
            msg.append(message);
        }
        //例
        if (StringUtils.isNotBlank(example)) {
            msg.append("[例:").append(example).append("]");
        }
        return msg.toString();
    }

}
