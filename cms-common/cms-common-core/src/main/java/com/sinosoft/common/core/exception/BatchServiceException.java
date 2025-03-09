package com.sinosoft.common.core.exception;

import com.sinosoft.common.core.constant.HttpStatus;
import com.sinosoft.common.core.exception.base.CErrors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;

/**
 * 批量错误业务异常
 *
 * @author cms
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public final class BatchServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private List<CErrors> messages;


    /**
     * @param messages
     */
    public BatchServiceException(List<CErrors> messages) {
        this.code = HttpStatus.BATCH_ERROR;
        this.messages = messages;
    }

    /**
     * @param messages
     * @param code
     */
    public BatchServiceException(List<CErrors> messages, Integer code) {
        this.messages = messages;
        this.code = code;
    }


    @Override
    public String getMessage() {
        return "批量错误";
    }

    public BatchServiceException setMessage(List<CErrors> messages) {
        this.messages = messages;
        return this;
    }

    public Integer getCode() {
        return code;
    }
}
