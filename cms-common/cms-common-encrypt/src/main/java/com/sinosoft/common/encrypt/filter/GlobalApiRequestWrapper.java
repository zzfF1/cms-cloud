package com.sinosoft.common.encrypt.filter;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sinosoft.common.core.constant.HeaderConstants;
import com.sinosoft.common.core.enums.ErrorCodeEnum;
import com.sinosoft.common.core.exception.GlobalAuthException;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.encrypt.utils.CryptoUtils;
import com.sinosoft.system.api.domain.vo.RemoteClientVo;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 统一接入请求体包装类
 *
 * @author: zzf
 * @create: 2025-02-12 14:26
 */
@Slf4j
public class GlobalApiRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public GlobalApiRequestWrapper(HttpServletRequest request, String safeMode, RemoteClientVo clientVo) throws IOException {
        super(request);
        byte[] readBytes = IoUtil.readBytes(request.getInputStream(), false);
        String requestBody = new String(readBytes, StandardCharsets.UTF_8);
        if ("1".equals(safeMode)) {
            // 获取并解密 SM4 密钥
            String encryptedKey = request.getHeader(HeaderConstants.ENCRYPT_KEY);
            // 解密 SM4 密钥
            String sm4Key = CryptoUtils.decryptSm4Key(encryptedKey, clientVo.getClientConfig().getSm2PrivateKey());
            requestBody = CryptoUtils.decryptBody(requestBody, sm4Key);
        }
        // 解析JSON，提取request字段内容
        if (StringUtils.isNotBlank(requestBody) && JSONUtil.isJsonObj(requestBody)) {
            JSONObject jsonObject = JSONUtil.parseObj(requestBody);
            if (jsonObject.containsKey("request")) {
                // 只获取request字段的内容
                requestBody = JSONUtil.toJsonStr(jsonObject.get("request"));
            }
        } else {
            throw new GlobalAuthException(ErrorCodeEnum.PARAM_FORMAT_INVALID, "参数格式不正确", "请求体不是有效的JSON格式!");
        }
        // 输出
        this.body = requestBody.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }


    @Override
    public int getContentLength() {
        return body.length;
    }

    @Override
    public long getContentLengthLong() {
        return body.length;
    }

    @Override
    public String getContentType() {
        return MediaType.APPLICATION_JSON_VALUE;
    }


    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() {
                return bais.read();
            }

            @Override
            public int available() {
                return body.length;
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }
}
