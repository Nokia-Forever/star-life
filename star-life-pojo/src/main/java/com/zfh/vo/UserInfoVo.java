package com.zfh.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户详细信息对象
 */
@Data
@Builder
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
    /**
     * 积分
     */
    private Long credits;
    /**
     * 等级
     */
    private Integer level;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
