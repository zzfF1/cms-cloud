package com.sinosoft.api;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 接口服务
 *
 * @author zzf
 */
@EnableDubbo
@SpringBootApplication
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ApiApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  接口服务启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
