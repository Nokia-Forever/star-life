package com.zfh.controller;

import com.zfh.dto.BlogCommentDto;
import com.zfh.dto.BlogDto;
import com.zfh.dto.IdPageDto;
import com.zfh.result.R;
import com.zfh.service.IBlogService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * 博客controller
 *
 *
 * @author author
 * @since 2025-09-24
 */

@RestController
@RequestMapping("/client/blog")
@Slf4j
public class BlogController {
    @Autowired
    private IBlogService blogService;

    /**
     * 获取博客列表
     */
    @GetMapping("/white/page")
    public R listPage(@RequestBody IdPageDto idPageDto) {
        return R.OK(blogService.listPage(idPageDto));
    }

    /**
     * 新增博客
     * @param blogDto
     * @return
     */
    //TODO 这里的审核未完成
    @PostMapping
    public R addBlog (@RequestBody @Valid BlogDto blogDto) {
        log.info("新增博客：{}", blogDto);
        return R.OK(blogService.addBlog(blogDto));
    }


    /**
     * 新增博客评论
     * @param blogCommentDto
     * @return
     */
    @PostMapping("/comment")
    public R addComment(@RequestBody @Valid BlogCommentDto blogCommentDto) {
        log.info("新增评论：{}", blogCommentDto);
        return R.OK(blogService.addComment(blogCommentDto));
    }

//
//    /**
//     * 获取博客详情
//     */
//    @GetMapping("/{id}")
//    public Blog detail(@PathVariable Long id) {
//        return blogService.getById(id);
//    }
//

//
//    /**
//     * 点赞博客
//     */
//    @PostMapping("/{id}/like")
//    public String like(@PathVariable Long id) {
//        boolean success = blogService.likeBlog(id);
//        return success ? "点赞成功" : "点赞失败";
//    }
    }
