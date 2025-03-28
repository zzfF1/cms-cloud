package com.sinosoft.integration.api.bls.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * 支付查询响应
 */
@Data
@ToString
public class BlsPaymentQueryResponse {
    /**
     * 结果
     */
    private String resualt;

    /**
     * 结果原因
     */
    private String resultReason;

    /**
     * 查询结果
     */
    private List<TransQuery> transQuery;

    /**
     * 查询参数明细类
     */
    @Data
    public static class TransQuery {
        /**
         * 交易流水号
         */
        private String transSeq;

        /**
         * 查询批次状态
         * 0-批次不存在，2-查询成功
         */
        private String reqSeqState;

        /**
         * 交易列表
         */
        private List<TransList> transList;
    }

    /**
     * 交易明细类
     */
    @Data
    public static class TransList {
        /**
         * 子交易流水号
         */
        private String rdSeq;

        /**
         * 支付状态
         * 2-成功，3-失败，6-退票
         */
        private String transState;

        /**
         * 企业方账户
         */
        private String corpAct;

        /**
         * 企业方账户所在机构
         */
        private String corpEntity;

        /**
         * 企业方账户所在银行
         */
        private String corpBank;

        /**
         * 支付信息编码
         */
        private String payInfoCode;

        /**
         * 支付信息描述
         */
        private String payInfo;

        /**
         * 交易失败类型
         * 0-正常，1-写入失败，2-率工作，3-支付失败
         */
        private String failType;

        /**
         * 支付确认时间
         */
        private Date payMadeDate;

        /**
         * 对账号
         */
        @JsonProperty("abstract")
        private String Abstract;
    }
}
