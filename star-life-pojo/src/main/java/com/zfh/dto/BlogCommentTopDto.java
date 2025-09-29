package com.zfh.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 博客评论置顶dto
 */
@Data
public class BlogCommentTopDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 博客id
     */
    @NotNull
    private Long blogId;

    /**
     * 评论id
     */
    @NotNull
    private Long commentId;

    /**
     * 置顶状态
     */
    @NotNull
    private Boolean top;
}
