package com.zfh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 博客dto
 */
@Data
public class BlogDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 商户id
     */
    private Long shopId;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 100, message = "标题长度在1-100之间")
    private String title;

    /**
     * 探店的文字描述
     */
    @NotBlank(message = "探店文字描述不能为空")
    private String content;

}
