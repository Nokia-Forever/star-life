package com.zfh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zfh.dto.AiCustomerServiceDto;
import com.zfh.entity.AiCustomerService;
import com.zfh.entity.AiCustomerServiceChatDto;

/**
 * <p>
 * 智能客服信息表 服务类
 * </p>
 *
 * @author author
 * @since 2025-10-12
 */
public interface IAiCustomerServiceService extends IService<AiCustomerService> {

    /**
     * 添加AI客服配置信息
     * @param aiCustomerServiceDto
     * @return
     */
    Boolean addAiCustomerService(AiCustomerServiceDto aiCustomerServiceDto);

    /**
     * ai生成回答
     */
    String handleQuestion(AiCustomerServiceChatDto aiCustomerServiceChatDto);
}
