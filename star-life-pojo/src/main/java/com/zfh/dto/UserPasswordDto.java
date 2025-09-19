package com.zfh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserPasswordDto {
    /**
     * 密码
     */
    @NotBlank
    @Size(min = 6, max = 20, message = "密码长度在6-20之间")
    private String password;
    /**
     * 新密码
     */
    @NotBlank
    @Size(min = 6, max = 20, message = "密码长度在6-20之间")
    private String newPassword;
}
