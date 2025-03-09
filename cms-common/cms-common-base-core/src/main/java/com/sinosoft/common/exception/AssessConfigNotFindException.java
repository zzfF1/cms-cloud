package com.sinosoft.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * @program: cms6
 * @description: 考核配置缺失异常
 * @author: zzf
 * @create: 2023-11-17 11:14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AssessConfigNotFindException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    /**
     * @param message
     */
    public AssessConfigNotFindException(String message) {
        this.message = message;
    }

    /**
     * @param message
     * @param code
     */
    public AssessConfigNotFindException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
