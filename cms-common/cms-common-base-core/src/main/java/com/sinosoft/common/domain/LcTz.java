package com.sinosoft.common.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 流程跳转对象 lc_tz
 *
 * @author zzf
 * @date 2023-07-02
 */
@Data
@TableName("lc_tz")
public class LcTz implements Serializable {

    @Serial
    private static final long serialVersionUID = 5867014617223374065L;

    /**
     * 跳转id
     */
    @TableId(value = "serialno")
    private Integer serialno;

    /**
     * 顺序号
     */
    private Integer recno;

    /**
     * 流程id
     */
    private Integer lcId;

    /**
     * 下一个流程节点
     */
    private Integer lcNextId;

    /**
     * 跳转条件
     */
    private String tzTj;

    /**
     * 跳转说明
     */
    private String sm;


}
