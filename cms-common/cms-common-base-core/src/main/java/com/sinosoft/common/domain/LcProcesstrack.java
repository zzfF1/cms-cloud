package com.sinosoft.common.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.util.Date;

/**
 * 流程轨迹对象 lc_processtrack
 *
 * @author zzf
 * @date 2023-07-04
 */
@Data
@TableName("lc_processtrack")
public class LcProcesstrack {

    @Serial
    private static final long serialVersionUID = 2108805272144659908L;

    /**
     * 流水号
     */
    @TableId(value = "serial_no")
    private Long serialNo;

    /**
     * 流程类型
     */
    private Integer lcSerialNo;

    /**
     * 最后标志
     */
    private Integer lastFlag;

    /**
     * 操作类型 0保存 1提交 -1退回
     */
    private Integer czType;

    /**
     * 业务数据ID
     */
    private String dataId;

    /**
     * 流程值
     */
    private Integer lcId;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作时间
     */
    private Date makeDate;

    /**
     * 意见
     */
    private String yj;


}
