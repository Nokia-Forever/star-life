package com.zfh.service.impl;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.constant.AiModelExceptionConstant;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.dto.AiCustomerServiceDto;
import com.zfh.entity.AiCustomerService;
import com.zfh.entity.AiCustomerServiceChatDto;
import com.zfh.entity.CustomerServiceChat;
import com.zfh.exception.AiCustomerServiceException;
import com.zfh.mapper.AiCustomerServiceMapper;
import com.zfh.service.IAiCustomerServiceService;
import com.zfh.service.ICustomerServiceChatService;
import com.zfh.tools.CustomerServiceTools;
import com.zfh.utils.CurrentHolder;
import com.zfh.vo.ChatSessionBuildVo;
import com.zfh.websocket.CustomerServiceSessionManager;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.zfh.constant.AiCustomerServiceServiceConstant.STATUS_ENABLE;
import static com.zfh.constant.AiModelExceptionConstant.REQUEST_ERROR;
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
    @Autowired
    private ICustomerServiceChatService customerServiceChatService;



    private ChatClient chatClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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


    /**
     * 人工客服上线
     * @param shopId
     * @return
     */
    @Override
    public Boolean huManOnline(Long shopId) {
        //获取当前用户id
        Long merchantId = CurrentHolder.getCurrentUser().getId();
        //查询客服是否建立webSocket连接
        if (!CustomerServiceSessionManager.isCustomerServiceConnected(merchantId)) {
            throw new AiCustomerServiceException(AiModelExceptionConstant.CUSTOMER_SERVICE_NOT_CONNECT);
        }
        CustomerServiceSessionManager.addCustomerServiceSession(shopId,merchantId);
        return true;
    }

    /**
     * 构建会话
     * @param shopId
     * @return
     */
    @Override
    public ChatSessionBuildVo buildChatSession(Long shopId) {
        //获取当前用户id
        Long userId = CurrentHolder.getCurrentUser().getId();
        //查询当前用户是否建立webSocket连接
        if (!CustomerServiceSessionManager.isUserConnected(userId)) {
            throw new AiCustomerServiceException(AiModelExceptionConstant.USER_NOT_CONNECT);
        }
        //生成随机会话id,redis保存会话状态和会话id集合,"店铺id:客服id:用户id",0代表ai客服
        String sessionId = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.USER_SESSION_KEY+sessionId,shopId+":0:"+userId,
                RedisKeyConstant.USER_SESSION_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        stringRedisTemplate.opsForSet().add(RedisKeyConstant.USER_SESSION_ID_LIST_KEY+userId,sessionId);

        //获取部分历史对话记录
        //TODO 硬编码,首次默认获取20条
        List<CustomerServiceChat> customerServiceChats = customerServiceChatService.listByShopIdAndUserId(shopId, userId, 0, 20);
        ChatSessionBuildVo chatSessionBuildVo = new ChatSessionBuildVo();
        chatSessionBuildVo.setSessionId(sessionId);
        chatSessionBuildVo.setCustomerServiceChats(customerServiceChats);
        return chatSessionBuildVo;
    }

    /**
     * 关闭会话
     * @param sessionId
     * @return
     */
    @Override
    public Boolean closeChatSession(String sessionId) {
        //先获取会话信息
        String sessionInfo = stringRedisTemplate.opsForValue().get(RedisKeyConstant.USER_SESSION_KEY+sessionId);
        if (sessionInfo==null){
            throw new AiCustomerServiceException(AiModelExceptionConstant.SESSION_NOT_EXIST);
        }
        //切割会话 信息
        String[] sessionInfoArray = sessionInfo.split(":");
        Long shopId = Long.parseLong(sessionInfoArray[0]);
        Long customerId = Long.parseLong(sessionInfoArray[1]);
        Long userId = Long.parseLong(sessionInfoArray[2]);

        //redis删除对应的会话id
        stringRedisTemplate.opsForSet().remove(RedisKeyConstant.USER_SESSION_ID_LIST_KEY+userId,sessionId);
        stringRedisTemplate.opsForSet().remove(RedisKeyConstant.USER_SESSION_ID_LIST_KEY+customerId,sessionId);
        stringRedisTemplate.delete(RedisKeyConstant.USER_SESSION_KEY+sessionId);

        //客服重回空闲
        CustomerServiceSessionManager.addCustomerServiceSession(shopId,customerId);
        return true;
    }
}
