package com.sinosoft.common.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 流程动作对象 lc_dz
 *
 * @author zzf
 * @date 2023-07-02
 */
@Data
@TableName("lc_dz")
public class LcDz implements Serializable {

    @Serial
    private static final long serialVersionUID = 7157896799237590677L;

    /**
     * 动作id
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
     * 动作条件
     */
    private String dz;

    /**
     * 动作类型
     */
    private Integer dzType;


}
