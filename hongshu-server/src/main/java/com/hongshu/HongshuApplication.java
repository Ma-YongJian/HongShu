package com.hongshu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @Author hongshu
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class HongshuApplication {
    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(HongshuApplication.class, args);
    }
}
