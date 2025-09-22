package com.zfh.service;

import com.zfh.dto.ShopDto;
import com.zfh.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zfh.vo.ShopVo;

/**
 * <p>
 * 商铺基础信息表 服务类
 * </p>
 *
 * @author author
 * @since 2025-09-22
 */
public interface IShopService extends IService<Shop> {

    /**
     * 申请商铺
     * @param shopDto
     * @return
     */
    int applyShop(ShopDto shopDto);

    /**
     * 获取商铺信息
     * @param id
     * @return
     */
    ShopVo getInfoById(Long id);
}
