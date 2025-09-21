package com.zfh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zfh.entity.ShopType;
import com.zfh.mapper.ShopTypeMapper;
import com.zfh.service.IShopTypeService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商铺类型表 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-09-21
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    /**
     * 获取商铺类型列表
     * @return
     */
    @Cacheable(key = "'list'", value = ":shopType")
    @Override
    public List<ShopType> listShopType() {
        return list(new QueryWrapper<ShopType>().orderByAsc("sort"));
    }
}
