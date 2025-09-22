package com.zfh.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zfh.constant.*;
import com.zfh.dto.ShopDto;
import com.zfh.entity.Shop;
import com.zfh.entity.ShopDetail;
import com.zfh.entity.Staff;
import com.zfh.entity.User;
import com.zfh.exception.ShopException;
import com.zfh.mapper.ShopMapper;
import com.zfh.service.IShopDetailService;
import com.zfh.service.IShopService;
import com.zfh.service.IStaffService;
import com.zfh.service.IUserService;
import com.zfh.utils.CurrentHolder;
import com.zfh.vo.ShopVo;
import com.zfh.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 商铺基础信息表 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-09-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
    @Autowired
    private IShopDetailService shopDetailService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IUserService userService;
    @Autowired
    private IStaffService staffService;
    @Autowired
    private ShopMapper shopMapper;

    /**
     * 申请商铺
     * @param shopDto
     * @return
     */
    @Transactional
    @Override
    public int applyShop(ShopDto shopDto) {
        Date now = new Date();
        //获取当前 用户
        Long userId = CurrentHolder.getCurrentUser().getId();

        if (userId == null){
            throw new ShopException(ExceptionConstant.USER_NOT_LOGIN);
        }


        //先创建基本信息
        Shop shop = new Shop();
        BeanUtils.copyProperties(shopDto, shop);
        shop.setUserId(userId);
        shop.setStatus(ShopConstant.SHOP_STATUS_CLOSE);
        shop.setCreateTime(now);
        shop.setUpdateTime(now);
        save( shop);

        //再保存商铺详细信息
        ShopDetail shopDetail = new ShopDetail();
        shopDetail.setShopId(shop.getId());
        shopDetail.setContactPhone(shopDto.getContactPhone());
        shopDetail.setDescription(shopDto.getDescription());
        shopDetail.setBusinessHours(shopDto.getBusinessHours());
        shopDetail.setCreateTime(now);
        shopDetail.setUpdateTime(now);

        //更改用户信息
        stringRedisTemplate.opsForHash().put(RedisKeyConstant.USER_TOKEN_KEY + userId, "user_type", String.valueOf(UserConstant.USER_TYPE_BUSINESS));
        userService.update(new UpdateWrapper<User>().set("user_type", UserConstant.USER_TYPE_BUSINESS));

        //设立CEO,保存职称
        Long roleId = Long.parseLong(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(RedisKeyConstant.STAFF_ROLE_KEY + StaffConstant.CEO)));
        Staff staff = new Staff();
        staff.setUserId(userId);
        staff.setShopId(shop.getId());
        staff.setRoleId(roleId);
        staff.setCreateTime(now);
        staff.setUpdateTime(now);
        staffService.save(staff);

        return shopDetailService.save(shopDetail) ? 1 : 0;
    }

    /**
     * 获取商铺信息
     * @param id
     * @return
     */
    @Override
    public ShopVo getInfoById(Long id) {
        return shopMapper.getInfoById(id);
    }
}
