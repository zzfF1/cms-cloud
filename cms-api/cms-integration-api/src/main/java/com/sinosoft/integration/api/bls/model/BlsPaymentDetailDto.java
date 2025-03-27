package com.sinosoft.integration.api.bls.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 结算-代付明细Dto
 *
 * @author: zzf
 * @create: 2025-03-13 17:37
 */
@Data
public class BlsPaymentDetailDto {
    //子交易流水号
    String rdSeq;
    //业务应付号码
    String paymentNumber;
    //应付日期
    Date payDate;
    //管理机构
    String manageCom;
    //业务类型
    String businessType;
    //支付方式
    String payMode;
    //业务号码
    String businessNumber;
    //投保人名称
    String appntName;
    //银行名称
    String payerName;
    //银行
    String payerBank;
    //银行账号
    String payerBankAccount;
    //开户行名称
    String payerBankLocation;
    //币种
    String currency;
    //金额
    BigDecimal amount;
    //加急标志
    String fastFlag;
    //私有标志
    String privateFlag;
    //卡折类型
    String cardType;
}
