package com.zfh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
@EnableScheduling
@MapperScan("com.zfh.mapper")
public class TasteNotesAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(TasteNotesAppApplication.class, args);
    }

}
