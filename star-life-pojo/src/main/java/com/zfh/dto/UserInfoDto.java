package com.zfh.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

/**
 * 用户信息请求对象
 */
@Data
public class UserInfoDto {
    /**
     * 用户ID
     */
    private Long id;
    /**
     * 用户名（用于登录）
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度在4-20之间")
    private String username;
    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 20, message = "昵称长度在1-20之间")
    private String nickName;
    /**
     * 人物头像
     */
    private String icon;
    /**
     * 性别 0-未知，1-男，2-女
     */
    @Min(value = 0)
    @Max(value = 2)
    private Integer gender;
    /**
     * 生日
     */
    private Date birthday;
    /**
     * 个性签名
     */
    @Size(max = 50, message = "个性签名长度不能超过50")
    private String signature;
    /**
     * 城市
     */
    private String city;
    /**
     * 个人介绍
     */
    @Size(max = 100, message = "个人介绍长度不能超过200")
    private String introduce;
}
