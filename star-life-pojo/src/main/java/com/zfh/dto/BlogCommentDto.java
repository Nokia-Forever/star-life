package com.zfh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 博客评论DTO
 */
@Data
public class BlogCommentDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 博客id
     */
    @NotNull(message = "博客id不能为空")
    private Long blogId;

    /**
     * 关联的1级评论id，如果是一级评论，则值为0
     */
    @NotNull(message = "关联的1级评论id不能为空")
    private Long parentId;

    /**
     * 评论内容
     */
    @NotBlank
    @Size(min = 1, max = 1000,message = "评论内容长度在1-1000之间")
    private String content;

}
