package com.zfh.vo;

import com.zfh.entity.Role;
import lombok.Data;

/**
 * 用户角色信息
 */
@Data
public class UserRoleVo {
    /**
     * 用户信息
     */
    private UserVo userVo;
    /**
     * 角色信息
     */
    private Role role;
}
