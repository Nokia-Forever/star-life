package com.zfh.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户vo返回对象
 * user
 */
@Data
public class UserVo implements Serializable {
    /**
     * 用户ID
     */
    private Long id;
    /**
     * 用户名（用于登录）
     */
    private String username;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 人物头像
     */
    private String icon;
    /**
     * 用户类型：0-普通用户，1-商家
     */
    private Byte userType;
    /**
     * 账户是否没有过期，0已过期 1正常
     */
    private Byte accountNoExpired;
    /**
     * 密码是否没有过期，0已过期 1正常
     */
    private Byte credentialsNoExpired;
    /**
     * 账号是否没有锁定，0已锁定 1正常
     */
    private Byte accountNoLocked;
    /**
     * 账号是否启用，0禁用 1启用
     */
    private Byte accountEnabled;
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    private String token;
}