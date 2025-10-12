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
        System.out.println("(♥◠‿◠)ﾉﾞ  红薯启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "   __                           __\n" +
                "  / /_  ____  ____  ____ ______/ /_  __  __\n" +
                " / __ \\/ __ \\/ __ \\/ __ `/ ___/ __ \\/ / / /\n" +
                "/ / / / /_/ / / / / /_/ (__  ) / / / /_/ /\n" +
                "/_/ /_/\\____/_/ /_/\\__, /____/_/ /_/\\__,_/\n" +
                "                 /____/"
        );
    }
}
