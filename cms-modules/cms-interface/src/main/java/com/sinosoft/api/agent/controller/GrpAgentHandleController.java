package com.sinosoft.api.agent.controller;


import cn.hutool.json.JSONUtil;
import com.dtflys.forest.http.ForestResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.sinosoft.api.agent.domain.CmsResult;
import com.sinosoft.api.agent.domain.TestDemo;
import com.sinosoft.api.agent.domain.TrainingInfoRequest;
import com.sinosoft.api.agent.domain.TrainingInfoResponse;
import com.sinosoft.api.client.JkphClient;
import com.sinosoft.api.client.TestClient;
import com.sinosoft.common.core.domain.GlobalResponse;
import com.sinosoft.api.client.MitServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final MitServiceClient mitServiceClient;

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
//
//        List<CheckContractChangeBo> queryBo = new ArrayList<>();
//        CheckContractChangeBo checkContractChangeBo = new CheckContractChangeBo();
//        checkContractChangeBo.setContNo("1");
//        checkContractChangeBo.setCode("2");
//
//        queryBo.add(checkContractChangeBo);
//        GlobalResponse<List<SaleInfoVo>> response = jkphClient.checkContractChange(queryBo);
//        System.out.println(response.toString());

        try {

            XmlMapper xmlMapper = new XmlMapper();

            TrainingInfoRequest request = new TrainingInfoRequest();
            // 设置ClientInfo
            TrainingInfoRequest.ClientInfo clientInfo = new TrainingInfoRequest.ClientInfo();
            clientInfo.setDealType("培训信息接口");
            clientInfo.setBusinessCode("1110150");
            clientInfo.setDate("2025-03-05");
            clientInfo.setTime("00:00:00");
            clientInfo.setSeqNo("00000");
            clientInfo.setOperator("001");
            request.setClientInfo(clientInfo);

            TrainingInfoRequest.Request reqData = new TrainingInfoRequest.Request();
            reqData.setName("*晓娟");
            reqData.setIdnoType("0");
            reqData.setIdno("140603199103072124");
            reqData.setManageCom("861400");
            request.setRequest(reqData);

            String cXml = xmlMapper.writeValueAsString(request);
            cXml = "<?xml version=\"1.0\" encoding=\"GBK\"?>" + cXml;
            String response = mitServiceClient.doService(cXml);

            TrainingInfoResponse response1 = xmlMapper.readValue(response, TrainingInfoResponse.class);
            System.out.println("response.getContent() = " + JSONUtil.toJsonStr(response1));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

//        System.out.println("msg = " + msg);
        return "123456";
    }
}
