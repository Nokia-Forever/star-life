package com.zfh.controller;

import com.zfh.dto.AICommentDto;
import com.zfh.result.R;
import com.zfh.service.AICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ai评论controller
 */
@RestController
@RequestMapping("/client/ai-comment")
public class AICommentController {
    @Autowired
    private AICommentService aICommentService;


    /**
     * 生成ai博客
     * @param aICommentDto
     * @return
     */
    @GetMapping("/generate")
    public R generateComment(@RequestBody AICommentDto aICommentDto){
        return R.OK(aICommentService.generateComment(aICommentDto));
    }


}
