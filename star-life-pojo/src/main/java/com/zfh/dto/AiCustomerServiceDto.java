package com.zfh.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class AiCustomerServiceDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 店铺ID（关联shop.id）
     */
    private Long shopId;

    /**
     * 客服prompt信息（店铺详情,提示）
     */
    @Size(max = 2000, message = "prompt长度不能超过2000")
    private String prompt;

    /**
     * 系统配置信息
     */
    @Size(max = 2000, message = "系统配置信息长度不能超过2000")
    private String additionalConfig;
}
