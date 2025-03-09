package com.sinosoft.common.config;

import com.aspose.words.License;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @program: cms6
 * @description: Aspose初始化
 * @author: zzf
 * @create: 2024-01-16 10:48
 */
@Slf4j
@Component
public class AsposeConfig {
    /**
     * 初始化
     * @throws Exception
     */
    @PostConstruct
    public void doInit() throws Exception {
        try {
            log.info("实现`aspose-words`授权 -> 去掉头部水印");
            InputStream is = new ClassPathResource("License.xml").getInputStream();
            License license = new License();
            license.setLicense(is);
        } catch (Exception e) {
            log.error("《`aspose-words`授权》 失败： {}", e.getMessage());
        }
    }
}
