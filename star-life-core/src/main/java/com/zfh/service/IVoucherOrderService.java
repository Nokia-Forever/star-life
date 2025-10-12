package com.zfh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zfh.entity.VoucherOrder;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2025-09-29
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {

    /**
     * 秒杀优惠券
     * @param voucherId
     * @return
     */
    Long seckillVoucher(Long voucherId);
}
