package com.sinosoft.common.encrypt.filter;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sinosoft.common.core.domain.GlobalResponse;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.encrypt.utils.CryptoUtils;
import com.sinosoft.system.api.domain.vo.RemoteClientVo;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 统一接入响应参数包装类
 *
 * @author zzf
 */
public class GlobalApiResponseBodyWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream buffer;
    private final ServletOutputStream output;
    private final PrintWriter writer;

    public GlobalApiResponseBodyWrapper(HttpServletResponse response) throws IOException {
        super(response);
        this.buffer = new ByteArrayOutputStream();
        this.output = createOutputStream();
        this.writer = new PrintWriter(new OutputStreamWriter(buffer, StandardCharsets.UTF_8), true);
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return output;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (writer != null) {
            writer.flush();
        }
        if (output != null) {
            output.flush();
        }
    }

    @Override
    public void reset() {
        buffer.reset();
    }

    /**
     * 获取响应数据字节数组
     */
    public byte[] getResponseData() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }

    /**
     * 获取响应内容字符串
     */
    public String getContent() throws IOException {
        flushBuffer();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    /**
     * 获取处理后的响应体
     *
     * @param servletResponse response
     * @param messageId       消息ID
     * @param safeMode        安全模式
     * @param encryptKey      加密密钥
     * @param clientVo        客户端对象
     * @return 响应体
     * @throws IOException IO异常
     */
    public String getResponseContent(HttpServletResponse servletResponse, String messageId,
                                     String safeMode, String encryptKey, RemoteClientVo clientVo) throws IOException {
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        String originalBody = this.getContent();

        // 构建标准响应格式
        JSONObject responseJson = new JSONObject();
        JSONObject header = buildHeader(servletResponse.getStatus(), messageId);

        // 处理响应体数据
        if (isValidJsonContent(originalBody)) {
            processJsonContent(originalBody, responseJson, header);
        } else {
            // 处理空响应或非JSON数据
            responseJson.set("response", new JSONObject());
        }

        // 设置响应头
        responseJson.set("header", header);

        // 处理加密
        return encryptResponseIfNeeded(responseJson.toString(), safeMode, encryptKey, clientVo);
    }

    /**
     * 创建自定义ServletOutputStream
     */
    private ServletOutputStream createOutputStream() {
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                // 不需要实现
            }

            @Override
            public void write(int b) throws IOException {
                buffer.write(b);
            }

            @Override
            public void write(byte[] b) throws IOException {
                buffer.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                buffer.write(b, off, len);
            }
        };
    }

    /**
     * 判断内容是否为有效的JSON格式
     */
    private boolean isValidJsonContent(String content) {
        return StringUtils.isNotEmpty(content) && JSONUtil.isTypeJSON(content);
    }

    /**
     * 处理JSON格式的响应内容
     */
    private void processJsonContent(String content, JSONObject responseJson, JSONObject header) {
        try {
            Object parsedJson = content.trim().startsWith("[") ?
                JSONUtil.parseArray(content) : JSONUtil.parseObj(content);

            // 检查是否为GlobalResponse格式
            if (isGlobalResponseFormat(parsedJson)) {
                processGlobalResponse(content, responseJson, header);
            } else {
                // 直接使用解析后的数据作为response
                responseJson.set("response", parsedJson);
            }
        } catch (Exception e) {
            // 解析异常时设置空对象
            responseJson.set("response", new JSONObject());
        }
    }

    /**
     * 判断是否为GlobalResponse格式
     */
    private boolean isGlobalResponseFormat(Object parsedJson) {
        return parsedJson instanceof JSONObject obj &&
            obj.containsKey("code") &&
            obj.containsKey("data");
    }

    /**
     * 处理GlobalResponse格式的响应
     */
    private void processGlobalResponse(String content, JSONObject responseJson, JSONObject header) {
        GlobalResponse<?> globalResponse = JSONUtil.toBean(content, GlobalResponse.class);

        // 更新响应头信息
        header.set("code", globalResponse.getCode());

        // 处理错误响应
        if (globalResponse.getCode() == GlobalResponse.FAIL) {
            header.set("error_code", globalResponse.getErrorCode());
            header.set("message", globalResponse.getMessage());
            header.set("detail_message", globalResponse.getDetailMessage());
        }

        // 设置响应数据
        Object responseData = globalResponse.getData();
        if (responseData != null) {
            responseJson.set("response", convertToJsonObject(responseData));
        } else {
            responseJson.set("response", new JSONObject());
        }
    }

    /**
     * 将对象转换为JSONObject或JSONArray
     */
    private Object convertToJsonObject(Object data) {
        if (data instanceof JSONObject || data instanceof JSONArray) {
            return data;
        }
        return JSONUtil.parseObj(data);
    }

    /**
     * 创建基础响应头
     */
    private JSONObject buildHeader(int statusCode, String messageId) {
        JSONObject header = new JSONObject();
        header.set("code", statusCode);
        header.set("messageid", messageId);
        return header;
    }

    /**
     * 根据需要加密响应内容
     */
    private String encryptResponseIfNeeded(String content, String safeMode, String encryptKey, RemoteClientVo clientVo) {
        if ("1".equals(safeMode) && clientVo != null && clientVo.getClientConfig() != null) {
            try {
                String sm4Key = CryptoUtils.decryptSm4Key(encryptKey, clientVo.getClientConfig().getSm2PrivateKey());
                return CryptoUtils.encryptBody(content, sm4Key);
            } catch (Exception e) {
                // 加密失败时返回原始内容
                return content;
            }
        }
        return content;
    }
}
