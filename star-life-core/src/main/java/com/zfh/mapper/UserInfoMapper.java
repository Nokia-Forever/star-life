package com.zfh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zfh.entity.UserInfo;
import com.zfh.vo.UserInfoVo;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2025-09-18
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    UserInfoVo selectUserInfoById(Long id);

}
