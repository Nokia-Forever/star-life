package com.zfh.service.impl;

import com.zfh.entity.BlogComments;
import com.zfh.mapper.BlogCommentsMapper;
import com.zfh.service.IBlogCommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 博客评论表 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-09-24
 */
@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

}
