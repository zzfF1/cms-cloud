package com.sinosoft.common.constant;

/**
 * @program: cms6
 * @description: 公共常量
 * @author: zzf
 * @create: 2024-01-28 17:13
 */
public class CommConstant {
    /**
     * 20个线程处理
     */
    public static final int PARSER_FOR_THREAD_NUM = 30;

    /**
     * 提数状态：00-提数开始
     */
    public static final String WAGE_LOG_STATE_INIT = "00";

    /**
     * 提数状态：10-CAL完成
     */
    public static final String WAGE_LOG_STATE_CAL = "10";

    /**
     * 提数状态：01-提数完成
     */
    public static final String WAGE_LOG_STATE_END = "01";
}
