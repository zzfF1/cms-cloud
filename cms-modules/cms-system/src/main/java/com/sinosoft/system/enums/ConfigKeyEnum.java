package com.sinosoft.system.enums;

import cn.hutool.core.util.NumberUtil;

import java.util.function.Function;

public enum ConfigKeyEnum {

    //连续登录失败最大次数
    logonFailNums("user.password.maxRetryCount", value -> {
        if (!NumberUtil.isInteger(value)) {
            return "参数值应填写整数";
        }
        Integer integer = Integer.valueOf(value);
        if (integer < 1 || integer > 10) {
            return "参数值应在[1,10]整数范围内";
        }
        return null;
    }),

    //登录失败锁定时长（分钟）
    logonFailLockSeconds("user.password.lockTime", value -> {
        if (!NumberUtil.isInteger(value)) {
            return "参数值应填写整数";
        }
        Integer integer = Integer.valueOf(value);
        if (integer < 20) {
            return "参数值应大于等于20";
        }
        return null;
    }),

    //最大在线用户数
    maxOnlineNums("sys.online.maxNum", value -> {
        if (!NumberUtil.isInteger(value)) {
            return "参数值应填写整数";
        }
        Integer integer = Integer.valueOf(value);
        if (integer < 1) {
            return "参数值应大于等于1";
        }
        return null;
    }),

    //日志存储容量上限
    maxLogCapacity("sys.log.maxCapacity", value -> {
        if (!NumberUtil.isInteger(value)) {
            return "参数值应填写整数";
        }
        Integer integer = Integer.valueOf(value);
        if (integer < 1) {
            return "参数值应大于等于1";
        }
        return null;
    }),

    //密码过期间隔
    pwdTimeOutDays("sys.password.validity.days", value -> {
        if (!NumberUtil.isInteger(value)) {
            return "参数值应填写整数";
        }
        Integer integer = Integer.valueOf(value);
        if (integer < 90) {
            return "参数值应大于等于90";
        }
        return null;
    }),

    ;

    private String key;

    private Function<String, String> validFunc;

    ConfigKeyEnum(String key, Function<String, String> validFunc) {
        this.key = key;
        this.validFunc = validFunc;
    }


    /**
     * 对数值进行校验，不通过，则返回错误信息；通过，则返回null
     *
     * @param key
     * @param value
     * @return
     */
    public static String doValid(String key, String value) {
        for (ConfigKeyEnum configKeyEnum : ConfigKeyEnum.values()) {
            if (configKeyEnum.key.equals(key)) {
                return configKeyEnum.validFunc.apply(value);
            }
        }
        return null;
    }


}
