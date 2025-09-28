package com.zfh.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zfh.constant.BlogConstant;
import com.zfh.constant.ExceptionConstant;
import com.zfh.dto.BlogCommentDto;
import com.zfh.dto.BlogDto;
import com.zfh.dto.IdPageDto;
import com.zfh.entity.Blog;
import com.zfh.entity.BlogComments;
import com.zfh.entity.User;
import com.zfh.exception.BlogException;
import com.zfh.mapper.BlogMapper;
import com.zfh.service.IBlogCommentsService;
import com.zfh.service.IBlogService;
import com.zfh.utils.CurrentHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 博客表 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-09-24
 */

//TODO 为了简单考虑,添加博客没有确认店铺是否存在,添加评论没有确认父级评论是否存在
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {
    @Autowired
    private IBlogCommentsService blogCommentsService;

    /**
     * 获取博客列表
     * @param idPageDto
     * @return
     */
    @Override
    public List<Blog> listPage(IdPageDto idPageDto) {
       return this.list(new Page<>(idPageDto.getCurrentPage(), idPageDto.getPageSize()));
    }

    /**
     * 新增博客
     * @param blogDto
     * @return
     */
    @Override
    public boolean addBlog(BlogDto blogDto) {
        //获取当前 用户
        User user = CurrentHolder.getCurrentUser();
        Blog blog = new Blog();

        //设置用户信息
        blog.setUserId(user.getId());
        blog.setUserNickName(user.getNickName());
        blog.setUserIcon(user.getIcon());

        Date now = new Date();
        blog.setUpdateTime(now);
        blog.setCreateTime(now);
        BeanUtils.copyProperties(blogDto, blog);
        return this.save(blog);
    }

    /**
     * 新增博客评论
     * @param blogCommentDto
     * @return
     */
    @Override
    public boolean addComment(BlogCommentDto blogCommentDto) {
        //查询博客id是否存在
        Blog blog = this.getById(blogCommentDto.getBlogId());
        if (blog == null) {
            throw  new BlogException(ExceptionConstant.BLOG_NOT_EXIST);
        }
        //获取当前 用户
        User user = CurrentHolder.getCurrentUser();
        BlogComments blogComments = new BlogComments();
        blogComments.setUserId(user.getId());
        blogComments.setUserNickName(user.getNickName());
        blogComments.setUserIcon(user.getIcon());

        Date now = new Date();
        blogComments.setUpdateTime(now);
        blogComments.setCreateTime(now);
        BeanUtils.copyProperties(blogCommentDto, blogComments);
        blogComments.setIsTop(BlogConstant.COMMENT_NOT_TOP);

        return blogCommentsService.save(blogComments);
    }
}
