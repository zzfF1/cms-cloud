package com.sinosoft.api.agent.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 * 培训信息接口请求类
 */
@Data
@JacksonXmlRootElement(localName = "Package")
public class TrainingInfoRequest {

    @JacksonXmlProperty(localName = "ClientInfo")
    private ClientInfo clientInfo;

    @JacksonXmlProperty(localName = "Request")
    private Request request;

    // 客户端信息
    @Data
    public static class ClientInfo {
        @JacksonXmlProperty(localName = "DealType")
        private String dealType;

        @JacksonXmlProperty(localName = "BusinessCode")
        private String businessCode;

        @JacksonXmlProperty(localName = "date")
        private String date;

        @JacksonXmlProperty(localName = "Time")
        private String time;

        @JacksonXmlProperty(localName = "SeqNo")
        private String seqNo;

        @JacksonXmlProperty(localName = "Operator")
        private String operator;
    }

    // 请求内容
    @Data
    public static class Request {
        @JacksonXmlProperty(localName = "name")
        private String name;

        @JacksonXmlProperty(localName = "idnoType")
        private String idnoType;

        @JacksonXmlProperty(localName = "idno")
        private String idno;

        @JacksonXmlProperty(localName = "manageCom")
        private String manageCom;
    }
}
