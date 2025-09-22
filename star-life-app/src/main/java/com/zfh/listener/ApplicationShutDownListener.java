package com.zfh.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

/**
 * 监听应用关闭事件
 */

//TODO 为了方便测试,这里不进行删除操作
//@Component
public class ApplicationShutDownListener implements ApplicationListener<ContextClosedEvent> {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        //关闭后删除该项目所有的key
        Set<String> keys = stringRedisTemplate.keys("starLife:*");
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }
}
