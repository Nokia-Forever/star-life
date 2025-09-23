package com.zfh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 职员dto
 */
@Data
public class StaffDto {
    /**
     * 商铺id
     */
    @NotNull
    private Long shopId;
    /**
     * 角色id
     */
    @NotNull
    private Long roleId;
    /**
     * 职员用户名
     */
    @NotBlank
    private String username;
}
