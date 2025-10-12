package com.zfh.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.entity.BusinessHours;
import com.zfh.service.IShopService;
import com.zfh.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zfh.constant.ShopConstant.SHOP_STATUS_MANUAL_CLOSE;
import static com.zfh.constant.ShopConstant.SHOP_STATUS_MANUAL_OPEN;

/**
 * 客户端定时任务
 */
@Component
@Slf4j
public class ClientTask {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IShopService shopService;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 定时任务，每5分钟更新redis中的店铺状态
     */
    @Scheduled(cron = "0 0/5 * * * ? ")
   public void updateRedisShopStatus (){
        //获取当前店铺营业状态
        Map<String, String> resMap= new HashMap<>();
        stringRedisTemplate.opsForHash().entries(RedisKeyConstant.SHOP_STATUS_KEY).forEach(
                (key, value) -> {
                    resMap.put(key.toString(), value.toString());
                }
        );
        //数据库查找上线的店铺,保留他们的营业时间
        List<BusinessHours> businessHoursList = shopService.getBusinessHoursList().stream().filter(
                businessHours -> {
                  String value = resMap.get(businessHours.getId().toString());
                  return value != null && !value.equals(String.valueOf(SHOP_STATUS_MANUAL_CLOSE)) &&!value.equals(String.valueOf(SHOP_STATUS_MANUAL_OPEN)) ;
                }
        ).toList();
       log.info("更新营业状态:{}", businessHoursList);
        //redis处理营业状态
        RedisUtils.shopStatusHandle(businessHoursList, objectMapper, stringRedisTemplate);
    }

}
