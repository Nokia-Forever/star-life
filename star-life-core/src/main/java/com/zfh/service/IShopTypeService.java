package com.zfh.service;

import com.zfh.entity.ShopType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商铺类型表 服务类
 * </p>
 *
 * @author author
 * @since 2025-09-21
 */
public interface IShopTypeService extends IService<ShopType> {

    /**
     * 获取商铺类型列表
     * @return
     */
    List<ShopType> listShopType();
}
