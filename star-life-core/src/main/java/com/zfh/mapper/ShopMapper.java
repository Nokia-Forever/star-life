package com.zfh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zfh.entity.BusinessHours;
import com.zfh.entity.Shop;
import com.zfh.vo.ShopVo;

import java.util.List;

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

    /**
     * 获取营业时间列表
     * @return
     *
    **/

    List<BusinessHours> getBusinessHoursList();
}
