package com.sinosoft.common.enums;

import lombok.Getter;

/**
 * 流程类型系列号枚举
 */
@Getter
public enum LcTypeSerNoEnum {
    /**
     * 流程类型主键
     */
    WAGE_YT_IND(5, "个险佣金预提", 1, 1),
    WAGE_ZS_IND(8, "个险佣金正式", 1101, 1108),

    AGENT_RZ_IND(32, "个险业务员入职", 2701, 2705),
    AGENT_LZ_IND(33, "个险业务员离职", 1, 1),
    AGENT_MOVE_IND(60, "个险人员异动", 6001, 6004),

    AGENT_RZ_BANK(33,"银保业务员入职",2901,2905),
    AGENT_MOVE_BANK(72, "银保人员异动", 7201, 7204),
    AGENT_LZ_BANK(34, "银保业务员离职", 2601, 2602),
    CHARGE_JS_BANK(47,"银保手续费结算",4701,4705),
    WAGE_ZS_BANK(9, "银保佣金正式", 2101, 2108),

    AGENT_RZ_GRP(31,"团险业务员入职",2801,2807),
    AGENT_MOVE_GRP(70, "团险人员调动", 7001, 7007),
    WAGE_ZS_GRP(11, "团险佣金正式", 1901, 1908),
    CHARGE_JS_GRP(45,"团险手续费结算",4501,4504),
    CHARGE_FA_GRP(46,"团险手续费方案",4601,4604),
    AGENT_ASSESS_GRP(80,"团险考核审批",8001,8007),
    COM_CONT_GRP(90,"协议审核",9000,9002),
    REWARD_PUNISH_GRP(12,"团险加扣款审批",1200,1202),
    CHARGE_ADJUST_GRP(91, "团险中介手续费调整", 3001, 3004),

    PLUS_REWARD_GRP(92,"附加绩效基础数据审批",9201,9207),

    UNKNOWN(-1, "", 1, 1);


    /**
     * 流程代码
     */
    private int code;
    /**
     * 流程名称
     */
    private String name;
    /**
     * 首流程ID
     */
    private int firstLc;
    /**
     * 结束流程ID
     */
    private int endLc;

    /**
     * @param code    流程代码
     * @param name    流程名称
     * @param firstLc 首个流程
     * @param endLc   结束流程
     */
    LcTypeSerNoEnum(int code, String name, int firstLc, int endLc) {
        this.code = code;
        this.name = name;
        this.firstLc = firstLc;
        this.endLc = endLc;
    }

    /**
     * 流程类型
     *
     * @param lcTypeSerNo
     * @return 未找到默认 UNKNOWN
     */
    public static LcTypeSerNoEnum getEnumType(int lcTypeSerNo) {
        LcTypeSerNoEnum[] enums = LcTypeSerNoEnum.values();
        for (LcTypeSerNoEnum um : enums) {
            if (um.getCode() == lcTypeSerNo) {
                return um;
            }
        }
        return LcTypeSerNoEnum.UNKNOWN;
    }
}
