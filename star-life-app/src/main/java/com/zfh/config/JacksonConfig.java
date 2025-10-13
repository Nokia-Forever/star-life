package com.zfh.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * Jackson 配置
 */
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {

       ObjectMapper objectMapper = new ObjectMapper();
       // 设置时区
       objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")))
               .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }


}
