package com.zfh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 博客评论表
 * </p>
 *
 * @author author
 * @since 2025-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("blog_comments")
public class BlogComments implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 博客id
     */
    private Long blogId;

    /**
     * 关联的1级评论id，如果是一级评论，则值为0
     */
    private Long parentId;

    /**
     * 用户头像url
     */
    private String userIcon;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 评论人姓名
     */
    private String userNickName;


}
