package com.zfh.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zfh.entity.ShopType;
import com.zfh.result.R;
import com.zfh.service.IShopTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商铺类型表

 * 客户端只有查看的权限
 * @author author
 * @since 2025-09-21
 */
@RestController("clientShopTypeController")
@RequestMapping("/client/shop-type")
public class ShopTypeController {
    @Autowired
    private IShopTypeService shopTypeService;

    /**
     * 查询所有商铺类型
     * @return
     */
    @GetMapping("/list")
    public R list() {
        return R.OK(shopTypeService.listShopType());
    }
}
