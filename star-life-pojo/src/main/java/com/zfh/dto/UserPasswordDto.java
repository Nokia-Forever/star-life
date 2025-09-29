package com.zfh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserPasswordDto implements Serializable {
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

    private static final long serialVersionUID = 1L;
}
