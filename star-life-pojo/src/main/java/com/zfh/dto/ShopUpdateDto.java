package com.zfh.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改商铺信息Dto对象
 */
@Data
public class ShopUpdateDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商铺ID
     */
    private Long id;

    /**
     * 商铺名称
     */
    @Size(max = 50, message = "商铺名称长度不能超过50个字符")
    private String name;

    /**
     * 地址
     */
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
    @Size(max = 11, message = "联系电话长度不能超过11个字符")
    private String contactPhone;

}
