package com.zfh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zfh.entity.Follow;

import java.util.List;

/**
 * <p>
 * 用户关注表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2025-09-19
 */
public interface FollowMapper extends BaseMapper<Follow> {

    /**
     * 查询用户粉丝id
     * @param page
     * @param userId
     * @return
     */
    IPage<Long> selectFanIdsByUserId(IPage<Long> page, Long userId);


    /**
     * 查询用户所有关注id
     * @param id
     * @return
     */
    List<Long> selectAllFollowIdById(Long id);


    /**
     * 查询用户关注id,分页查询
     * @param objectPage
     * @param id
     * @return
     */
    IPage<Long> selectFollowIdsByUserId(Page<Long> objectPage, Long id);
}
