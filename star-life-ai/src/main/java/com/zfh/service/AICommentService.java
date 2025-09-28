package com.zfh.service;

import com.zfh.dto.AICommentDto;

/**
 * ai博客服务接口
 */
public interface AICommentService {
    /**
     * 生成ai博客
     *
     * @param aiCommentDto
     * @return
     */
    String generateComment(AICommentDto aiCommentDto);
}
