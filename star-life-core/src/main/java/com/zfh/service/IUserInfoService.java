package com.zfh.service;

import com.zfh.entity.User;
import com.zfh.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author author
 * @since 2025-09-18
 */
public interface IUserInfoService extends IService<UserInfo> {

    /**
     * 注册用户信息
     * @param user
     * @return
     */
    int registerInfo(User user);
}
