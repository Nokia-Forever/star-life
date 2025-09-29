package com.zfh.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 博客DTO
 */
@Data
public class AICommentDto implements Serializable {
    /**
     * 商铺id
     */
    private Long shopId;

    /**
     * 提示内容
     */
    private String prompt;

    private static final long serialVersionUID = 1L;
}
