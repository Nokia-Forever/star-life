package com.zfh.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 客户端定时任务
 */
@Component
public class ClientTask {

    /**
     * 定时任务，每5分钟更新redis中的店铺状态
     */
    @Scheduled(cron = "0 0/5 * * * ? ")
   public void updateRedisShopStatus (){

    }
}
