package com.zfh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 营业时间
 */
@Data
public class ShopBusinessHoursDto {
    /**
     * id
     */
    private Long id;
    /**
     * 营业时间（JSON格式，如{"TUESDAY":"9:00-22:00"}）
     */
    @NotBlank(message = "营业时间不能为空")
    @Pattern(regexp = "^\\{.*}$", message = "营业时间必须为JSON格式")
    private String businessHours;
}
