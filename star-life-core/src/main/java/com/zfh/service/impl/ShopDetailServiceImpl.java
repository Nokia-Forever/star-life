package com.zfh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.entity.ShopDetail;
import com.zfh.mapper.ShopDetailMapper;
import com.zfh.service.IShopDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 商铺详情表 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-09-22
 */
@Service
public class ShopDetailServiceImpl extends ServiceImpl<ShopDetailMapper, ShopDetail> implements IShopDetailService {
    @Autowired
    private ObjectMapper objectMapper;

    private final ShopDetailMapper shopDetailMapper;

    public ShopDetailServiceImpl(ShopDetailMapper shopDetailMapper) {
        this.shopDetailMapper = shopDetailMapper;
    }

    /**
     * 根据id查询营业时间
     * @param id
     * @return
     */
    @Override
    public Map<String, String> selectBusinessHoursById(Long id) {


        ShopDetail shopDetail = shopDetailMapper.selectOne(
                new LambdaQueryWrapper<ShopDetail>().select(ShopDetail::getBusinessHours)
                        .eq(ShopDetail::getShopId, id));
        Map<String,String> map=new HashMap<>();
        //解析json
        try {
            objectMapper.readValue(shopDetail.getBusinessHours(), Map.class).forEach((k,v)->{
                map.put(k.toString(),v==null?null:v.toString());
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("解析失败");
        }
        return map;
    }
}
