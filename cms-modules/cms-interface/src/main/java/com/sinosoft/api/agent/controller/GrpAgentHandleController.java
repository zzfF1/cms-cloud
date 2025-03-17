package com.sinosoft.api.agent.controller;

import com.sinosoft.api.agent.domain.TestDemo;
import com.sinosoft.api.agent.domain.bo.CheckContractChangeBo;
import com.sinosoft.api.agent.domain.bo.CheckOrphanSingleBo;
import com.sinosoft.api.agent.domain.vo.SaleInfoVo;
import com.sinosoft.api.client.JkphClient;
import com.sinosoft.api.client.TestClient;
import com.sinosoft.common.core.domain.GlobalResponse;
import com.sinosoft.common.mq.core.MqMessage;
import com.sinosoft.common.mq.core.MqProducer;
import com.sinosoft.common.mq.core.MqResult;
import com.sinosoft.common.mq.core.MqType;
import com.sinosoft.common.mq.factory.MqFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * 测试接口
 *
 * @author: zzf
 * @create: 2025-02-06 17:27
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/grpagent")
public class GrpAgentHandleController {

    private final TestClient testClient;
    private final JkphClient jkphClient;
    private final MqFactory mqFactory;

    @PostMapping("/list")
    public GlobalResponse<Map> test(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> valMap = new HashMap<>();
        List<TestDemo> list = new ArrayList<>();
        TestDemo testDemo = new TestDemo();
        testDemo.setCode("A");
        testDemo.setName("A");
        TestDemo testDemo1 = new TestDemo();
        testDemo1.setCode("B");
        testDemo1.setName("B");
        list.add(testDemo);
        list.add(testDemo1);
        valMap.put("data", list);
        return GlobalResponse.ok(valMap);
    }

    @PostMapping("/login")
    public String testLogin() {
//        String msg = testClient.testLogin("000000", "admin", "admin123", "e5cd7e4891bf95d1d19206ce24a7b32e", "password");
//        String msg = testClient.testList("admin", "e5cd7e4891bf95d1d19206ce24a7b32e");
//        String msg = testClient.testManagecom("86");
//        String msg = testClient.testSaleManagecom("86");

//        List<CheckContractChangeBo> queryBo = new ArrayList<>();
//        CheckContractChangeBo checkContractChangeBo = new CheckContractChangeBo();
//        checkContractChangeBo.setContNo("1");
//        checkContractChangeBo.setCode("2");
//
//        queryBo.add(checkContractChangeBo);
        //GlobalResponse<List<SaleInfoVo>> response = jkphClient.checkContractChange(queryBo);
        //System.out.println("信息："+response.toString());
//        System.out.println("msg = " + msg);

        kafkaTest();
        rabbitTest();
        return "123456";
    }

    public void kafkaTest() {
        // 获取Kafka生产者
        MqProducer producer = mqFactory.getProducer(MqType.KAFKA, "local");

        // 发送测试消息
        log.info("开始发送Kafka测试消息...");

        for (int i = 1; i <= 5; i++) {
            // 创建消息
            String messageContent = "测试消息 #" + i + ", 时间: " + new Date();
            MqMessage message = new MqMessage("test-topic", messageContent);

            // 发送消息
            MqResult<String> result = producer.send(message);
            if (result.isSuccess()) {
                log.info("消息发送成功: {}, messageId: {}", messageContent, result.getData());
            } else {
                log.error("消息发送失败: {}, 错误: {}", messageContent, result.getMessage());
            }
            // 等待1秒
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("Kafka测试消息发送完成");
    }

    public void rabbitTest() {
        // 获取RabbitMQ生产者
        MqProducer producer = mqFactory.getProducer(MqType.RABBIT_MQ, "local");

        // 发送测试消息
        log.info("开始发送RabbitMQ测试消息...");

        for (int i = 1; i <= 5; i++) {
            // 创建消息
            String messageContent = "RabbitMQ测试消息 #" + i + ", 时间: " + new Date();
            MqMessage message = new MqMessage("test-rabbit-topic", "test", messageContent);

            // 发送消息
            MqResult<String> result = producer.send(message);
            if (result.isSuccess()) {
                log.info("RabbitMQ消息发送成功: {}, messageId: {}", messageContent, result.getData());
            } else {
                log.error("RabbitMQ消息发送失败: {}, 错误: {}", messageContent, result.getMessage());
            }

            // 等待1秒
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        log.info("RabbitMQ测试消息发送完成");
    }


    @PostMapping("/logina")
    public String testLogin1() {
//        String msg = testClient.testLogin("000000", "admin", "admin123", "e5cd7e4891bf95d1d19206ce24a7b32e", "password");
//        String msg = testClient.testList("admin", "e5cd7e4891bf95d1d19206ce24a7b32e");
//        String msg = testClient.testManagecom("86");
//        String msg = testClient.testSaleManagecom("86");

        List<CheckOrphanSingleBo> queryBo = new ArrayList<>();
        CheckOrphanSingleBo checkContractChangeBo = new CheckOrphanSingleBo();
        checkContractChangeBo.setConNo("1");
        checkContractChangeBo.setName("2");
        checkContractChangeBo.setCode("2");
        checkContractChangeBo.setChangeName("2");
        checkContractChangeBo.setChangeCode("2");
        checkContractChangeBo.setChangeDate("2");

        queryBo.add(checkContractChangeBo);
        GlobalResponse<List<SaleInfoVo>> response = jkphClient.checkContractChange1(queryBo);
        System.out.println(response.toString());
        System.out.println("msg孤儿单 = " + response.toString());
        return "34444";
    }
}
