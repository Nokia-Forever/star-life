package com.zfh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import com.zfh.vo.UserRoleVo;
import com.zfh.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private StaffMapper staffMapper;

    /**
     * 获取当前用户角色下权重比自己小
     *
     * @param shopId
     * @param roleId
     * @return
     */
    @Override
    public List<Role> getRoleLessPowerList(Long shopId, Long roleId) {
        //先获取当前用户
        User user = CurrentHolder.getCurrentUser();
        if (!user.getUserType().equals(UserConstant.USER_TYPE_BUSINESS)) {
            throw new StaffException(ExceptionConstant.NOT_BUSINESS);
        }
        //获取角色信息
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(RedisKeyConstant.STAFF_ROLE_KEY + roleId);
        Role nowRole = objectMapper.convertValue(map, Role.class);
        List<Role> roleList = roleService.list();
        return roleList.stream().filter(role -> role.getPower() > nowRole.getPower()).toList();
    }


    /**
     * 添加店员
     *
     * @param staffDto
     * @return
     */
    @Transactional
    @Override
    public int addStaff(StaffDto staffDto) {
        //验证用户名
        if (staffDto.getUsername() == null) {
            throw new StaffException(ExceptionConstant.USER_NOT_EXIST);
        }

        //获取当前用户对象
        User user = CurrentHolder.getCurrentUser();
        //非商家不可添加店员
        if (!user.getUserType().equals(UserConstant.USER_TYPE_BUSINESS)) {
            throw new StaffException(ExceptionConstant.NOT_BUSINESS);
        }

        //查找用户是否存在
        Long staffId = userService.getIdByUsername(staffDto.getUsername());
        if (staffId == null) {
            throw new UserException(ExceptionConstant.USER_NOT_EXIST);
        }

        //验证当前用户权限
        verifyCurrentUserPermission(staffDto, user);

        //查询用户是否已经是该店店员
        if (getOne(new LambdaQueryWrapper<Staff>().eq(Staff::getUserId, staffId)
                .eq(Staff::getShopId, staffDto.getShopId())) != null) {
            throw new StaffException(ExceptionConstant.USER_IS_STAFF);
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

    /**
     * 修改职员权限
     *
     * @param staffDto
     * @return
     */
    @Transactional
    @Override
    public int updateStaff(StaffDto staffDto) {
        //获取当前用户对象
        User user = CurrentHolder.getCurrentUser();
        //操作者非商家不可添加店员
        if (!user.getUserType().equals(UserConstant.USER_TYPE_BUSINESS)) {
            throw new StaffException(ExceptionConstant.NOT_BUSINESS);
        }

        Long staffId = staffDto.getStaffId();
        if (staffId == null) {
            throw new UserException(ExceptionConstant.USER_NOT_EXIST);
        }

        //查找用户是否存在
        User user1 = userService.getById(staffId);
        if (user1 == null) {
            throw new UserException(ExceptionConstant.USER_NOT_EXIST);
        }

        //验证当前用户权限
        verifyCurrentUserPermission(staffDto, user);

        //查询用户是否是该店店员
        if (getOne(new LambdaQueryWrapper<Staff>()
                .eq(Staff::getUserId, staffId).eq(Staff::getShopId, staffDto.getShopId())) == null) {
            throw new StaffException(ExceptionConstant.USER_IS_NOT_STAFF);
        }

        //改变用户角色
        update(new LambdaUpdateWrapper<Staff>()
                .set(Staff::getUpdateTime, new Date()).set(Staff::getRoleId, staffDto.getRoleId())
                .eq(Staff::getUserId, staffId));

        //TODO 这里直接把用户下线
        stringRedisTemplate.delete(RedisKeyConstant.USER_TOKEN_KEY + staffId);
        return 1;
    }

    /**
     * 获取店员列表
     *
     * @param shopId
     * @return
     */
    @Override
    public List<UserRoleVo> getStaffList(Long shopId) {
        //获取店员id列表
        List<Staff> staffList = list(new LambdaQueryWrapper<Staff>()
                .select(Staff::getUserId, Staff::getRoleId).eq(Staff::getShopId, shopId));
        if(staffList.isEmpty()){
            return Collections.emptyList();
        }

        List<UserVo> userVoList = userService.selectUserVoListByIds(staffList.stream().map(Staff::getUserId).toList());
        //用户id-角色id
        Map<Long, Long> collect = staffList.stream().collect(Collectors.toMap(Staff::getUserId, Staff::getRoleId));

        //数据库查询所有角色
        Map<Long,Map> roleList = roleService.listMap();
        HashMap<Long,Role> roleMap = new HashMap<>();
        roleList.forEach((key, value) -> roleMap.put(key, objectMapper.convertValue(value, Role.class)));
        return userVoList.stream().map(userVo -> {
            UserRoleVo userRoleVo = new UserRoleVo();
            userRoleVo.setUserVo(userVo);

            userRoleVo.setRole(roleMap.get(collect.get(userVo.getId())));
            return userRoleVo;
            }).toList();
    }

    /**
     * 获取店员详细信息
     * @param  shopId
     * @param id
     * @return
     */
    @Override
    public UserRoleVo getStaffInfo(Long shopId,Long id) {
        //先查询用户信息
        UserVo userVo = userService.getInfoById(id);
        if(userVo == null){
            throw new UserException(ExceptionConstant.USER_NOT_EXIST);
        }
        //再查询角色信息
        Staff staff = staffMapper.selectOne(new LambdaQueryWrapper<Staff>().eq(Staff::getUserId, id).eq(Staff::getShopId, shopId));
        if(staff == null){
            throw new StaffException(ExceptionConstant.USER_IS_NOT_STAFF);
        }

        //查询角色信息
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(RedisKeyConstant.STAFF_ROLE_KEY + staff.getRoleId());
        Role role = objectMapper.convertValue(entries, Role.class);

        UserRoleVo userRoleVo = new UserRoleVo();
        userRoleVo.setUserVo(userVo);
        userRoleVo.setRole(role);
        return userRoleVo;
    }

    /**
     * 验证当前用户权限
     *
     * @param staffDto
     * @param user
     */
    private void verifyCurrentUserPermission(StaffDto staffDto, User user) {
        //获取权限列表
        Map<Long, String> currentUserAuthorityStr = CurrentHolder.getCurrentUserAuthorityStr();
        //不是该店铺员工
        if (!currentUserAuthorityStr.containsKey(staffDto.getShopId())) {
            throw new StaffException(ExceptionConstant.PERMISSION_DENIED);
        }

        //获取操作者角色的权值
        String roleId = stringRedisTemplate.opsForValue()
                .get(RedisKeyConstant.STAFF_ROLE_KEY + currentUserAuthorityStr.get(staffDto.getShopId()));
        long currentUserPower = Long.parseLong((String) Objects.requireNonNull(stringRedisTemplate.opsForHash()
                .get(RedisKeyConstant.STAFF_ROLE_KEY + roleId, "power")));


        //获取要分配的角色权值
        long staffPower = Long.parseLong((String) Objects.requireNonNull(stringRedisTemplate.opsForHash()
                .get(RedisKeyConstant.STAFF_ROLE_KEY + staffDto.getRoleId(), "power")));


        //当前操作者权限低
        if (currentUserPower >= staffPower) {
            throw new StaffException(ExceptionConstant.PERMISSION_DENIED);
        }
    }
}
