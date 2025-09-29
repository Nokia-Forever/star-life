package com.zfh.mapper;

import com.zfh.entity.Voucher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zfh.vo.VoucherVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2025-09-29
 */
public interface VoucherMapper extends BaseMapper<Voucher> {

    /**
     * 查询店铺优惠卷信息
     * @param shopId
     * @return
     */
    List<VoucherVo> listAllByShopId(Long shopId);

    /**
     * 查询店铺上架的优惠卷信息
     * @param shopId
     * @return
     */
    List<VoucherVo> listOnlineByShopId(Long shopId);

    /**
     * 查询优惠卷信息
     * @param id
     * @return
     */
    VoucherVo getInfoById(Long id);
}
