package com.sinosoft.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.sinosoft.common.schema.common.domain.LcMain;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;



/**
 * 流程定义视图对象 lc_main
 *
 * @author zzf
 * @date 2023-11-12
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = LcMain.class)
public class LcMainVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 流水号
     */
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
     * 业务表是不是多主键 1多主键 0单主键
     */
    private Integer mulkey;
}
