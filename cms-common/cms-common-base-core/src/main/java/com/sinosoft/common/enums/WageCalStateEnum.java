package com.sinosoft.common.enums;

/**
 * 佣金计算状态枚举
 */
public enum WageCalStateEnum {
    /**
     * 未计算
     */
    NOT_CAL(0),
    /**
     * 计算中
     */
    CAL_ING(5),
    /**
     * 计算完成
     */
    CAL_DONE(10),
    /**
     * 审批通过
     */
    APPROVE_PASS(15),
    /**
     * 审批驳回
     */
    APPROVE_REJECT(20),
    /**
     * 计算失败
     */
    CAL_FAIL(99),

    /**
     * 薪资计算
     */
    WAGE_CAL(14),
    /**
     * 合并计税
     */
    WAGE_TAX(15),
    /**
     * 审核发放
     */
    WAGE_CONFIRM(21),
    /**
     * 未匹配到
     */
    UNKNOWN(-1);

    private final int value;

    WageCalStateEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * 是否允许计算
     *
     * @param calState 计算状态
     * @return true 允许计算 false 不允许计算
     */
    public static boolean isCalState(WageCalStateEnum calState) {
        return NOT_CAL == calState;
    }

    /**
     * 校验是否允许被计算
     *
     * @param calState 计算状态
     * @return 有值代表存在错误信息 空代表校验通过
     */
    public static String checkCalState(WageCalStateEnum calState) {
        String errorMsg = "";
        switch (calState) {
            case CAL_ING -> errorMsg = "计算中,不允许重复计算!";
            case CAL_DONE -> errorMsg = "计算完成,不允许重复计算!";
            case APPROVE_PASS -> errorMsg = "审批通过,不允许计算!";
            case APPROVE_REJECT, CAL_FAIL -> errorMsg = "计算完成,不允许计算!";
            case UNKNOWN -> errorMsg = "未匹配到计算状态,请联系管理员!";
        }
        return errorMsg;
    }

    /**
     * 是否允许删除
     *
     * @param calState 计算状态
     * @return true 允许删除 false 不允许删除
     */
    public static boolean isDelCalState(WageCalStateEnum calState) {
        return CAL_DONE == calState || APPROVE_REJECT == calState;
    }


    /**
     * 根据value获取枚举
     *
     * @param value 值
     * @return 枚举
     */
    public static WageCalStateEnum getEnum(int value) {
        for (WageCalStateEnum state : WageCalStateEnum.values()) {
            if (state.getValue() == value) {
                return state;
            }
        }
        return UNKNOWN;
    }
}
