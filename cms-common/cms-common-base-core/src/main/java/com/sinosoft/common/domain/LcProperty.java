package com.sinosoft.common.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 流程属性对象 lc_property
 *
 * @author zzf
 * @date 2023-11-13
 */
@Data
@TableName("lc_property")
public class LcProperty implements Serializable {

    @Serial
    private static final long serialVersionUID = -21841022343628267L;

    /**
     * 流水号
     */
    @TableId(value = "serial_no")
    private Long SerialNo;

    /**
     * 流程ID
     */
    private Integer lcId;

    /**
     * 属性编码
     */
    private String attrName;

    /**
     * 属性类型 0-提交进入时 1-退回时 2-提交时 3-退回进入时
     */
    private Integer Type;

    /**
     * 值
     */
    private String Val;


}
