package com.zfh.vo;

import com.zfh.entity.Role;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色信息
 */
@Data
public class UserRoleVo implements Serializable {
    /**
     * 用户信息
     */
    private UserVo userVo;
    /**
     * 角色信息
     */
    private Role role;

    private static final long serialVersionUID = 1L;
}
