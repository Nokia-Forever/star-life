package com.zfh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zfh.dto.ShopDto;
import com.zfh.entity.BusinessHours;
import com.zfh.entity.Shop;
import com.zfh.vo.ShopVo;

import java.util.List;
import java.util.Map;

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

    /**
     * 获取上线店铺营业时间列表
     * @return
     */
    List<BusinessHours> getBusinessHoursList();

    /**
     * 手动开关商铺
     * @param id
     * @param status
     * @return
     */
    int manualOpenOrCloseShop(Long id, Boolean status);


    /**
     * 清除手动处理营业状态
     * @param id
     * @return
     */
    int deleteShopStatusManual(Long id);

    /**
     * 获取商铺营业状态
     * @param id
     * @return
     */
    Boolean getShopStatus(Long id);

    /**
     * 批量获取商铺信息
     * @param ids
     * @return
     */
    Map<Long, ShopVo> getInfoByIds(List<Long> ids);
}
