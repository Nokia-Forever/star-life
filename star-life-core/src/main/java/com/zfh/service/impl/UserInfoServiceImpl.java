package com.zfh.service.impl;

import com.zfh.constant.UserConstant;
import com.zfh.entity.User;
import com.zfh.entity.UserInfo;
import com.zfh.mapper.UserInfoMapper;
import com.zfh.service.IUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-09-18
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    /**
     * 注册用户信息
     * @param user
     * @return
     */
    @Override
    public int registerInfo(User user) {
        Date now = new Date();
        UserInfo userInfo = UserInfo.builder().userId(user.getId())
                .gender(UserConstant.GENDER_UNKNOWN)
                .city("")
                .introduce("")
                .signature("")
                .fans(0L)
                .followee(0L)
                .credits(0L)
                .level(UserConstant.MEMBER_LEVEL_0)
                .createTime(now)
                .updateTime(now)
                .build();
        return this.save(userInfo) ? 1 : 0;
    }
}
