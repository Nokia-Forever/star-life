package com.zfh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.constant.ExceptionConstant;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.constant.UserConstant;
import com.zfh.dto.StaffDto;
import com.zfh.entity.Role;
import com.zfh.entity.Staff;
import com.zfh.entity.User;
import com.zfh.exception.StaffException;
import com.zfh.exception.UserException;
import com.zfh.mapper.StaffMapper;
import com.zfh.service.IRoleService;
import com.zfh.service.IShopService;
import com.zfh.service.IStaffService;
import com.zfh.service.IUserService;
import com.zfh.utils.CurrentHolder;
import com.zfh.vo.ShopRoleVo;
import com.zfh.vo.ShopVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 店员关联表 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-09-22
 */
@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements IStaffService {
    @Autowired
    private IRoleService roleService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    @Lazy
    private IShopService shopService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    @Lazy
    private IUserService userService;

    /**
     * 获取当前用户角色下权重比自己小
     * @param shopId
     * @param roleId
     * @return
     */
    @Override
    public List<Role> getRoleLessPowerList(Long shopId,Long roleId) {
       //先获取当前用户
        User user = CurrentHolder.getCurrentUser();
        if(!user.getUserType().equals(UserConstant.USER_TYPE_BUSINESS)){
            throw new StaffException(ExceptionConstant.NOT_BUSINESS);
        }
        //获取角色信息
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(RedisKeyConstant.STAFF_ROLE_KEY+roleId);
        Role nowRole = objectMapper.convertValue(map, Role.class);
        List<Role> roleList = roleService.list();
        return roleList.stream().filter(role -> role.getPower() > nowRole.getPower()).toList();
    }

    /**
     * 获取店员角色列表
     * @return
     */
    @Override
    public List<ShopRoleVo> getShopList() {
        //获取当前登录用户
        User user = CurrentHolder.getCurrentUser();
        //非商家不可获取
        if(!user.getUserType().equals(UserConstant.USER_TYPE_BUSINESS)){
            throw new StaffException(ExceptionConstant.NOT_BUSINESS);
        }

        //根据user_id查询
        LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Staff::getUserId, user.getId());
        List<Staff> staffList = list(queryWrapper);
        if(staffList.isEmpty()){
            return null;
        }

        //查询商铺信息
        Map<Long, ShopVo> shopMap = shopService.getInfoByIds(staffList.stream().map(Staff::getShopId).toList());
        if(shopMap.isEmpty()){
            return null;
        }

        return staffList.stream().map(staff -> {
            ShopRoleVo shopRoleVo = new ShopRoleVo();
            shopRoleVo.setShopVo(shopMap.get(staff.getShopId()));
            //角色信息从角色表获取
            Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(RedisKeyConstant.STAFF_ROLE_KEY+staff.getRoleId());
           shopRoleVo.setRole(objectMapper.convertValue(map, Role.class));
            return shopRoleVo;
        }).toList();
    }

    /**
     * 添加店员
     * @param staffDto
     * @return
     */
    @Transactional
    @Override
    public int addStaff(StaffDto staffDto) {
        //获取当前用户对象
        User user = CurrentHolder.getCurrentUser();
        //非商家不可添加店员
        if(!user.getUserType().equals(UserConstant.USER_TYPE_BUSINESS)){
            throw new StaffException(ExceptionConstant.NOT_BUSINESS);
        }

        //查找用户是否存在
        Long staffId =userService.getIdByUsername(staffDto.getUsername());
        if(staffId == null){
            throw new UserException(ExceptionConstant.USER_NOT_EXIST);
        }

        //查询用户是否已经是该店店员
        LambdaQueryWrapper<Staff> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Staff::getUserId, staffId).eq(Staff::getShopId, staffDto.getShopId());
        if(getOne(queryWrapper1) != null){
            throw new StaffException(ExceptionConstant.USER_IS_STAFF);
        }


        //查询当前用户的身份
        LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Staff::getUserId, user.getId()).eq(Staff::getShopId, staffDto.getShopId());
        Staff staff = getOne(queryWrapper);
        if(staff == null){
            throw new StaffException(ExceptionConstant.PERMISSION_DENIED);
        }

        //获取当前角色信息
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(RedisKeyConstant.STAFF_ROLE_KEY+staff.getRoleId());
        Role role = objectMapper.convertValue(map, Role.class);

        //获取要分配的角色信息
        map = stringRedisTemplate.opsForHash().entries(RedisKeyConstant.STAFF_ROLE_KEY+staffDto.getRoleId());
        Role role1 = objectMapper.convertValue(map, Role.class);

        //当前用户权限低
        if(role.getPower() >= role1.getPower()){
            throw new StaffException(ExceptionConstant.PERMISSION_DENIED);
        }

        //新增职员
        Staff staff1 = new Staff();
        staff1.setUserId(staffId);
        staff1.setShopId(staffDto.getShopId());
        staff1.setRoleId(staffDto.getRoleId());
        staff1.setCreateTime(new Date());
        staff1.setUpdateTime(new Date());

        //改变用户状态
        userService.update(new UpdateWrapper<User>().set("user_type", UserConstant.USER_TYPE_BUSINESS));
        //TODO 这里直接把用户下线
        stringRedisTemplate.delete(RedisKeyConstant.USER_TOKEN_KEY + staffId);
        return save(staff1) ? 1 : 0;
    }
}
