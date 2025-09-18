package com.zfh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zfh.constant.ExceptionConstant;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.constant.UserConstant;
import com.zfh.dto.UserRegisterDto;
import com.zfh.entity.User;
import com.zfh.exception.UserLoginException;
import com.zfh.mapper.UserMapper;
import com.zfh.service.IUserInfoService;
import com.zfh.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.zfh.constant.UserConstant.USER_STATUS_ENABLE;

/**
 * 用户服务类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IUserInfoService userInfoService;
    /**
     * 根据用户名查询用户(spring-security)
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询用户被锁
        String s = stringRedisTemplate.opsForValue().get(RedisKeyConstant.USER_LOCK_KEY + username);
        int i=0;
        if (s != null) {
            i = Integer.parseInt(s);
        }
        if(i>=5){
            throw new UserLoginException(ExceptionConstant.USER_LOCK);
        }

        //根据用户名查询用户
        User user = this.getOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            stringRedisTemplate.opsForValue().increment(RedisKeyConstant.USER_LOCK_KEY + username);
            stringRedisTemplate.expire(RedisKeyConstant.USER_LOCK_KEY + username, RedisKeyConstant.USER_LOCK_EXPIRE_TIME, TimeUnit.MILLISECONDS);
            throw new UsernameNotFoundException(ExceptionConstant.USER_NOT_EXIST);
        }
        return user;
    }

    /**
     *  注册
     * @param userRegisterDto 注册信息
     * @return
     */
    @Transactional
    @Override
    public int register(UserRegisterDto userRegisterDto) {
        //查询用户名是否已存在
        User user = this.getOne(new QueryWrapper<User>().eq("username", userRegisterDto.getUsername()));
        if (user != null) {
            throw new UserLoginException(ExceptionConstant.USERNAME_EXIST);
        }

        //把密码加密
        String encode = passwordEncoder.encode(userRegisterDto.getPassword());
        user = new User();
        BeanUtils.copyProperties(userRegisterDto, user);
        user.setPassword(encode);

        Date now = new Date();
        //补充剩余信息
        user.setUserType(UserConstant.USER_TYPE_NORMAL);
        //security框架信息
        user.setAccountNoExpired(USER_STATUS_ENABLE);
        user.setCredentialsNoExpired(USER_STATUS_ENABLE);
        user.setAccountNoLocked(USER_STATUS_ENABLE);
        user.setAccountEnabled(USER_STATUS_ENABLE);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        boolean res1 = this.save(user);

        //创建详细信息
        return userInfoService.registerInfo(user);
    }
}
