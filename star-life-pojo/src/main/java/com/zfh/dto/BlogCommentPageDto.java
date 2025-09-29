package com.zfh.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 博客评论分页
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BlogCommentPageDto extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 博客id
     */
    private Long blogId;

    /**
     * 关联的1级评论id，如果是一级评论，则值为0
     */
    private Long parentId;
}
