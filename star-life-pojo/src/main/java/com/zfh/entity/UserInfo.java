package com.zfh.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author author
 * @since 2025-09-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_info")
@Builder
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，用户id（关联user.id）
     */
    private Long userId;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 城市名称
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
     * 会员级别：0-普通用户，1-9级会员
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


}
