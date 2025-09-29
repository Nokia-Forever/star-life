package com.zfh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
@EnableScheduling
@EnableAspectJAutoProxy(exposeProxy=true)
@MapperScan("com.zfh.mapper")
public class StarLifeAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarLifeAppApplication.class, args);
    }

}
