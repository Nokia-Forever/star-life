package com.zfh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 用户认证表
 * user
 */
@Data
public class User implements Serializable, UserDetails {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（用于登录）
     */
    private String username;

    /**
     * 加密后的密码
     */
    private String password;

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
    private Integer userType;

    /**
     * 账户是否没有过期，0已过期 1正常
     */
    private Integer accountNoExpired;

    /**
     * 密码是否没有过期，0已过期 1正常
     */
    private Integer credentialsNoExpired;

    /**
     * 账号是否没有锁定，0已锁定 1正常
     */
    private Integer accountNoLocked;

    /**
     * 账号是否启用，0禁用 1启用
     */
    private Integer accountEnabled;

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

    /**
     * 获取用户权限
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }


    /**
     * 账号是否没有过期
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return this.accountNoExpired == 1;
    }

    /**
     * 账号是否没有锁定
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.accountNoLocked == 1;
    }


    /**
     * 密码是否没有过期
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNoExpired == 1;
    }

    /**
     * 账号是否启用
     * @return
     */
    @Override
    public boolean isEnabled() {
        return this.accountEnabled == 1;
    }
}