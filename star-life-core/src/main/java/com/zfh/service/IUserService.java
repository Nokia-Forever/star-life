package com.zfh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zfh.dto.UserRegisterDto;
import com.zfh.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户服务接口
 */
public interface IUserService extends UserDetailsService, IService<User> {
    /**
     * 注册
     *
     * @param userRegisterDto 注册信息
     * @return 注册结果
     */
    int register(UserRegisterDto userRegisterDto);
}
