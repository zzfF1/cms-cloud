package com.sinosoft.api.agent.domain.bo;

import lombok.Data;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 批量代付请求信息
 */
@Data
public class BartchPaymentAgentBo {

    //交易时间
    private Date transTime;
    //交易流水号
    private String transSeq;
    //系统来源
    private String fromSystem;
    //交易笔数
    private int totalCount;
    //交易总金额
    private BigDecimal totalAmount;
    //明细集合
    private List<TransactionDetailsBo> paymentDetailList;


}
