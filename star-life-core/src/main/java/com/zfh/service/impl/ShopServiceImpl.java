package com.zfh.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.constant.*;
import com.zfh.dto.ShopBusinessHoursDto;
import com.zfh.dto.ShopDto;
import com.zfh.dto.ShopUpdateDto;
import com.zfh.entity.*;
import com.zfh.exception.ShopException;
import com.zfh.mapper.ShopMapper;
import com.zfh.service.IShopDetailService;
import com.zfh.service.IShopService;
import com.zfh.service.IStaffService;
import com.zfh.service.IUserService;
import com.zfh.utils.CurrentHolder;
import com.zfh.utils.TimeUtils;
import com.zfh.vo.ShopVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.zfh.constant.ShopConstant.*;

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
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 申请商铺
     *
     * @param shopDto
     * @return
     */
    @Transactional
    @Override
    public int applyShop(ShopDto shopDto) {
        Date now = new Date();
        //获取当前 用户
        Long userId = CurrentHolder.getCurrentUser().getId();

        if (userId == null) {
            throw new ShopException(ExceptionConstant.USER_NOT_LOGIN);
        }


        //先创建基本信息
        Shop shop = new Shop();
        BeanUtils.copyProperties(shopDto, shop);
        shop.setUserId(userId);
        shop.setStatus(ShopConstant.SHOP_STATUS_AUTO_CLOSE);
        shop.setCreateTime(now);
        shop.setUpdateTime(now);
        save(shop);

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
        userService.beBussiness(userId);

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
     *
     * @param id
     * @return
     */
    @Override
    public ShopVo getInfoById(Long id) {
        ShopVo shopVo = shopMapper.getInfoById(id);
        //设置营业状态
        if (shopVo.getStatus() == SHOP_ONLINE) {
            shopVo.setBusinessStatus(getShopStatus(id));
        } else {
            shopVo.setBusinessStatus(false);
        }
        return shopVo;
    }

    /**
     * 获取上线店铺营业时间
     *
     * @return
     */
    @Override
    public List<BusinessHours> getBusinessHoursList() {
        return shopMapper.getBusinessHoursList();
    }

    /**
     * 手动开启或关闭商铺
     *
     * @param id
     * @param status
     * @return
     */
    @Override
    public int manualOpenOrCloseShop(Long id, Boolean status) {
        if (status) {
            stringRedisTemplate.opsForHash().put(RedisKeyConstant.SHOP_STATUS_KEY, String.valueOf(id), String.valueOf(SHOP_STATUS_MANUAL_OPEN));
        } else {
            stringRedisTemplate.opsForHash().put(RedisKeyConstant.SHOP_STATUS_KEY, String.valueOf(id), String.valueOf(SHOP_STATUS_MANUAL_CLOSE));
        }
        return 1;
    }

    /**
     * 清除手动处理营业状态
     *
     * @param id
     * @return
     */
    @Override
    public int deleteShopStatusManual(Long id) {
        //获取营业时间
        Map<String, String> map = shopDetailService.selectBusinessHoursById(id);
        LocalDateTime now = LocalDateTime.now();
        //重新计算营业时间
        //为空,则关闭
        if (map.isEmpty()) {
            stringRedisTemplate.opsForHash().put(RedisKeyConstant.SHOP_STATUS_KEY, id.toString(), String.valueOf(SHOP_STATUS_AUTO_CLOSE));
        }
        //找不到对应的星期
        else if (!map.containsKey(now.getDayOfWeek().toString())) {
            stringRedisTemplate.opsForHash().put(RedisKeyConstant.SHOP_STATUS_KEY, id.toString(), String.valueOf(SHOP_STATUS_AUTO_CLOSE));
        }
        //不在营业时间
        else if (!TimeUtils.isInTimeRange(now.toLocalTime(), map.get(now.getDayOfWeek().toString()))) {
            stringRedisTemplate.opsForHash().put(RedisKeyConstant.SHOP_STATUS_KEY, id.toString(), String.valueOf(SHOP_STATUS_AUTO_CLOSE));
        } else {
            stringRedisTemplate.opsForHash().put(RedisKeyConstant.SHOP_STATUS_KEY, id.toString(), String.valueOf(SHOP_STATUS_AUTO_OPEN));
        }
        return 1;
    }

    /**
     * 获取商铺营业状态
     *
     * @param id
     * @return
     */
    @Override
    public Boolean getShopStatus(Long id) {
        int status = Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisKeyConstant.SHOP_STATUS_KEY, id.toString())).toString());
        return status == SHOP_STATUS_AUTO_OPEN || status == SHOP_STATUS_MANUAL_OPEN;
    }

    /**
     * 批量获取商铺信息
     *
     * @param ids
     * @return
     */
    @Override
    public Map<Long, ShopVo> getInfoByIds(List<Long> ids) {
        Map<Long, ShopVo> shopVo = shopMapper.getInfoByIds(ids);
        //设置营业状态
        shopVo.forEach((id, shopVop) -> {
            if (shopVop.getStatus() == SHOP_ONLINE) {
                shopVop.setBusinessStatus(getShopStatus(id));
            } else {
                shopVop.setBusinessStatus(false);
            }
        });
        return shopVo;
    }

    /**
     * 更改商铺营业状态
     *
     * @param status
     * @param shopId
     * @return
     */
    @Override
    public int updateShopStatus(Long shopId, Boolean status) {
        if (status) {
            //更新数据库
            update(new LambdaUpdateWrapper<Shop>().set(Shop::getStatus, SHOP_ONLINE).eq(Shop::getId, shopId));
            //增加营业状态,刚上线为手动营业状态
            stringRedisTemplate.opsForHash()
                    .put(RedisKeyConstant.SHOP_STATUS_KEY, shopId.toString(), String.valueOf(SHOP_STATUS_MANUAL_OPEN));
        } else {
            //更新数据库
            update(new LambdaUpdateWrapper<Shop>()
                    .set(Shop::getStatus, SHOP_OFFLINE).eq(Shop::getId, shopId));
            //删除营业状态
            stringRedisTemplate.opsForHash().delete(RedisKeyConstant.SHOP_STATUS_KEY, shopId.toString());
        }
        return 1;
    }

    /**
     * 修改商铺信息
     *
     * @param shopUpdateDto
     * @return
     */
    @Transactional
    @Override
    public boolean updateShop(ShopUpdateDto shopUpdateDto) {
        Shop shop = new Shop();
        shop.setId(shopUpdateDto.getId());
        shop.setName(shopUpdateDto.getName());
        shop.setAddress(shopUpdateDto.getAddress());
        shop.setCoverImage(shopUpdateDto.getCoverImage());
        //更新商铺详情
        LambdaUpdateWrapper<ShopDetail> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ShopDetail::getShopId, shopUpdateDto.getId());

        if (shopUpdateDto.getDescription() != null) {
            updateWrapper.set(ShopDetail::getDescription, shopUpdateDto.getDescription());
        }

        if (shopUpdateDto.getContactPhone() != null) {
            updateWrapper.set(ShopDetail::getContactPhone, shopUpdateDto.getContactPhone());
        }

        return shopDetailService.update(updateWrapper);
    }

    /**
     * 修改商铺营业时间
     *
     * @param shopBusinessHoursDto
     * @return
     */
    @Override
    public boolean updateBusinessHours(ShopBusinessHoursDto shopBusinessHoursDto) {
        return shopDetailService.update(new LambdaUpdateWrapper<ShopDetail>()
                .set(ShopDetail::getBusinessHours, shopBusinessHoursDto.getBusinessHours())
                .eq(ShopDetail::getShopId, shopBusinessHoursDto.getId()));
    }

    //TODO 删除店铺

}
