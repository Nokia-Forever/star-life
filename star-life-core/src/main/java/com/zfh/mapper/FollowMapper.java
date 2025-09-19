package com.zfh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zfh.entity.Follow;

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


}
