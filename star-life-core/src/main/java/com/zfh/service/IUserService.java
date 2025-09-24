package com.zfh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zfh.dto.UserInfoDto;
import com.zfh.dto.UserPasswordDto;
import com.zfh.dto.UserRegisterDto;
import com.zfh.entity.User;
import com.zfh.vo.UserSelfVo;
import com.zfh.vo.UserVo;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

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

    /**
     * 获取当前用户信息
     * @return
     */
    UserSelfVo getCurrentUserInfo();

    /**
     * 修改当前用户信息
     * @param userInfoDto
     * @return
     */
    int updateCurrent(UserInfoDto userInfoDto);


    /**
     * 修改当前用户密码
     *
     * @param userPasswordDto
     * @return
     */
    boolean updateCurrentPassword(UserPasswordDto userPasswordDto);

    /**
     * 获取用户信息
     * @param id
     * @return
     */
    UserVo getInfoById(Long id);

    /**
     * 批量获取用户信息
     * @param ids
     * @return
     */
    List<UserVo> selectUserVoListByIds(List<Long> ids);

    /**
     * 根据用户名获取用户信息
     * @param username
     * @return
     */
    UserVo getInfoByUsername(String username);

    /**
     * 根据用户名获取用户id
     * @param username
     * @return
     */
    Long getIdByUsername(String username);

    /**
     * 成为商家
     *
     * @param userId
     * @return
     */
    boolean beBussiness(Long userId);
}
