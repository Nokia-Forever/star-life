package com.zfh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zfh.entity.Admin;
import com.zfh.mapper.AdminMapper;
import com.zfh.service.IAdminService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 管理端用户表 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-09-30
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    /**
     * 根据用户名获取用户信息(springSecurity)
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       //查询用户是否被锁
        return null;
    }
}
