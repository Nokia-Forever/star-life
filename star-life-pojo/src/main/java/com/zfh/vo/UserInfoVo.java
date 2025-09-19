package com.zfh.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户详细信息对象(外界)
 */
@Data
public class UserInfoVo implements Serializable {
    /**
     * 性别 0-未知，1-男，2-女
     */
    private Integer gender;
    /**
     * 生日
     */
    private Date birthday;
    /**
     * 个性签名
     */
    private String signature;
    /**
     * 城市
     */
    private String city;
    /**
     * 个人介绍
     */
    private String introduce;
    /**
     * 粉丝数量
     */
    private Long fans;
    /**
     * 关注数量
     */
    private Long followee;

    private static final long serialVersionUID = 1L;
}
