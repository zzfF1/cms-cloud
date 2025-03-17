package com.sinosoft.inter.api.bls.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 结算-代付DTO
 *
 * @author: zzf
 * @create: 2025-03-13 17:37
 */
@Data
public class BlsPaymentDto {
    Date transTime;
    //请求流水号
    String transSeq;
    //来源系统
    String fromSystem;
    //交易笔数
    int totalCount;
    //交易总金额
    BigDecimal totalAmount;
    /**
     * 代付明细
     */
    List<BlsPaymentDetailDto> paymentDetailList;
}
