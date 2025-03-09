package com.sinosoft.common.core.exception.base;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: cms6
 * @description: 错误信息
 * @author: zzf
 * @create: 2023-12-15 14:24
 */
@Data
public class CErrors implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误主键
     */
    private String errorKey;

    /**
     * 错误明细
     */
    private List<CErrorInfo> msgList = new ArrayList<>();

    /**
     * @param msg 错误说明
     */
    public void addErrorInfo(String msg) {
        addErrorInfo(null, null, msg, null);
    }

    /**
     * @param val 值
     * @param msg 错误说明
     */
    public void addErrorInfo(String val, String msg) {
        addErrorInfo(null, val, msg, null);
    }

    /**
     * @param prefix 前缀
     * @param val    值
     * @param msg    错误说明
     */
    public void addErrorInfo(String prefix, String val, String msg) {
        addErrorInfo(prefix, val, msg, null);
    }

    /**
     * @param prefix  前缀 如标题:管理机构
     * @param value   值 如:8611
     * @param message 错误说明 如:管理机构不存在
     * @param example 例 86110101
     */
    public void addErrorInfo(String prefix, String value, String message, String example) {
        CErrorInfo errorInfo = new CErrorInfo();
        errorInfo.setPrefix(prefix);
        errorInfo.setValue(value);
        errorInfo.setMessage(message);
        errorInfo.setExample(example);
        msgList.add(errorInfo);
    }

    /**
     * 是否有错误
     *
     * @return true 有错误
     */
    public boolean haveError() {
        return !msgList.isEmpty();
    }
}
