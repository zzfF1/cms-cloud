package com.sinosoft.api.agent.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * CMS结果实体类
 * 优化XML映射，解决转换问题
 *
 * @author: zzf
 * @create: 2025-03-06 17:30
 */
@Data
@NoArgsConstructor
@ToString
@JacksonXmlRootElement(localName = "CmsResult")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CmsResult {

    /**
     * 批次号
     */
    @JacksonXmlProperty(localName = "BatchNum")
    private String batchNum;

    /**
     * 时间戳
     */
    @JacksonXmlProperty(localName = "Times")
    private String times;

    /**
     * 总数量
     */
    @JacksonXmlProperty(localName = "TotalCount")
    private Integer totalCount;

    /**
     * 结果是否成功 Y/N
     */
    @JacksonXmlProperty(localName = "ResultSuccess")
    private String resultSuccess;

    /**
     * 结果消息
     */
    @JacksonXmlProperty(localName = "ResultMsg")
    private String resultMsg;

    /**
     * 销售记录集合
     */
    @JacksonXmlElementWrapper(localName = "Sales")
    @JacksonXmlProperty(localName = "RD")
    private List<SaleRecord> sales;

    /**
     * 销售记录
     */
    @Data
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SaleRecord {

        /**
         * 代理机构代码
         */
        @JacksonXmlProperty(localName = "AgentCom")
        private String agentCom;

        /**
         * 消息
         */
        @JacksonXmlProperty(localName = "MSG")
        private String msg;

        /**
         * 是否成功 Y/N
         */
        @JacksonXmlProperty(localName = "SUCCESS")
        private String success;

        /**
         * 销售代码
         */
        @JacksonXmlProperty(localName = "SaleCode")
        private String saleCode;

        /**
         * 销售名称
         */
        @JacksonXmlProperty(localName = "SaleName")
        private String saleName;
    }
}
