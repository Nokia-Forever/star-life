package com.zfh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀优惠券DTO
 */
@Data
public class SeckillVoucherDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 商铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 代金券标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200")
    private String title;

    /**
     * 副标题
     */
    @Size(max = 200, message = "副标题长度不能超过200")
    private String subTitle;

    /**
     * 使用规则
     */
    @NotBlank(message = "使用规则不能为空")
    @Size(max = 1000, message = "使用规则长度不能超过1000")
    private String rules;

    /**
     * 支付金额，单位是分。例如200代表2元
     */
    @NotNull
    private Long payValue;

    /**
     * 抵扣金额，单位是分。例如200代表2元
     */
    @NotNull
    private Long actualValue;

    /**
     * 库存
     */
    @NotNull
    private Integer stock;

    /**
     * 秒杀开始时间
     */
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;

    /**
     * 秒杀结束时间
     */
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

}
