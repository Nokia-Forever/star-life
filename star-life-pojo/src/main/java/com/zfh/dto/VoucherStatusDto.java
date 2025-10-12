package com.zfh.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 优惠券状态Dto
 */
@Data
public class VoucherStatusDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 优惠券id
     */
    @NotNull
    private Long id;
    /**
     * 优惠券状态
     */
    @NotNull
    private Boolean status;
    /**
     * 商铺id
     */
    @NotNull
    private Long shopId;
}
