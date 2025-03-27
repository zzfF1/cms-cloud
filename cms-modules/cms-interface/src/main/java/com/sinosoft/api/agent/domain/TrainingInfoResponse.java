package com.sinosoft.api.agent.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;


/**
 * 培训信息接口统一响应类
 */
@Data
@JacksonXmlRootElement(localName = "Package")
public class TrainingInfoResponse {

    @JacksonXmlProperty(localName = "ClientInfo")
    private ClientInfo clientInfo;

    @JacksonXmlProperty(localName = "Response")
    private Response response;

    // 客户端信息（含错误列表）
    @Data
    public static class ClientInfo {
        @JacksonXmlProperty(localName = "DealType")
        private String dealType;

        @JacksonXmlProperty(localName = "BusinessCode")
        private String businessCode;

        @JacksonXmlProperty(localName = "SubTransCode")
        private String subTransCode = "";

        @JacksonXmlProperty(localName = "Date")
        private String date = "";

        @JacksonXmlProperty(localName = "Time")
        private String time;

        @JacksonXmlProperty(localName = "SeqNo")
        private String seqNo;

        @JacksonXmlProperty(localName = "Operator")
        private String operator;

        @JacksonXmlProperty(localName = "RowNumStart")
        private String rowNumStart = "";

        @JacksonXmlProperty(localName = "PageRowNum")
        private String pageRowNum = "";

        @JacksonXmlProperty(localName = "ResultCode")
        private String resultCode = "200";  // 默认成功

        @JacksonXmlProperty(localName = "ErrorList")
        private ErrorList errorList;

        // 错误列表
        @Data
        public static class ErrorList {
            @JacksonXmlElementWrapper(useWrapping = false)
            @JacksonXmlProperty(localName = "Error")
            private List<Error> errors;

            // 错误信息
            @Data
            public static class Error {
                @JacksonXmlProperty(localName = "ErrorID")
                private String errorID;

                @JacksonXmlProperty(localName = "ErrorMessage")
                private String errorMessage;
            }
        }
    }

    // 响应内容（成功时有值，失败时为空）
    @Data
    public static class Response {
        @JacksonXmlProperty(localName = "trainiseligible")
        private String trainiseligible;

        @JacksonXmlProperty(localName = "baseGrade")
        private String baseGrade;

        @JacksonXmlProperty(localName = "trainEndGrade")
        private String trainEndGrade;

        @JacksonXmlProperty(localName = "newProductGrade")
        private String newProductGrade;

        @JacksonXmlProperty(localName = "eligibleGrade")
        private String eligibleGrade;
    }

    /**
     * 判断是否成功响应
     *
     * @return 如果ResultCode为200则返回true，否则返回false
     */
    public boolean isSuccess() {
        return clientInfo != null && "200".equals(clientInfo.getResultCode());
    }
}
