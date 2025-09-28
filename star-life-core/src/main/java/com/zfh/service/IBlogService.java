package com.zfh.service;

import com.zfh.dto.BlogCommentDto;
import com.zfh.dto.BlogDto;
import com.zfh.dto.IdPageDto;
import com.zfh.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
