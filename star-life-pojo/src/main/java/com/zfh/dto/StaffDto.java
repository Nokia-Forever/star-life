package com.zfh.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 职员dto
 */
@Data
public class StaffDto implements Serializable {
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
    private String username;


    /**
     * 职员Id
     */
    private Long staffId;

    private static final long serialVersionUID = 1L;
}
