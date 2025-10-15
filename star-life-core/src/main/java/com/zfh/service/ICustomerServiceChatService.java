package com.zfh.service;

import com.zfh.entity.CustomerServiceChat;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 客服对话记录表 服务类
 * </p>
 *
 * @author author
 * @since 2025-10-12
 */
public interface ICustomerServiceChatService extends IService<CustomerServiceChat> {

    /**
     * 根据用户id和店铺id获取会话记录
     * @param shopId
     * @param userId
     * @return
     */
    List<CustomerServiceChat> listByShopIdAndUserId(Long shopId, Long userId,Integer currentPage, Integer pageSize);
}
