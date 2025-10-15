package com.zfh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zfh.dto.AiCustomerServiceDto;
import com.zfh.entity.AiCustomerService;
import com.zfh.entity.AiCustomerServiceChatDto;
import com.zfh.vo.ChatSessionBuildVo;

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


    /**
     * 人工客服上线
     * @param shopId
     * @return
     */
    Boolean huManOnline(Long shopId);

    /**
     * 创建会话
     * @param shopId
     * @return
     */
    ChatSessionBuildVo buildChatSession(Long shopId);

    /**
     * 关闭会话
     * @param sessionId
     * @return
     */
    Boolean closeChatSession(String sessionId);
}
