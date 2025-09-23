package com.zfh.controller;


import com.zfh.dto.ShopDto;
import com.zfh.result.R;
import com.zfh.service.IShopService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 商铺controller
 *
 * @author author
 * @since 2025-09-22
 */
@RestController
@RequestMapping("/client/shop")
@Slf4j
public class ShopController {
    @Autowired
    private IShopService shopService;

    //TODO 申请店铺应该经过管理端的认证
    /**
     * 申请商铺
     *
     * @param shopDto
     * @return
     */
    @PostMapping
    public R applyShop(@RequestBody @Valid ShopDto shopDto) {
        log.info("申请商铺：{}", shopDto);
        return R.OK(shopService.applyShop(shopDto));
    }

    //测试
    @PreAuthorize("hasAnyRole(#id + '_' + T(com.zfh.constant.StaffConstant).CEO)")
    @GetMapping("/{id}")
    public R test(@PathVariable Long id) {
        return R.OK("获取成功");
    }

    /**
     * 获取商铺信息
     *
     * @param id
     * @return
     */
    @GetMapping("/white/detail/{id}")
    public R getShopInfo(@PathVariable Long id) {
        return R.OK(shopService.getInfoById(id));
    }

    /**
     * 获取店铺营业状态
     *
     * @param id
     * @return
     */
    @GetMapping("/white/status/{id}")
    public R getShopStatus(@PathVariable Long id) {
        return R.OK(shopService.getShopStatus(id));
    }


    /**
     * 手动开启或关闭商铺
     *
     * @param id
     * @param status
     * @return
     */
    @PreAuthorize("hasAnyRole(#id + '_' + T(com.zfh.constant.StaffConstant).CEO)")
    @PutMapping("/manual/{id}/{status}")
    public R manualOpenOrCloseShop(@PathVariable Long id, @PathVariable Boolean status) {
        return R.OK(shopService.manualOpenOrCloseShop(id, status));
    }

    /**
     * 清除手动处理营业状态
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAnyRole(#id + '_' + T(com.zfh.constant.StaffConstant).CEO," +
            "#id + '_' + T(com.zfh.constant.StaffConstant).Manger," +
            "#id + '_' + T(com.zfh.constant.StaffConstant).Salesclerk," +
            "#id + '_' + T(com.zfh.constant.StaffConstant).CustomerService) ")
    @DeleteMapping("/manual/status")
    public R deleteShopStatus(@RequestParam Long id) {
        return R.OK(shopService.deleteShopStatusManual(id));
    }

}


