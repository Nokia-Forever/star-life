package com.zfh.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * redis工具类
 */
@Component
public class RedisWorker {
    // 开始时间戳(2020-01-01 00:00:00)
    private static final long BEGIN_TIMESTAMP = 946684800L;
    // 序列号位数
    private static final long COUNT_BITS = 32;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public  Long getNextId(String keyPrefix) {
        //获取当前时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowStamp = now.toEpochSecond(ZoneOffset.UTC);
        //获取时间差
        long timeStamp = nowStamp - BEGIN_TIMESTAMP;
        //获取当前时间格式
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));

        //生成id
        Long id = stringRedisTemplate.opsForValue().increment(keyPrefix+ date);
        //拼接返回
        return timeStamp << COUNT_BITS | (id!=null?id:0);
    }
}
