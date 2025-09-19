package com.zfh.vo;

import com.zfh.entity.UserInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息 (自己)
 */
@Data
public class UserSelfVo implements Serializable {
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
    private Integer userType;
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
    /**
     * 用户详细信息
     */
    private UserInfo userInfo;

    private static final long serialVersionUID = 1L;
}
