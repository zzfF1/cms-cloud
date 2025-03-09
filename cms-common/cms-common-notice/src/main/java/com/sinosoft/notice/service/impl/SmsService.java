package com.sinosoft.notice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 短信服务
 */
@Slf4j
@Service
public class SmsService {

    /**
     * 发送短信
     *
     * @param mobile  手机号
     * @param content 内容
     * @return 是否发送成功
     */
    public boolean send(String mobile, String content) {
        // 实际项目中，这里应该调用SMS服务商提供的API
        // 例如阿里云、腾讯云等短信服务
        // 示例代码，模拟发送过程
        log.info("发送短信到: {}, 内容: {}", mobile, content);
        try {
            // 模拟发送耗时
            Thread.sleep(200);
            // 模拟发送成功
            return true;
        } catch (Exception e) {
            log.error("短信发送异常", e);
            return false;
        }
    }
}
