package com.sinosoft.inter.api.bls.model;

import lombok.Data;

import java.util.List;

/**
 * 支付结果查询
 *
 * @author: zzf
 * @create: 2025-03-13 18:13
 */
public class BlsPaymentQuery {
    /**
     * 系统来源
     */
    private String fromSystem;

    /**
     * 支付明细查询列表
     */
    private List<PaymentDetailQueryList> paymentDetailQueryList;

    /**
     * 支付明细查询列表类
     */
    @Data
    public static class PaymentDetailQueryList {
        /**
         * 交易流水号
         */
        private String transSeq;

        /**
         * 交易列表
         */
        private List<TransList> transList;
    }

    /**
     * 交易列表类
     */
    @Data
    public static class TransList {
        /**
         * 子交易流水号
         */
        private String rdSeq;
    }
}
