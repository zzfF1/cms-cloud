package com.sinosoft.common.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 流程检查对象 lc_check
 *
 * @author zzf
 * @date 2023-07-02
 */
@Data
@TableName("lc_check")
public class LcCheck implements Serializable {

    @Serial
    private static final long serialVersionUID = 5505501606844947119L;

    /**
     * 检查id
     */
    @TableId(value = "serialno")
    private Integer serialno;

    /**
     * 检查说明
     */
    private String name;

    /**
     * 顺序号
     */
    private Integer recno;

    /**
     * 流程id
     */
    private Integer lcId;

    /**
     * 类型 0-提交进入时 1-退回时 2-提交时 3-退回进入时
     */
    private Integer type;

    /**
     * 检查条件
     */
    private String checkTj;

    /**
     * 检查类型
     */
    private Integer checkType;

    /**
     * 检查提示说明
     */
    private String checkMsg;
}
