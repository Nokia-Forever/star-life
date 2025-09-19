package com.zfh.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zfh.constant.UserConstant;
import com.zfh.entity.User;
import com.zfh.entity.UserInfo;
import com.zfh.mapper.UserInfoMapper;
import com.zfh.service.IUserInfoService;
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
        UserInfo userInfo=new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setCreateTime(now);
        userInfo.setUpdateTime(now);
        userInfo.setGender(UserConstant.GENDER_UNKNOWN);
        userInfo.setLevel(UserConstant.MEMBER_LEVEL_0);
        userInfo.setCredits(0L);
        userInfo.setFans(0L);
        userInfo.setFollowee(0L);
        userInfo.setIntroduce("");
        userInfo.setSignature("");
        userInfo.setCity("");
        return this.save(userInfo) ? 1 : 0;
    }

    /**
     * 修改粉丝数
     * @param id
     * @param count
     * @return
     */
    @Override
    public int changeFansCount(Long id, Integer count) {
        LambdaUpdateWrapper< UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .setSql("fans = fans "+ (count>=0?"+ ":"- ") +count) // 直接写SQL片段：
                .eq(UserInfo::getUserId, id);            // 条件：

        return update(null, updateWrapper) ?1:0;
    }

    /**
     * 修改关注数
     * @param id
     * @param count
     * @return
     */
    @Override
    public int changeFollowingCount(Long id, Integer count) {
        LambdaUpdateWrapper< UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .setSql("followee = followee "+ (count>=0?"+ ":"- ") +count) // 直接写SQL片段：
                .eq(UserInfo::getUserId, id);            // 条件：
        return update(null, updateWrapper) ?1:0;
    }
}
