package com.zfh.controller;


import com.zfh.dto.ShopDto;
import com.zfh.result.R;
import com.zfh.service.IShopService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商铺基础信息表
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
     * @param shopDto
     * @return
     */
    @PostMapping
    public R applyShop(@RequestBody @Valid ShopDto shopDto) {
        log.info("申请商铺：{}", shopDto);
        return R.OK(shopService.applyShop(shopDto));
    }

//    //测试
//    @PreAuthorize("hasAnyRole(#id + '_' + T(com.zfh.constant.StaffConstant).CEO)")
//    @GetMapping("/{id}")
//    public R test(@PathVariable  Long  id) {
//        return R.OK("获取成功");
//    }

    /**
     * 获取商铺信息
     * @param id
     * @return
     */
    @GetMapping("/white/detail/{id}")
    public R getShopInfo(@PathVariable Long id) {
        return R.OK(shopService.getInfoById(id));
    }




}
