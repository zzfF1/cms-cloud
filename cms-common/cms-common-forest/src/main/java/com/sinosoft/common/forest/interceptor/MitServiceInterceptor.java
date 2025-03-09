package com.sinosoft.common.forest.interceptor;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.utils.ForestDataType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;


/**
 * 老核心Service拦截器
 *
 * @author: zzf
 * @create: 2025-03-06 11:42
 */
@Slf4j
@Component
public class MitServiceInterceptor<T> implements Interceptor<T> {

    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    public boolean beforeExecute(ForestRequest request) {
        String xmlVal = request.getBody() != null ? request.body().encodeToString(ForestDataType.TEXT) : "";
        if (StringUtils.isBlank(xmlVal)) {
            throw new ServiceException("请求对象为空!");
        }
        String soapRequest = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' " +
            "xmlns:ec='http://ec.lis.sinosoft.com'>" +
            "  <soapenv:Header/>" +
            "  <soapenv:Body>" +
            "    <ec:DoService>" +
            "      <input>" + StringEscapeUtils.escapeXml(xmlVal) + "</input>" +
            "    </ec:DoService>" +
            "  </soapenv:Body>" +
            "</soapenv:Envelope>";
        request.replaceBody(soapRequest);
        return true;
    }

    @Override
    public void onSuccess(T data, ForestRequest request, ForestResponse response) {
        String responseBody = response.getContent();
        log.debug("原始响应内容: {}", responseBody);
        try {
            // 解析XML响应
            JsonNode rootNode = xmlMapper.readTree(responseBody);
            // 按照SOAP响应的结构导航到ns:return节点
            JsonNode bodyNode = rootNode.path("Body");
            JsonNode responseNode = bodyNode.path("queryBankSaleCodeResponse");
            JsonNode returnNode = responseNode.path("return");
            if (returnNode.isMissingNode()) {
                log.warn("未能从响应中找到return节点");
                return;
            }
            // 获取ns:return的文本内容
            String xmlContent = returnNode.asText();
            log.debug("提取的XML内容: {}", xmlContent);

            // 确保XML内容完整并解码
            if (!xmlContent.trim().startsWith("<?xml")) {
                xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xmlContent;
            }
            xmlContent = StringEscapeUtils.unescapeXml(xmlContent);
            // 验证内容是否为有效XML
            if (StringUtils.isBlank(xmlContent)) {
                log.warn("XML内容为空，无法转换为对象");
                return;
            }
            response.setResult(xmlContent);
        } catch (IOException e) {
            log.error("解析SOAP响应失败", e);
            throw new ServiceException("解析服务响应失败: " + e.getMessage());
        }
    }

}
