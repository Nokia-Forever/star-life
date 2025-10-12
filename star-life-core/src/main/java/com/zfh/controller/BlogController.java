package com.zfh.controller;

import com.zfh.dto.*;
import com.zfh.result.R;
import com.zfh.service.IBlogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 博客controller
 *
 * @author author
 * @since 2025-09-24
 */

@RestController
@RequestMapping("/client/blog")
@Slf4j
//TODO 有个很重要的事情用户的头像和昵称改变后,博客应该也跟着改变
public class BlogController {
    @Autowired
    private IBlogService blogService;

    /**
     * 获取博客列表
     */
    @GetMapping("/white/blog/page")
    public R listPage(@RequestBody IdPageDto idPageDto) {
        return R.OK(blogService.listPage(idPageDto));
    }

    /**
     * 新增博客
     *
     * @param blogDto
     * @return
     */
    //TODO 这里的审核未完成
    @PostMapping
    public R addBlog(@RequestBody @Valid BlogDto blogDto) {
        log.info("新增博客：{}", blogDto);
        return R.OK(blogService.addBlog(blogDto));
    }


    /**
     * 新增博客评论
     *
     * @param blogCommentDto
     * @return
     */
    @PostMapping("/comment")
    public R addComment(@RequestBody @Valid BlogCommentDto blogCommentDto) {
        log.info("新增评论：{}", blogCommentDto);
        return R.OK(blogService.addComment(blogCommentDto));
    }

    /**
     * 获取博客评论列表
     *
     * @param idPageDto
     * @return
     */
    @GetMapping("/white/comment/page")
    public R listCommentPage(@RequestBody IdPageDto idPageDto) {
        return R.OK(blogService.listCommentPage(idPageDto));
    }

    /**
     * 点赞或取消点赞博客
     *
     * @param blogId
     * @return
     */
    @PutMapping("/like/{blogId}")
    public R like(@PathVariable @NotNull(message = "博客id不能为空") Long blogId) {
        return R.OK(blogService.like(blogId));
    }


    /**
     * 获取博客评论下的评论列表
     *
     * @param
     * @return
     */
    @GetMapping("/white/commentofcomment/page")
    public R listCommentofcommentPage(@RequestBody BlogCommentPageDto blogCommentPageDto) {
        return R.OK(blogService.listCommentofcommentPage(blogCommentPageDto));
    }

    /**
     * 点赞或取消点赞博客评论
     *
     * @param commentId
     * @return
     */
    @PutMapping("/like/comment/{blogId}/{commentId}")
    public R likeComment(@PathVariable @NotNull(message = "博客id不能为空") Long blogId,
                         @PathVariable @NotNull(message = "评论id不能为空") Long commentId) {
        return R.OK(blogService.likeComment(blogId, commentId));
    }

    /**
     * 获取博客详情
     */
    @GetMapping("/white/{id}")
    public R detail(@PathVariable Long id) {
        return R.OK(blogService.getInfoById(id));
    }


    /**
     * 修改博客
     * @param blogDto
     * @return
     */
    @PutMapping
    public R update(@RequestBody @Valid BlogDto blogDto) {
        return R.OK(blogService.updateBlog(blogDto));
    }

    /**
     * 置顶
     * @param blogCommentTopDto
     * @return
     */
    @PutMapping("/top")
    public R top(@RequestBody @Valid BlogCommentTopDto blogCommentTopDto) {
        return R.OK(blogService.topComment(blogCommentTopDto));
    }

    //TODO 作者应该有些特殊动作删除评论.....
    /**
     * 作者删除评论
     * @param id
     * @return
     */
}
