package com.zfh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zfh.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户服务接口
 */
public interface UserService extends UserDetailsService, IService<User> {
}
