package com.sinosoft.bank;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 银保渠道
 *
 * @author zzf
 */
@EnableDubbo
@SpringBootApplication
public class BankApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(BankApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  银保渠道启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
