package com.zfh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册对象
 */
@Data
public class UserRegisterDto {
    /*
    用户名
     */

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度在4-20之间")
    private String username;

    /*
    密码
     */

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度在6-20之间")
    private String password;

    /*
    昵称
     */

    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 20, message = "昵称长度在1-20之间")
    private String nickName;

    /*
    头像
     */
    private String icon;
}
