package com.yoobee.licenseplate;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * spring boot 启动类
 *
 * @author jackiechan
 */
@SpringBootApplication
@MapperScan("com.yoobee.licenseplate.mapper")
@EnableScheduling //开启对定时任务的支持
@Slf4j
public class LicensePlateApplication {

    public static void main(String[] args) {
        SpringApplication.run(LicensePlateApplication.class, args);
    }
}
