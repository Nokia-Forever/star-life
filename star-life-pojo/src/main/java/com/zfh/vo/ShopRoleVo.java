package com.zfh.vo;

import com.zfh.entity.Role;
import lombok.Data;

/**
 * 商铺角色信息
 */
@Data
public class ShopRoleVo {
    /**
     * 商铺信息
     */
    private ShopVo shopVo;
    /**
     * 角色信息
     */
    private Role role;
}
