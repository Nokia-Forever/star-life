package com.zfh.service.impl;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.dto.AiCustomerServiceDto;
import com.zfh.entity.AiCustomerService;
import com.zfh.entity.AiCustomerServiceChatDto;
import com.zfh.mapper.AiCustomerServiceMapper;
import com.zfh.service.IAiCustomerServiceService;
import com.zfh.tools.CustomerServiceTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.zfh.constant.AiCustomerServiceServiceConstant.STATUS_ENABLE;
import static com.zfh.constant.ExceptionConstant.REQUEST_ERROR;
import static com.zfh.constant.PromptFileConstant.GENERATE_REPLY_FILE;

/**
 * <p>
 * 智能客服信息表 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-10-12
 */
@Service
public class AiCustomerServiceServiceImpl extends ServiceImpl<AiCustomerServiceMapper, AiCustomerService> implements IAiCustomerServiceService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomerServiceTools customerServiceTools;



    private ChatClient chatClient;
    public AiCustomerServiceServiceImpl(DashScopeChatModel dashScopeChatModel) {
        ChatOptions options = ChatOptions.builder()
                .maxTokens(500)//最大生成长度
                .temperature(0.7)//模型温度
                .build();
        chatClient  = ChatClient.builder(dashScopeChatModel)
                .defaultOptions(options)
                .defaultSystem(new ClassPathResource(GENERATE_REPLY_FILE))//系统规定
                .build();
    }
    /**
     * 添加AI客服
     * @param aiCustomerServiceDto
     * @return
     */
    @Override
    public Boolean addAiCustomerService(AiCustomerServiceDto aiCustomerServiceDto) {
        AiCustomerService aiCustomerService = new AiCustomerService();
        BeanUtils.copyProperties(aiCustomerServiceDto,aiCustomerService);
        Date  now = new Date();
        aiCustomerService.setCreateTime(now);
        aiCustomerService.setUpdateTime(now);
        aiCustomerService.setStatus(STATUS_ENABLE);
        return this.save(aiCustomerService);
    }

    /**
     * ai生成回答
     */
    @Override
    public String handleQuestion(AiCustomerServiceChatDto aiCustomerServiceChatDto){
        //现根据shopId查询出ai客服信息
        AiCustomerService aiCustomerService
                = this.getOne(new LambdaQueryWrapper<AiCustomerService>().eq(AiCustomerService::getShopId,aiCustomerServiceChatDto.getShopId()));
        if (aiCustomerService==null){
            return REQUEST_ERROR;
        }

        return chatClient
                .prompt("客户发送的信息详情:" +aiCustomerServiceChatDto +"店铺内容信息:"+aiCustomerService.getPrompt())
                .system(shopRoles->shopRoles.param("shop_rules",aiCustomerService.getAdditionalConfig()))
                .user(aiCustomerServiceChatDto.getContent())
                .tools(customerServiceTools)
                .call()
                .content();
    }
}
