package com.zfh.mapper;

import com.zfh.entity.Shop;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zfh.vo.ShopVo;

/**
 * <p>
 * 商铺基础信息表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2025-09-22
 */
public interface ShopMapper extends BaseMapper<Shop> {

    /**
     * 获取商铺信息
     * @param id
     * @return
     */
    ShopVo getInfoById(Long id);
}
