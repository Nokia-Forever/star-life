package com.zfh.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 博客点赞表
 * </p>
 *
 * @author author
 * @since 2025-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("blog_like")
public class BlogLike implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 博客id
     */
    private Long blogId;

    /**
     * 用户id
     */
    private Long userId;
}
