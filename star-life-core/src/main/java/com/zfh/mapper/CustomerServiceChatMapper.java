package com.zfh.mapper;

import com.zfh.entity.CustomerServiceChat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 客服对话记录表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2025-10-12
 */
public interface CustomerServiceChatMapper extends BaseMapper<CustomerServiceChat> {

    /**
     * 根据店铺id和用户id查询会话记录
     * @param shopId
     * @param userId
     * @return
     */
    List<CustomerServiceChat> listByShopIdAndUserId(Long shopId, Long userId);
}
