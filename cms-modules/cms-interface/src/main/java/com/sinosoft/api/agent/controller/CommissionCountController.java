package com.sinosoft.api.agent.controller;

import com.sinosoft.api.agent.domain.bo.BartchPaymentAgentBo;
import com.sinosoft.api.agent.domain.bo.CheckOrphanSingleBo;
import com.sinosoft.api.agent.domain.bo.TransactionDetailsBo;
import com.sinosoft.api.agent.domain.vo.BartchPaymentAgentVo;
import com.sinosoft.api.agent.domain.vo.SaleInfoVo;
import com.sinosoft.api.client.SettlementServiceClient;
import com.sinosoft.common.core.domain.GlobalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hpsf.Decimal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 佣金计算
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/commission")
public class CommissionCountController {

    private final SettlementServiceClient settlementServiceClient;

    @PostMapping("/commissionlist")
    public String commissionCount(){

        List<BartchPaymentAgentBo> querye = new ArrayList<>();
        BartchPaymentAgentBo bartchPaymentAgentBo = new BartchPaymentAgentBo();
        //交易笔数
        bartchPaymentAgentBo.setTotalCount(1);
        //系统来源
        bartchPaymentAgentBo.setFromSystem("2");
        //交易时间
        bartchPaymentAgentBo.setTransTime(new Date());
        BigDecimal bigDecimal = new BigDecimal("10.1");
        //交易总金额
        bartchPaymentAgentBo.setTotalAmount(bigDecimal);
        List<TransactionDetailsBo> queryBo = new ArrayList<>();
        TransactionDetailsBo transactionDetailsBo = new TransactionDetailsBo();
        //子交易流水号
        transactionDetailsBo.setRdSeq("111");
        //业务应付号码
        transactionDetailsBo.setPaymentNumber("10");
        //应付日期
        transactionDetailsBo.setPayDate(new Date());
        //业务机构
        transactionDetailsBo.setManageCom("86110");
        //业务类型
        transactionDetailsBo.setBusinessType("5");
        //支付方式
        transactionDetailsBo.setPayMode("12");
        //业务号码
        transactionDetailsBo.setBusinessNumber("10");
        //投保人名称
        transactionDetailsBo.setAppntName("4");
        //银行名称
        transactionDetailsBo.setPayerName("12");
        //银行
        transactionDetailsBo.setPayerBank("1232");
        //交易方账户
        transactionDetailsBo.setPayerBankAccount("1");
        //开户行名称
        transactionDetailsBo.setPayerBankLocation("12");
        //币种
        transactionDetailsBo.setCurrency("3");
        //金额
        transactionDetailsBo.setAmount(bigDecimal);
        //加急标志
        transactionDetailsBo.setFastFlag("1");
        //公私标志
        transactionDetailsBo.setPrivateFlag("1");
        //卡折类型
        transactionDetailsBo.setCardType("01");
        TransactionDetailsBo transactionDetailsBo1 = new TransactionDetailsBo();
        //子交易流水号
        transactionDetailsBo1.setRdSeq("111");
        //业务应付号码
        transactionDetailsBo1.setPaymentNumber("10");
        //应付日期
        transactionDetailsBo1.setPayDate(new Date());
        //业务机构
        transactionDetailsBo1.setManageCom("86110");
        //业务类型
        transactionDetailsBo.setBusinessType("5");
        //支付方式
        transactionDetailsBo1.setPayMode("12");
        //业务号码
        transactionDetailsBo1.setBusinessNumber("10");
        //投保人名称
        transactionDetailsBo1.setAppntName("4");
        //银行名称
        transactionDetailsBo1.setPayerName("12");
        //银行
        transactionDetailsBo1.setPayerBank("1232");
        //交易方账户
        transactionDetailsBo1.setPayerBankAccount("1");
        //开户行名称
        transactionDetailsBo1.setPayerBankLocation("12");
        //币种
        transactionDetailsBo1.setCurrency("3");
        //金额
        transactionDetailsBo1.setAmount(bigDecimal);
        //加急标志
        transactionDetailsBo1.setFastFlag("1");
        //公私标志
        transactionDetailsBo1.setPrivateFlag("1");
        //卡折类型
        transactionDetailsBo1.setCardType("01");
        queryBo.add(transactionDetailsBo);
        queryBo.add(transactionDetailsBo1);
        bartchPaymentAgentBo.setPaymentDetailList(queryBo);
        querye.add(bartchPaymentAgentBo);
        System.out.println(bartchPaymentAgentBo);
        GlobalResponse<BartchPaymentAgentVo> bartchPaymentAgentVoGlobalResponse = settlementServiceClient.BartchPaymentAgent(bartchPaymentAgentBo);
        System.out.println(bartchPaymentAgentVoGlobalResponse.toString());
        return "成功";
    }
}
