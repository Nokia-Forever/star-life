package com.zfh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zfh.entity.Admin;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * <p>
 * 管理端用户表 服务类
 * </p>
 *
 * @author author
 * @since 2025-09-30
 */
public interface IAdminService extends IService<Admin> , UserDetailsService {

}
