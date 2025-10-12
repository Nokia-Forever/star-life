package com.zfh.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redisClient(){
        Config config = new Config();

        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")// Redis 服务器地址和端口
                //.setPassword("your_password_here")// 如果有密码
                .setDatabase(0);// 使用的数据库索引
                //.setConnectionPoolSize(10)// 连接池大小
               // .setConnectionMinimumIdleSize(5);// 最小空闲连接数
        // 也可以配置集群、哨兵、主从等模式，根据你的 Redis 部署方式选择// config.useClusterServers().addNodeAddress("redis://node1:6379", "redis://node2:6379");// config.useSentinelServers().addSentinelAddress("redis://sentinel1:26379", "redis://sentinel2:26379");// config.useMasterSlaveServers().setMasterAddress("redis://master:6379").addSlaveAddress("redis://slave1:6379", "redis://slave2:6379");// 根据配置创建 RedissonClient 实例
        return Redisson.create(config);
    }

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        // 创建 Redisson 的缓存管理器
        return new RedissonSpringCacheManager(redissonClient);
    }
}
