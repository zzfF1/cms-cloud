package com.sinosoft.common.flow;

/**
 * 流程操作类型常量
 *
 * @author zzf
 * @create 2023-07-02
 */
public class LcActionType {
    /** 提交后续处理 */
    public static final int AFTER_SUBMIT = 0;
    /** 退回前处理 */
    public static final int BEFORE_RETURN = 1;
    /** 提交前处理 */
    public static final int BEFORE_SUBMIT = 2;
    /** 退回后处理 */
    public static final int AFTER_RETURN = 3;
}
