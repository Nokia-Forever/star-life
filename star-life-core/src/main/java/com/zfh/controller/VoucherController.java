package com.zfh.controller;


import com.zfh.dto.SeckillVoucherDto;
import com.zfh.dto.VoucherDto;
import com.zfh.dto.VoucherStatusDto;
import com.zfh.result.R;
import com.zfh.service.IVoucherService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *  优惠卷controller
 *
 * @author author
 * @since 2025-09-29
 */
@RestController
@RequestMapping("/client/voucher")
@Slf4j
public class VoucherController {
    @Autowired
    private IVoucherService voucherService;

    /**
     * 新增普通优惠券
     * @param voucherDto
     * @return
     */
    @PreAuthorize("hasAnyRole(#voucherDto.shopId + '_' + T(com.zfh.constant.StaffConstant).CEO," +
            "#voucherDto.shopId + '_' + T(com.zfh.constant.StaffConstant).Manger)" )
    @PostMapping()
    public R add(@RequestBody @Valid VoucherDto voucherDto){
        log.info("新增普通优惠券:{}", voucherDto);
        return R.OK(voucherService.add(voucherDto));
    }

    /**
     * 新增秒杀优惠券
     * @param seckillVoucherDto
     * @return
     */
    @PreAuthorize("hasAnyRole(#seckillVoucherDto.shopId + '_' + T(com.zfh.constant.StaffConstant).CEO," +
            "#seckillVoucherDto.shopId + '_' + T(com.zfh.constant.StaffConstant).Manger)" )
    @PostMapping("/seckill")
    public R addSeckill(@RequestBody @Valid SeckillVoucherDto seckillVoucherDto){
        log.info("新增秒杀优惠券:{}", seckillVoucherDto);
        return R.OK(voucherService.addSeckill(seckillVoucherDto));
    }

    /**
     * 员工查询店铺优惠卷信息
     * @param shopId
     * @return
     */
    @PreAuthorize("hasAnyRole(#shopId + '_' + T(com.zfh.constant.StaffConstant).CEO," +
            "#shopId + '_' + T(com.zfh.constant.StaffConstant).Manger," +
            "#shopId + '_' + T(com.zfh.constant.StaffConstant).Salesclerk," +
            "#shopId + '_' + T(com.zfh.constant.StaffConstant).CustomerService) ")
    @GetMapping("/staff/{shopId}")
    public R getShopVoucherForStaff(@PathVariable Long shopId){
        log.info("查询店铺优惠卷信息:{}", shopId);
        return R.OK(voucherService.listByShopId(shopId));
    }

    /**
     * 优惠卷状态改变
     * @return
     */
    @PreAuthorize("hasAnyRole(#voucherStatusDto.shopId + '_' + T(com.zfh.constant.StaffConstant).CEO," +
            "#voucherStatusDto.shopId + '_' + T(com.zfh.constant.StaffConstant).Manger)" )
    @PutMapping("/staff/status")
    public R changeStatus(@RequestBody @Valid VoucherStatusDto voucherStatusDto){
        log.info("优惠卷状态改变:{}", voucherStatusDto);
        return R.OK(voucherService.changeStatus(voucherStatusDto));
    }


    /**
     * 查询店铺上架的优惠卷信息
     * @param shopId
     * @return
     */
    @GetMapping("/white/{shopId}")
    public R getShopVoucherOnline(@PathVariable Long shopId){
        log.info("查询店铺上架的优惠卷信息:{}", shopId);
        return R.OK(voucherService.listOnlineByShopId(shopId));
    }

    /**
     * 查询单个优惠卷信息
     * @param id
     * @return
     */
    @GetMapping("/white/{id}")
    public R getVoucherOnline(@PathVariable Long id){
        log.info("查询单个优惠卷信息:{}", id);
        return R.OK(voucherService.getInfoById(id));
    }

    /**
     * 修改普通优惠卷信息
     * @param voucherDto
     * @return
     */
    @PreAuthorize("hasAnyRole(#voucherDto.shopId + '_' + T(com.zfh.constant.StaffConstant).CEO," +
            "#voucherDto.shopId + '_' + T(com.zfh.constant.StaffConstant).Manger)" )
    @PutMapping
    public R update(@RequestBody @Valid VoucherDto voucherDto){
        log.info("修改普通优惠卷信息:{}", voucherDto);
        return R.OK(voucherService.updateInfo(voucherDto));
    }

    /**
     * 修改秒杀优惠卷信息
     * @param seckillVoucherDto
     * @return
     */
    @PreAuthorize("hasAnyRole(#seckillVoucherDto.shopId + '_' + T(com.zfh.constant.StaffConstant).CEO," +
            "#seckillVoucherDto.shopId + '_' + T(com.zfh.constant.StaffConstant).Manger)" )
    @PutMapping("/seckill")
    public R updateSeckill(@RequestBody @Valid SeckillVoucherDto seckillVoucherDto){
        log.info("修改秒杀优惠券:{}", seckillVoucherDto);
        return R.OK(voucherService.updateSeckill(seckillVoucherDto));
    }
}
