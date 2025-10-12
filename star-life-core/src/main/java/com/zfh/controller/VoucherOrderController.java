package com.zfh.controller;


import com.zfh.result.R;
import com.zfh.service.IVoucherOrderService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  优惠卷订单controller
 * @author author
 * @since 2025-09-30
 */
@RestController
@RequestMapping("/client/voucher-order")
@Slf4j
public class VoucherOrderController {
    @Autowired
    private IVoucherOrderService voucherOrderService;

    /**
     * 秒杀优惠券
     * @param voucherId
     * @return
     */
    @PostMapping("seckill/{id}")
    public R seckillVoucher(@PathVariable("id") @NotNull Long voucherId) {
        log.info("秒杀优惠券:{}", voucherId);
        return R.OK(voucherOrderService.seckillVoucher(voucherId));
    }

}
