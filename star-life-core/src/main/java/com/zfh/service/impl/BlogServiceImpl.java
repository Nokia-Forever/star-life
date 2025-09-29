package com.zfh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zfh.constant.BlogConstant;
import com.zfh.constant.ExceptionConstant;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.dto.*;
import com.zfh.entity.*;
import com.zfh.exception.BlogException;
import com.zfh.mapper.BlogMapper;
import com.zfh.service.IBlogCommentLikeService;
import com.zfh.service.IBlogCommentsService;
import com.zfh.service.IBlogLikeService;
import com.zfh.service.IBlogService;
import com.zfh.utils.CurrentHolder;
import com.zfh.vo.BlogVo;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * 博客表 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-09-24
 */

//TODO 为了简单考虑,删除未完成,包括评论和博客的
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {
    @Autowired
    private IBlogCommentsService blogCommentsService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private IBlogLikeService blogLikeService;

    @Autowired
    private IBlogCommentLikeService blogCommentLikeService;

    //lua脚本
    private static final DefaultRedisScript<Long> LIKE_SCRIPT;

    static {
        LIKE_SCRIPT = new DefaultRedisScript<Long>();
        LIKE_SCRIPT.setLocation(new ClassPathResource("lua/BlogLike.lua"));
        LIKE_SCRIPT.setResultType(Long.class);
    }

    /**
     * 获取博客列表
     *
     * @param idPageDto
     * @return
     */
    @Override
    public List<Blog> listPage(IdPageDto idPageDto) {
        return this.list(new Page<>(idPageDto.getCurrentPage(), idPageDto.getPageSize()),
                new LambdaQueryWrapper<Blog>().orderByAsc(Blog::getLikeCount).orderByAsc(Blog::getCommentCount));
    }

    /**
     * 新增博客
     *
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
     *
     * @param blogCommentDto
     * @return
     */
    @Transactional
    @Override
    public boolean addComment(BlogCommentDto blogCommentDto) {
        //查询博客id是否存在
        Blog blog = this.getById(blogCommentDto.getBlogId());
        if (blog == null) {
            throw new BlogException(ExceptionConstant.BLOG_NOT_EXIST);
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

        //博客评论数加1
        this.update(new LambdaUpdateWrapper<Blog>().set(Blog::getCommentCount, blog.getCommentCount() + 1)
                .eq(Blog::getId, blogCommentDto.getBlogId()));

        //保存
        return blogCommentsService.save(blogComments);
    }

    /**
     * 获取博客评论列表
     *
     * @param idPageDto
     * @return
     */
    @Override
    public List<BlogComments> listCommentPage(IdPageDto idPageDto) {
        if (idPageDto.getId() == null) {
            throw new BlogException(ExceptionConstant.BLOG_NOT_EXIST);
        }
        return blogCommentsService.list(new Page<>(idPageDto.getCurrentPage(), idPageDto.getPageSize())
                , new LambdaQueryWrapper<BlogComments>()
                        .eq(BlogComments::getBlogId, idPageDto.getId())
                        .eq(BlogComments::getParentId, BlogConstant.COMMENT_LEVEL_ONE)
                        .orderByAsc(BlogComments::getIsTop)
                        .orderByAsc(BlogComments::getLikeCount)
                        .orderByAsc(BlogComments::getCommentCount));
    }

    /**
     * 点赞或取消
     *
     * @param blogId
     * @return
     */
    @Override
    public Boolean like(Long blogId) {
        //查看博客是否存在
        Blog blog = this.getById(blogId);
        if (blog == null) {
            throw new BlogException(ExceptionConstant.BLOG_NOT_EXIST);
        }
        //获取当前用户
        User user = CurrentHolder.getCurrentUser();
        String key = RedisKeyConstant.BLOG_LIKE_KEY + blogId;
        Long res = stringRedisTemplate.execute(LIKE_SCRIPT, List.of(key), user.getId().toString());
        //获取代理对象
        BlogServiceImpl blogService = (BlogServiceImpl) AopContext.currentProxy();
        blogService.likeBlog(res, blogId, user.getId());
        return true;
    }

    /**
     * 获取博客评论中评论列表
     *
     * @param blogCommentPageDto
     * @return
     */
    @Override
    public List<BlogComments> listCommentofcommentPage(BlogCommentPageDto blogCommentPageDto) {
        if (blogCommentPageDto.getBlogId() == null || blogCommentPageDto.getParentId() == null) {
            throw new BlogException(ExceptionConstant.BLOG_NOT_EXIST);
        }
        return blogCommentsService.list(new Page<>(blogCommentPageDto.getCurrentPage(), blogCommentPageDto.getPageSize())
                , new LambdaQueryWrapper<BlogComments>()
                        .eq(BlogComments::getBlogId, blogCommentPageDto.getBlogId())
                        .eq(BlogComments::getParentId, blogCommentPageDto.getParentId())
                        .orderByAsc(BlogComments::getLikeCount)
                        .orderByAsc(BlogComments::getCommentCount)
        );
    }

    /**
     * 点赞或取消(评论)
     *
     * @param blogId
     * @param commentId
     * @return
     */
    @Override
    public Boolean likeComment(Long blogId, Long commentId) {
        //查看博客和评论是否存在
        Blog blog = this.getById(blogId);
        if (blog == null) {
            throw new BlogException(ExceptionConstant.BLOG_NOT_EXIST);
        }
        BlogComments blogComments = blogCommentsService.getById(commentId);
        if (blogComments == null) {
            throw new BlogException(ExceptionConstant.BLOG_COMMENT_NOT_EXIST);
        }
        //获取当前用户
        User user = CurrentHolder.getCurrentUser();
        String key = RedisKeyConstant.BLOGCOMMENT_LIKE_KEY + blogId + ":" + commentId;
        Long res = stringRedisTemplate.execute(LIKE_SCRIPT, List.of(key), user.getId().toString());
        //获取代理对象
        BlogServiceImpl blogService = (BlogServiceImpl) AopContext.currentProxy();
        blogService.likeBlogComment(res, blogId, user.getId(), commentId);
        return true;
    }

    /**
     * 获取博客详情
     *
     * @param id
     * @return
     */
    @Override
    public BlogVo getInfoById(Long id) {
        //先获取博客基本信息
        Blog blog = this.getById(id);

        BlogVo blogVo = new BlogVo();
        BeanUtils.copyProperties(blog, blogVo);

        //再获取评论信息,只获取一级评论,且第一次获取只获取20条
        blogVo.setComments(blogCommentsService.list(new Page<>(0, 20),
                new LambdaQueryWrapper<BlogComments>()
                        .eq(BlogComments::getBlogId, id)
                        .eq(BlogComments::getParentId, BlogConstant.COMMENT_LEVEL_ONE)
                        .orderByAsc(BlogComments::getIsTop)
                        .orderByAsc(BlogComments::getLikeCount)
                        .orderByAsc(BlogComments::getCommentCount)));
        return blogVo;
    }

    /**
     * 修改博客
     *
     * @param blogDto
     * @return
     */
    @Override
    public Boolean updateBlog(BlogDto blogDto) {
        if (blogDto.getId() == null) {
            throw new BlogException(ExceptionConstant.BLOG_NOT_EXIST);
        }
        //先获取博客
        Blog blog = this.getById(blogDto.getId());
        if (blog == null) {
            throw new BlogException(ExceptionConstant.BLOG_NOT_EXIST);
        }
        //验证用户
        if (!blog.getUserId().equals(CurrentHolder.getCurrentUser().getId())) {
            throw new BlogException(ExceptionConstant.REQUEST_ERROR);
        }
        return this.update(new LambdaUpdateWrapper<Blog>()
                .set(Blog::getShopId, blogDto.getShopId())
                .set(Blog::getTitle, blogDto.getTitle())
                .set(Blog::getContent, blogDto.getContent())
                .eq(Blog::getId, blogDto.getId())
        );
    }

    /**
     * 置顶或取消置顶评论
     *
     * @param blogCommentTopDto
     * @return
     */
    @Override
    public Boolean topComment(BlogCommentTopDto blogCommentTopDto) {
        if (blogCommentTopDto.getBlogId() == null) {
            throw new BlogException(ExceptionConstant.BLOG_NOT_EXIST);
        }
        //先获取博客
        Blog blog = this.getById(blogCommentTopDto.getBlogId());
        if (blog == null) {
            throw new BlogException(ExceptionConstant.BLOG_NOT_EXIST);
        }
        //验证用户
        if (!blog.getUserId().equals(CurrentHolder.getCurrentUser().getId())) {
            throw new BlogException(ExceptionConstant.REQUEST_ERROR);
        }

        Integer isTop = blogCommentTopDto.getTop() ? BlogConstant.COMMENT_TOP : BlogConstant.COMMENT_NOT_TOP;
        return blogCommentsService.update(new LambdaUpdateWrapper<BlogComments>()
                .set(BlogComments::getIsTop, isTop)
                .eq(BlogComments::getId, blogCommentTopDto.getCommentId())
        );
    }

    //点赞或取消(评论)
    @Transactional
    @Async("threadPoolExecutor")
    public void likeBlogComment(Long res, Long blogId, Long id, Long commentId) {
        threadPoolExecutor.execute(() -> {
            //关注
            if (res == 1) {
                BlogCommentLike blogCommentLike = new BlogCommentLike();
                blogCommentLike.setUserId(id);
                blogCommentLike.setBlogId(blogId);
                blogCommentLike.setCommentId(commentId);


                blogCommentLikeService.save(blogCommentLike);
                //博客评论点赞数量+1
                blogCommentsService.update(new LambdaUpdateWrapper<BlogComments>()
                        .setSql("like_count = like_count + 1")
                        .eq(BlogComments::getId, commentId));

            }
            //取消关注
            else if (res == 0) {
                blogCommentLikeService.remove(new LambdaQueryWrapper<BlogCommentLike>()
                        .eq(BlogCommentLike::getUserId, id)
                        .eq(BlogCommentLike::getBlogId, blogId)
                        .eq(BlogCommentLike::getCommentId, commentId));

                //博客评论点赞数量-1
                blogCommentsService.update(new LambdaUpdateWrapper<BlogComments>()
                        .setSql("like_count = like_count - 1")
                        .eq(BlogComments::getId, commentId));
            }
        });
    }

    /**
     * 异步任务点赞博客
     *
     * @param res
     * @param BlogId
     * @param userId
     */
    @Async("threadPoolExecutor")
    @Transactional
    public void likeBlog(Long res, Long BlogId, Long userId) {

        threadPoolExecutor.execute(() -> {
            //关注
            if (res == 1) {
                BlogLike blogLike = new BlogLike();
                blogLike.setUserId(userId);
                blogLike.setBlogId(BlogId);
                blogLikeService.save(blogLike);

                //博客点赞数量+1
                update(new LambdaUpdateWrapper<Blog>()
                        .setSql("like_count = like_count + 1")
                        .eq(Blog::getId, BlogId));

            }
            //取消关注
            else if (res == 0) {
                blogLikeService.remove(new LambdaQueryWrapper<BlogLike>()
                        .eq(BlogLike::getUserId, userId)
                        .eq(BlogLike::getBlogId, BlogId));
                update(new LambdaUpdateWrapper<Blog>()
                        .setSql("like_count = like_count - 1")
                        .eq(Blog::getId, BlogId));
            }
        });
    }
}
