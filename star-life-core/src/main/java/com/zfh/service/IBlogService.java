package com.zfh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zfh.dto.*;
import com.zfh.entity.Blog;
import com.zfh.entity.BlogComments;
import com.zfh.vo.BlogVo;

import java.util.List;

/**
 * <p>
 * 博客表 服务类
 * </p>
 *
 * @author author
 * @since 2025-09-24
 */
public interface IBlogService extends IService<Blog> {

    /**
     * 获取博客列表
     * @param idPageDto
     * @return
     */
    List<Blog> listPage(IdPageDto idPageDto);

    /**
     * 新增博客
     * @param blogDto
     * @return
     */
    boolean addBlog(BlogDto blogDto);

    /**
     * 新增博客评论
     * @param blogCommentDto
     * @return
     */
    boolean addComment(BlogCommentDto blogCommentDto);

    /**
     * 获取博客评论列表
     * @param idPageDto
     * @return
     */
    List<BlogComments> listCommentPage(IdPageDto idPageDto);

    /**
     * 点赞或取消
     * @param BlogId
     * @return
     */
    Boolean like(Long BlogId);

    /**
     * 获取博客评论中评论列表
     * @param blogCommentPageDto
     * @return
     */
    List<BlogComments> listCommentofcommentPage(BlogCommentPageDto blogCommentPageDto);

    /**
     * 点赞或取消(评论)
     * @param blogId
     * @param commentId
     * @return
     */
    Boolean likeComment(Long blogId, Long commentId);

    /**
     * 获取博客详情
     * @param id
     * @return
     */
    BlogVo getInfoById(Long id);

    /**
     * 修改博客
      *
     * @param blogDto
     * @return
     */
    Boolean updateBlog(BlogDto blogDto);

    /**
     * 置顶
     * @param blogCommentTopDto
     * @return
     */
    Boolean topComment(BlogCommentTopDto blogCommentTopDto);
}
