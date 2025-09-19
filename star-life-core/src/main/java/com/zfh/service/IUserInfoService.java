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

    /**
     * 修改用户粉丝数
     * @param id
     * @param count
     * @return
     */
    int changeFansCount(Long id, Integer  count);

    /**
     * 修改用户关注数
     * @param id
     * @param count
     * @return
     */
    int changeFollowingCount(Long id, Integer  count);
}
