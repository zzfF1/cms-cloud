package com.sinosoft.common.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: cms6
 * @description: 佣金计算人员
 * @author: zzf
 * @create: 2023-07-16 13:19
 */
@Data
public class WageCalAgentDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 7875166086064431511L;
    /**
     * 管理机构
     */
    private String manageCom;
    /**
     * 工号
     */
    private String agentCode;
    /**
     * 职级
     */
    private String agentGrade;
    /**
     * 职级序列
     */
    private String agentSeries;
    /**
     * 销售机构
     */
    private String agentGroup;
    /**
     * 机构代码
     */
    private String branchCode;
    /**
     * 入司时间
     */
    private Date employDate;
    /**
     * 人员性质
     */
    private Integer personType;
    /**
     * 佣金年月
     */
    private String indexCalNo;
    /**
     * 佣金计算类型
     */
    private String indexCalType;
    /**
     * 计算批次号
     */
    private String calBatch;
    /**
     * 运行ID
     */
    private String runId;
    /**
     * 基本法版本
     */
    private int version;
    /**
     * 当前计算步长
     */
    private Integer currentStep;
}
