package com.zfh.Initializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.entity.BusinessHours;
import com.zfh.entity.Role;
import com.zfh.service.IRoleService;
import com.zfh.service.IShopService;
import com.zfh.service.IShopTypeService;
import com.zfh.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * redis初始化
 */
@Configuration
public class RedisAppInitializer implements ApplicationRunner {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IShopTypeService shopTypeService;
    @Autowired
    private IShopService shopService;

    @Override
    public void run(ApplicationArguments args) {
        //初始化角色key
        initRole();
        //初始化商铺类型key
        initShopType();
        //初始化商铺状态
        initShopStatus();
    }

    //初始化商铺状态
    private void initShopStatus() {
        //数据库查找上线的店铺,保留他们的营业时间
        List<BusinessHours> businessHoursList = shopService.getBusinessHoursList();
        RedisUtils.shopStatusHandle(businessHoursList,objectMapper,stringRedisTemplate);
    }

    //初始化角色key
    private void initRole() {
        //先查询数据库
        List<Role> list = roleService.list();
        //存放角色列表key
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();
            ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
            for (Role role : list) {
                Map<String, String> map = new HashMap<>();
                objectMapper.convertValue(role, Map.class).forEach((k, v) -> {
                    map.put(k.toString(), v == null ? null : v.toString());
                });
                // 使用角色ID作为hash的key，角色名称作为value存储
                hashOps.putAll(RedisKeyConstant.STAFF_ROLE_KEY + role.getId(), map);
                //建立二级索引
                valueOps.set(RedisKeyConstant.STAFF_ROLE_KEY + role.getName(), role.getId().toString());
            }
            return null;
        });
    }

    //初始化商铺类型key
    private void initShopType() {
        shopTypeService.listShopType();
    }
}