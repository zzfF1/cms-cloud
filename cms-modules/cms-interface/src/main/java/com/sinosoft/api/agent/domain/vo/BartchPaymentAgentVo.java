package com.sinosoft.api.agent.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 批量代付返回信息
 *
 * @author: yjq
 * @create: 2025-03-11 11:37
 */
@Data
public class BartchPaymentAgentVo {

    /**
     * 结果
     */
    private String resualt;

    /**
     * 结果原因
     */
    private String resultReason;

    /**
     * 交易流水号
     */
    private String transSeq;

    /**
     * 系统来源
     */
    private String fromSystem;

    /**
     * 交易笔数
     */
    private Integer totalCount;

    /**
     * 交易总金额
     */
    private BigDecimal totalAmount;

    /**
     * 子交易流水号list
     */
    private ArrayList<SerialNumberSonVo> paymentDetailList;

}
