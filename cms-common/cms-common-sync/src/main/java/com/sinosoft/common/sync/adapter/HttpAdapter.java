package com.sinosoft.common.sync.adapter;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * HTTP接口调用适配器
 * <p>
 * 用于通过HTTP接口同步数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpAdapter {

//    /**
//     * 发送HTTP请求
//     *
//     * @param url         请求URL
//     * @param method      请求方法
//     * @param requestBody 请求体
//     * @param headers     请求头
//     * @param timeout     超时时间（毫秒）
//     * @return 响应内容
//     */
//    public String sendRequest(String url, HttpMethod method, String requestBody,
//                              Map<String, String> headers, int timeout) {
//        try {
//            log.debug("发送HTTP请求: URL={}, 方法={}, 超时={}ms", url, method, timeout);
//
////            // 创建请求头
////            HttpHeaders httpHeaders = new HttpHeaders();
////            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
////
////            // 添加自定义请求头
////            if (headers != null && !headers.isEmpty()) {
////                headers.forEach(httpHeaders::add);
////            }
////
////            // 创建请求实体
////            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
////
////            // 设置超时（在实际应用中，可能需要创建带超时设置的RestTemplate实例）
////
////            // 记录请求开始时间
////            long startTime = System.currentTimeMillis();
////
////            // 发送请求
////            ResponseEntity<String> response = restTemplate.exchange(
////                url,
////                method,
////                requestEntity,
////                String.class
////            );
////
////            // 计算耗时
////            long elapsedTime = System.currentTimeMillis() - startTime;
////
////            // 检查响应状态
////            if (response.getStatusCode().is2xxSuccessful()) {
////                log.debug("HTTP请求成功: 状态码={}, 耗时={}ms", response.getStatusCode(), elapsedTime);
////                return response.getBody();
////            } else {
////                log.warn("HTTP请求返回非成功状态: {}, 耗时={}ms", response.getStatusCode(), elapsedTime);
////                throw new RuntimeException("HTTP请求失败，状态码: " + response.getStatusCode());
////            }
//        } catch (Exception e) {
//            log.error("发送HTTP请求失败: {}", e.getMessage());
//            throw new RuntimeException("发送HTTP请求失败: " + e.getMessage(), e);
//        }
//    }
}
