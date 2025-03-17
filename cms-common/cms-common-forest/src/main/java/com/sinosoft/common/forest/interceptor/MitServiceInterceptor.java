package com.sinosoft.common.forest.interceptor;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.utils.ForestDataType;
import com.fasterxml.jackson.core.JsonProcessingException;
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
            //解析XML响应
            JsonNode rootNode = xmlMapper.readTree(responseBody);
            JsonNode bodyNode = rootNode.at("/Body/DoServiceResponse/DoServiceReturn");
            if (bodyNode.isMissingNode()) {
                log.warn("未能从响应中找到Package节点");
                return;
            }
            // 获取文本内容
            String xmlContent = bodyNode.asText();
            log.debug("提取的XML内容: {}", xmlContent);
            // 确保XML内容完整并解码
            if (!xmlContent.trim().startsWith("<?xml")) {
                xmlContent = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n" + xmlContent;
            }
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

    public static void main(String[] args) {
        String responseBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Body><DoServiceResponse xmlns=\"http://ec.lis.sinosoft.com\"><DoServiceReturn>&lt;?xml version=&quot;1.0&quot; encoding=&quot;GBK&quot;?&gt;&lt;Package&gt;\n" +
            "        &lt;ClientInfo&gt;\n" +
            "                &lt;DealType&gt;&#x57F9;&#x8BAD;&#x4FE1;&#x606F;&#x63A5;&#x53E3;&lt;/DealType&gt;\n" +
            "                &lt;BusinessCode&gt;1110150&lt;/BusinessCode&gt;\n" +
            "                &lt;SubTransCode&gt;\n" +
            "        &lt;/SubTransCode&gt;\n" +
            "                &lt;Date&gt;\n" +
            "        &lt;/Date&gt;\n" +
            "                &lt;Time&gt;00:00:00&lt;/Time&gt;\n" +
            "                &lt;SeqNo&gt;00000&lt;/SeqNo&gt;\n" +
            "                &lt;Operator&gt;001&lt;/Operator&gt;\n" +
            "                &lt;RowNumStart&gt;\n" +
            "        &lt;/RowNumStart&gt;\n" +
            "                &lt;PageRowNum&gt;\n" +
            "        &lt;/PageRowNum&gt;\n" +
            "                &lt;ResultCode&gt;200&lt;/ResultCode&gt;\n" +
            "        &lt;/ClientInfo&gt;\n" +
            "        &lt;Response&gt;\n" +
            "                &lt;trainiseligible&gt;&#x5408;&#x683C;&lt;/trainiseligible&gt;\n" +
            "                &lt;baseGrade&gt;80&lt;/baseGrade&gt;\n" +
            "                &lt;trainEndGrade&gt;80&lt;/trainEndGrade&gt;\n" +
            "                &lt;newProductGrade&gt;80&lt;/newProductGrade&gt;\n" +
            "                &lt;eligibleGrade&gt;80&lt;/eligibleGrade&gt;\n" +
            "        &lt;/Response&gt;\n" +
            "&lt;/Package&gt;\n" +
            "</DoServiceReturn></DoServiceResponse></soapenv:Body></soapenv:Envelope>";

        try {
            // 第一步：解析 SOAP XML
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode rootNode = xmlMapper.readTree(responseBody);

            // 获取 DoServiceReturn 里的内容（注意：此内容是 XML 的字符串）
            JsonNode bodyNode = rootNode.at("/Body/DoServiceResponse/DoServiceReturn");
            String escapedXmlContent = bodyNode.asText();
            System.out.println("escapedXmlContent = " + escapedXmlContent);
            // 第二步：解析 DoServiceReturn 里的 XML
            JsonNode innerXmlNode = xmlMapper.readTree(escapedXmlContent);

            // 打印解析后的 XML 结构
            System.out.println("Parsed XML Content:");
            System.out.println(innerXmlNode.toPrettyString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
