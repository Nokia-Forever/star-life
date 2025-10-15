package com.zfh.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zfh.entity.CustomerServiceChat;
import com.zfh.mapper.CustomerServiceChatMapper;
import com.zfh.service.ICustomerServiceChatService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 客服对话记录表 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-10-12
 */
@Service
public class CustomerServiceChatServiceImpl extends ServiceImpl<CustomerServiceChatMapper, CustomerServiceChat> implements ICustomerServiceChatService {

    /**
     * 根据店铺id和用户id查询会话记录
     * @param shopId
     * @param userId
     * @return
     */
    @Override
    public List<CustomerServiceChat> listByShopIdAndUserId(Long shopId, Long userId,Integer currentPage ,Integer pageSize) {
        return baseMapper.listByShopIdAndUserId(shopId,userId,new Page<>(currentPage,pageSize));
    }
}
