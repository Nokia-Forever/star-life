package com.zfh.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.entity.BusinessHours;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zfh.constant.ShopConstant.SHOP_STATUS_AUTO_CLOSE;
import static com.zfh.constant.ShopConstant.SHOP_STATUS_AUTO_OPEN;

/**
 * redis工具类
 */

public class RedisUtils {
    public static void shopStatusHandle(List<BusinessHours> businessHoursList, ObjectMapper objectMapper, StringRedisTemplate stringRedisTemplate){
        Map<String, String> resmap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        businessHoursList.forEach(businessHours -> {
            int value = -1;
            //解析商铺营业时间
            try {
                Map<String, String> map = objectMapper.readValue(businessHours.getBusinessHours(), Map.class);
                //为空,则关闭
                if (map.isEmpty()) {
                    value=SHOP_STATUS_AUTO_CLOSE;
                }
                //找不到对应的星期
                else if (!map.containsKey(now.getDayOfWeek().toString())) {
                    value = SHOP_STATUS_AUTO_CLOSE;
                }
                //不在营业时间
                else if (!TimeUtils.isInTimeRange(now.toLocalTime(), map.get(now.getDayOfWeek().toString()))) {
                    value = SHOP_STATUS_AUTO_CLOSE;
                }
                else {
                    value = SHOP_STATUS_AUTO_OPEN;
                }
                resmap.put(businessHours.getId().toString(), String.valueOf(value));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("解析失败");
            }
        });

        //批量设置
        stringRedisTemplate.opsForHash().putAll(RedisKeyConstant.SHOP_STATUS_KEY, resmap);
    }
}
