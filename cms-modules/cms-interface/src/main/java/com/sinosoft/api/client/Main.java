package com.sinosoft.api.client;

import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestResponse;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @program: cms-cloud
 * @description:
 * @author: zzf
 * @create: 2025-02-17 00:06
 */
public class Main {
    public static void main(String[] args) {
////        // 创建 Forest 客户端实例
        CmsServiceClient client = Forest.client(CmsServiceClient.class);

////
////        // 构造请求参数
////        // 构造 SOAP 请求体
//        String cXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Request><TotalCount>3</TotalCount><BatchNum>B202503010001</BatchNum><Sales><RD><SaleCode>S001</SaleCode><SaleName>张三</SaleName><AgentCom>3201</AgentCom></RD><RD><SaleCode>S002</SaleCode><SaleName>李四</SaleName><AgentCom>3202</AgentCom></RD><RD><SaleCode>S003</SaleCode><SaleName>王五</SaleName><AgentCom>3203</AgentCom></RD></Sales></Request>"; // 替换为实际的 XML 数据
//        String soapRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://xmltransfer.webinterface.lis.sinosoft.com\">\n" +
//            "  <soapenv:Header/>\n" +
//            "  <soapenv:Body>\n" +
//            "    <ns:queryBankSaleCode>\n" +
//            "      <ns:cXml>" + StringEscapeUtils.escapeXml(cXml) + "</ns:cXml>\n" +
//            "    </ns:queryBankSaleCode>\n" +
//            "  </soapenv:Body>\n" +
//            "</soapenv:Envelope>";
//        ForestResponse response = client.queryBankSaleCode(soapRequest);
//        System.out.println("response.isSuccess() = " + response.isSuccess());
//        System.out.println("response.getContent() = " + response.getContent());



//        String cXml="<Package><ClientInfo><DealType>AAAAAAAA</DealType><BusinessCode>1111111</BusinessCode><date>2022-10-21</date><Time>16:00:09</Time><SeqNo>00000</SeqNo><Operator>huleishi</Operator></ClientInfo><Request><AgentCodeList><AgentCodeInfo><AgentCode>66666666</AgentCode><IDX>P9CWIyk5</IDX></AgentCodeInfo><AgentCodeInfo><AgentCode>88888888</AgentCode><IDX>tBFNa5PH</IDX></AgentCodeInfo><AgentCodeInfo><AgentCode>33333333</AgentCode><IDX>Jo3JOK6d</IDX></AgentCodeInfo></AgentCodeList></Request><Response/></Package>";
//        String soapRequest = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' " +
//            "xmlns:ec='http://ec.lis.sinosoft.com'>" +
//            "  <soapenv:Header/>" +
//            "  <soapenv:Body>" +
//            "    <ec:DoService>" +
//            "      <input>" + StringEscapeUtils.escapeXml(cXml) + "</input>" +
//            "    </ec:DoService>" +
//            "  </soapenv:Body>" +
//            "</soapenv:Envelope>";


        // 发送 SOAP 请求
//        ForestResponse response = client.doService(soapRequest);
//
//        // 处理响应
//        if (response.isSuccess()) {
//            String xmlResponse = response.getContent();
//            // 解析 XML 响应，提取结果
//            String result = parseSoapResponse(xmlResponse);
//            System.out.println("Result: " + result);
//        } else {
//            System.err.println("Request failed: " + response.getStatusCode());
//        }

//        TestClient client = Forest.client(TestClient.class);
//        client.testManagecom("86");
    }

    // 使用 XPath 或 JAXB 解析 SOAP 响应
    private static String parseSoapResponse(String xml) {
        // 示例：简单使用字符串截取（实际建议用 XML 解析库）
        String startTag = "<return>";
        String endTag = "</return>";
        int start = xml.indexOf(startTag) + startTag.length();
        int end = xml.indexOf(endTag);
        return xml.substring(start, end).trim();
    }


    class CmsResult {
        private String batchNum;
        private String times;
        private String totalCount;
        private String resultSuccess;
        private String resultMsg;
        private List<SaleRecord> sales = new ArrayList<>();

        // Getters and setters

        // 内部类表示每条销售记录
        public static class SaleRecord {
            private String agentCom;
            private String msg;
            private String success;
            private String saleCode;

            // Getters and setters
        }
    }
}
