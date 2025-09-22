package com.zfh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 商铺Dto
 */
@Data
public class ShopDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 商铺ID
     */
    private Long id;

    /**
     * 商铺名称
     */
    @NotBlank(message = "商铺名称不能为空")
    @Size(max = 50, message = "商铺名称长度不能超过50个字符")
    private String name;

    /**
     * 商铺类型ID（关联shop_type.id）
     */
    @NotNull(message = "商铺类型ID不能为空")
    private Long typeId;

    /**
     * 地址
     */
    @NotBlank(message = "地址不能为空")
    @Size(max = 200, message = "地址长度不能超过200个字符")
    private String address;


    /**
     * 封面图URL
     */
    private String coverImage;

    /**
     * 详细描述
     */
    @Size(max = 500, message = "详细描述长度不能超过500个字符")
    private String description;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;


    /**
     * 营业时间（JSON格式，如{"周一":"9:00-22:00"}）
     */
    @NotBlank(message = "营业时间不能为空")
    @Pattern(regexp = "^\\{.*}$", message = "营业时间必须为JSON格式")
    private String businessHours;
}