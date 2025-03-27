package com.sinosoft.api.agent.domain.bo;

import lombok.Data;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易明细
 */
@Data
public class TransactionDetailsBo {
    //子交易流水号
    private String rdSeq;
    //业务应付号码
    private String paymentNumber;
    //应付日期
    private Date payDate;
    //业务机构
    private String manageCom;
    //业务类型
    private String businessType;
    //支付方式
    private String payMode;
    //业务号码
    private String businessNumber;
    //投保人名称
    private String appntName;
    //银行名称
    private String payerName;
    //银行
    private String payerBank;
    //交易方账户
    private String payerBankAccount;
    //开户行名称
    private String payerBankLocation;
    //币种
    private String currency;
    //金额
    private BigDecimal amount;
    //加急标志
    private String fastFlag;
    //公私标志
    private String privateFlag;
    //卡折类型
    private String cardType;



}
