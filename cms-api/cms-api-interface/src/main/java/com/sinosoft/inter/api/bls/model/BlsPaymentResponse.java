package com.sinosoft.inter.api.bls.model;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * /**
 * 支付结果类
 */
@Data
@ToString
public class BlsPaymentResponse {
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
     * 支付明细列表
     */
    private List<PaymentDetail> paymentDetailList;

    /**
     * 支付明细类
     */
    @Data
    static class PaymentDetail {
        /**
         * 子交易流水号
         */
        private String rdSeq;

    }
}
