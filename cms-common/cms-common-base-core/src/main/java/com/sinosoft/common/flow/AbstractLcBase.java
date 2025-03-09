package com.sinosoft.common.flow;

import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.domain.LcMain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: cms6
 * @description: 流程动作执行类
 * @author: zzf
 * @create: 2023-07-02 15:44
 */
@Slf4j
public abstract class AbstractLcBase {

    /**
     * 失败信息
     */
    private String errorMsg = "";
    /**
     * 参数
     */
    private Map<String, Object> m_Parms = new HashMap<>();

    /**
     * 设置参数
     *
     * @param parms 提交时参数
     */
    public void setParms(Map<String, Object> parms) {
        this.m_Parms.putAll(parms);
    }

    /**
     * 返回错误信息
     *
     * @return 错误信息
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 设置错误信息
     *
     * @param errorMsg 错误信息
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * 执行动作
     *
     * @param nLcId  流程ID
     * @param lcMain 流程主表
     * @param ids    业务数据集合
     * @return true成功 false失败
     */
    public abstract boolean handler(int nLcId, LcMain lcMain, List<String> ids);
}
