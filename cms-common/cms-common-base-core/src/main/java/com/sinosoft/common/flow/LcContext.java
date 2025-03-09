package com.sinosoft.common.flow;

import com.sinosoft.system.api.model.LoginUser;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程上下文，封装流程相关参数
 *
 * @author zzf
 * @create 2023-07-02
 */
public class LcContext {
    private final LoginUser loginUser;
    private final Map<String, Object> parameters;
    private final String operator;

    private LcContext(LoginUser loginUser, Map<String, Object> parameters) {
        this.loginUser = loginUser;
        this.parameters = parameters != null ? parameters : new HashMap<>();
        this.operator = loginUser != null ? loginUser.getUsername() : "";
    }

    /**
     * 创建流程上下文
     *
     * @param loginUser  登录用户
     * @param parameters 参数
     * @return 流程上下文
     */
    public static LcContext of(LoginUser loginUser, Map<String, Object> parameters) {
        return new LcContext(loginUser, parameters);
    }

    /**
     * 创建流程上下文（无参数）
     *
     * @param loginUser 登录用户
     * @return 流程上下文
     */
    public static LcContext of(LoginUser loginUser) {
        return new LcContext(loginUser, new HashMap<>());
    }

    /**
     * 获取登录用户
     *
     * @return 登录用户
     */
    public LoginUser getLoginUser() {
        return loginUser;
    }

    /**
     * 获取参数
     *
     * @return 参数
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * 添加参数
     *
     * @param key   键
     * @param value 值
     * @return 当前上下文
     */
    public LcContext addParameter(String key, Object value) {
        this.parameters.put(key, value);
        return this;
    }

    /**
     * 添加多个参数
     *
     * @param params 参数Map
     * @return 当前上下文
     */
    public LcContext addParameters(Map<String, Object> params) {
        if (params != null) {
            this.parameters.putAll(params);
        }
        return this;
    }

    /**
     * 获取操作人
     *
     * @return 操作人
     */
    public String getOperator() {
        return operator;
    }
}
