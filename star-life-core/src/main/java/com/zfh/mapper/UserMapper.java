package com.zfh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zfh.entity.User;
import com.zfh.vo.UserSelfVo;
import com.zfh.vo.UserVo;

import java.util.List;

/**
 * 用户持久层接口
 */
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据id查询用户信息(自己)
     * @param id
     * @return
     */
    UserSelfVo selectUserSelfInfoById(Long id);

    /**
     * 根据id查询密码
     * @param id
     * @return
     */
    String getPasswordById(Long id);


    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    UserVo selectUserInfoById(Long id);


    /**
     * 根据id列表查询用户信息
     * @param ids
     * @return
     */
    List<UserVo> selectUserVoListByIds(List<Long> ids);
}
