package com.zfh.service;

import com.zfh.dto.SeckillVoucherDto;
import com.zfh.dto.VoucherDto;
import com.zfh.dto.VoucherStatusDto;
import com.zfh.entity.Voucher;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zfh.vo.VoucherVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2025-09-29
 */
public interface IVoucherService extends IService<Voucher> {

    /**
     * 新增普通优惠券
     * @param voucherDto
     * @return
     */
    Boolean add(VoucherDto voucherDto);

    /**
     * 新增秒杀优惠券
     * @param seckillVoucherDto
     * @return
     */
    Boolean addSeckill(SeckillVoucherDto seckillVoucherDto);

    /**
     *
     * @param shopId
     * @return
     */
    List<VoucherVo> listByShopId(Long shopId);

    /**
     * 优惠券状态改变
     * @param voucherStatusDto
     * @return
     */
    Boolean changeStatus(VoucherStatusDto voucherStatusDto);

    /**
     * 查询上架的优惠券
     * @param shopId
     * @return
     */
    List<VoucherVo> listOnlineByShopId(Long shopId);

    /**
     * 查询单个优惠券信息
     * @param id
     * @return
     */
    VoucherVo getInfoById(Long id);
}
