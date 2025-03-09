package com.sinosoft.common.core;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 接口返回对象
 *
 * @author: zzf
 * @create: 2024-12-10 22:29
 */
@Data
public class WebInterfaceResult {
    /**
     * 接口结果
     */
    private boolean flag = false;
    /**
     * 接口返回结果
     */
    private String resultVal;
    /**
     * 错位代码
     */
    public String errorCode;
    /**
     * 错误信息
     */
    public String errorMsg;
    /**
     * 结果
     */
    private Map<String, Object> mapResult = new HashMap<>();

    /**
     * 设置返回代码
     *
     * @param strCode 返回代码
     * @param strMsg  返回信息
     */
    public void setResultCode(String strCode, String strMsg) {
        this.errorCode = strCode;
        this.errorMsg = strMsg;
    }

    /**
     * 添加结果
     *
     * @param key  键
     * @param oVal 值
     */
    public void put(String key, Object oVal) {
        this.mapResult.put(key, oVal);
    }

    /**
     * 获取结果
     *
     * @param key 键
     * @return Object
     */
    public Object getMapVal(String key) {
        return this.mapResult.get(key);
    }
}
