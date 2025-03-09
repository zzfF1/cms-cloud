package com.sinosoft.common.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 流程类型定义对象 lc_main
 *
 * @author zzf
 * @date 2023-07-02
 */
@Data
@TableName("lc_main")
public class LcMain implements Serializable {

    @Serial
    private static final long serialVersionUID = 6084024157196059125L;

    /**
     * 流水号
     */
    @TableId(value = "serialno")
    private Integer serialno;

    /**
     * 流程类型名称
     */
    private String name;

    /**
     * 顺序号
     */
    private Integer recno;

    /**
     * 业务表
     */
    private String lcTable;

    /**
     * 流程字段
     */
    private String lcField;

    /**
     * 业务表主键
     */
    private String idField;

    /**
     * 驳回字段
     */
    private String rejectField;

    /**
     * 驳回原因字段
     */
    private String rejectField2;

    /**
     * 业务表是不是多主键 1多主键 0单主键
     */
    private Integer mulkey;


}
