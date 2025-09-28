package com.zfh.dto;

import lombok.Data;

/**
 * 博客DTO
 */
@Data
public class AICommentDto {
    /**
     * 商铺id
     */
    private Long shopId;

    /**
     * 提示内容
     */
    private String prompt;
}
