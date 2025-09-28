package com.zfh.service.impl;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.dto.AICommentDto;
import com.zfh.entity.User;
import com.zfh.service.AICommentService;
import com.zfh.service.IShopService;
import com.zfh.utils.CurrentHolder;
import com.zfh.vo.ShopVo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ai评论服务实现类
 */
@Service
public class AICommentServiceImpl implements AICommentService {
    @Autowired
    private IShopService shopService;

    private final ChatMemory chatMemory;//会话内存
    @Autowired
    private ObjectMapper objectMapper;


    private ChatClient chatClient;
    public AICommentServiceImpl(DashScopeChatModel dashScopeChatModel) {
        //设置会话记忆
        chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
        ChatOptions options = ChatOptions.builder()
                .maxTokens(500)//最大生成长度
                .temperature(0.7)//模型温度
                .build();
        chatClient  = ChatClient.builder(dashScopeChatModel)
                .defaultOptions(options)
                .defaultSystem("1.你是星选生活里面的一个点评生成助手,用户会提供一些提示给你,你要根据他的提示生成一份好的点评文章给他,供他参考" +
                      "2.用户可能会根据某个店铺来写点评,所有有可能会传入店铺的详细信息给你,也可能没有,那这时候你就不需要管这个")//系统信息
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
    /**
     * 生成ai博客
     *
     * @param aiCommentDto
     * @return
     */
    @Override
    public String generateComment(AICommentDto aiCommentDto) {
        //获取当前用户
        User user = CurrentHolder.getCurrentUser();
        String shopInfo = "";
        if(aiCommentDto.getShopId()!=null) {
            //获取店铺详细信息
            ShopVo shopVo = shopService.getInfoById(aiCommentDto.getShopId());
            try {
                if(shopVo!=null) {
                    shopInfo=objectMapper.writeValueAsString(shopVo);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("解析失败");
            }
        }

        String content = chatClient
                .prompt(shopInfo.isBlank() ? "按用户要求生成" : "根据店铺信息生成" +shopInfo)
                .messages(chatMemory.get(user.getId().toString()))//获取当前用户的会话记忆
                .user(aiCommentDto.getPrompt())
                .call()
                .content();

        //保存当前用户的会话记忆
       if(content!=null){
           chatMemory.add(user.getId().toString(),new UserMessage( content));
       }
        return content;
    }
}
