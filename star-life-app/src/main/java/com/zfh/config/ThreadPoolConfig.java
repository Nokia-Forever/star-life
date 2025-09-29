package com.zfh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 */
@EnableAsync
@Configuration
public class ThreadPoolConfig {
    @Bean("threadPoolExecutor")
    public ThreadPoolExecutor threadPoolExecutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                5,//核心线程数，一直都能工作的数量
                10,//请求处理大的时候，可以开放的最大工作数、最大线程数
                6,//开启最大工作数后，当无请求时，还让其存活的时间、线程活跃时间
               TimeUnit.SECONDS,//存活时间的单位
                new LinkedBlockingDeque<>(100),//阻塞队列，保存操作请求线程
                Executors.defaultThreadFactory(),//创建线程的工厂类
                new ThreadPoolExecutor.AbortPolicy()//设置拒绝策略
        );
        return threadPoolExecutor;
    }
}
