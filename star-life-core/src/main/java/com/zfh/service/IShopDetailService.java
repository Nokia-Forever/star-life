package com.zfh.service;

import com.zfh.entity.ShopDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 商铺详情表 服务类
 * </p>
 *
 * @author author
 * @since 2025-09-22
 */
public interface IShopDetailService extends IService<ShopDetail> {

    /**
     * 查询商铺营业时间
     * @param id
     * @return
     */
    Map<String, String> selectBusinessHoursById(Long id);
}
