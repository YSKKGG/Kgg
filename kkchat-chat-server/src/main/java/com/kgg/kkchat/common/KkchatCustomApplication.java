package com.kgg.kkchat.common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author kgg
 * @date 2023/12/19
 */
@SpringBootApplication(scanBasePackages = {"com.kgg.kkchat"})
@MapperScan({"com.kgg.kkchat.common.**.mapper"})
public class KkchatCustomApplication {

    public static void main(String[] args) {
        SpringApplication.run(KkchatCustomApplication.class,args);
    }

}