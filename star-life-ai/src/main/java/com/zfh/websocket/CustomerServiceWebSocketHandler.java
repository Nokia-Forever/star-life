package com.zfh.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.constant.CustomerServiceConstant;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.dto.CustomerServiceChatDto;
import com.zfh.entity.AiCustomerServiceChatDto;
import com.zfh.entity.CustomerServiceChat;
import com.zfh.service.IAiCustomerServiceService;
import com.zfh.service.ICustomerServiceChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CustomerServiceWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IAiCustomerServiceService aiCustomerServiceService;
    @Autowired
    private ICustomerServiceChatService customerServiceChatService;
    @Autowired
    private ObjectMapper  objectMapper;
    /*
    * 建立连接
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从请求参数获取 userId，例如 ws://localhost:8080/ws?userId=123
        String userId = (String) session.getAttributes().get("userId");
        String shopId = (String) session.getAttributes().get("shopId");
        String type = (String) session.getAttributes().get("type");
        if(type==null||shopId==null||userId== null){
            session.close();
            return;
        }

        //客服链接就添加空闲客服
        if(Integer.parseInt(type)== CustomerServiceConstant.CUSTOMER_SERVICE){
            CustomerServiceSessionManager.addCustomerServiceSession(shopId,userId,session);
            log.info("添加空闲客服{}成功",userId);
            CustomerServiceSessionManager.sendToCustomerService(userId, "欢迎加入客服");
            //发送提示信息
            return;
        }
        log.info("用户{}连接店铺{}成功",userId,shopId);
        //如果是用户就建立会话
        //1.生成唯一会话id,UUId
        String sessionId = UUID.randomUUID().toString();
        //2.查询数据库会话该用户和商家的会话记录
        List<CustomerServiceChat> customerServiceChats
                =  customerServiceChatService.listByShopIdAndUserId(Long.parseLong(shopId),Long.parseLong(userId));
        //TODO 3.加载会话记录到redis
        //TODO 4.先跟ai客服对接
        //5.添加用户
        CustomerServiceSessionManager.addUserSession(userId,session);
        //6.添加会话入redis,拼接客户key和客服key,首次对话拼接0,代表ai客服(半个小时)
        stringRedisTemplate.opsForValue()
                .set(RedisKeyConstant.USER_SESSION_KEY+sessionId,
                        "0:"+userId,RedisKeyConstant.USER_SESSION_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        //7.发送会话id
        CustomerServiceSessionManager.sendToUser(userId, "欢迎进入对话,会话id:"+sessionId);
    }

    /*
    * 接收消息
     */
    //TODO 这里只做了用户的处理,只做了用户的ai回答
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //1.解析发来的JSON信息
        CustomerServiceChatDto customerServiceChatDto
                = objectMapper.readValue(message.getPayload(), CustomerServiceChatDto.class);
        //获取信息类型
        String type = (String)session.getAttributes().get("type");
        //处理用户消息
        if(Integer.parseInt(type)== CustomerServiceConstant.USER) {
            handleUserMessage(session, customerServiceChatDto);
        }
        //处理客服消息
        else{
            handleCustomerMessage(session, customerServiceChatDto);
        }
    }

    //处理客服消息
    private void handleCustomerMessage(WebSocketSession session,
                                       CustomerServiceChatDto customerServiceChatDto) throws IOException {
        //在session中获取店铺id
        String sessionUser = getSessionUser(session, customerServiceChatDto);
        if(sessionUser == null){
            return;
        }
        log.info("客服发送信息:{}", customerServiceChatDto);
        //TODO 要在redis判断是否有该会话id,放入该信息

        //获取对应的客户id
        String userId = sessionUser.split(":")[1];
        //发信息给用户
        CustomerServiceSessionManager.sendToUser(userId, customerServiceChatDto.getContent());
    }

    //获取会话用户
    private String getSessionUser(WebSocketSession session,
                                  CustomerServiceChatDto customerServiceChatDto) throws IOException {
        String shopId = (String) session.getAttributes().get("shopId");
        //异常处理
        if(customerServiceChatDto ==null
                || customerServiceChatDto.getSessionId()==null|| customerServiceChatDto.getSessionId().isBlank()){
            session.sendMessage(new TextMessage("请发送有效信息"));
            return null;
        }

        //判断现在是否是人工客服接入中(redis判断)
        String s = stringRedisTemplate.opsForValue()
                .get(RedisKeyConstant.USER_SESSION_KEY + customerServiceChatDto.getSessionId());
        if(s == null||s.isBlank()){
            session.sendMessage(new TextMessage("请发送有效信息"));
            return null;
        }
        //重置过期时间
        stringRedisTemplate.expire(RedisKeyConstant.USER_SESSION_KEY + customerServiceChatDto.getSessionId(),
                RedisKeyConstant.USER_SESSION_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        return s;
    }


    //处理用户消息
    private void handleUserMessage(WebSocketSession session,
                                   CustomerServiceChatDto customerServiceChatDto) throws IOException {
        String shopId = (String) session.getAttributes().get("shopId");
        String sessionUser = getSessionUser(session, customerServiceChatDto);
        if(sessionUser == null){
            return;
        }
        log.info("用户发送信息:{}", customerServiceChatDto);
        //ai处理中,则继续处理
        if(sessionUser.startsWith("0:")){
            AiCustomerServiceChatDto customerServiceChat = new AiCustomerServiceChatDto();
            BeanUtils.copyProperties(customerServiceChatDto, customerServiceChat);
            customerServiceChat.setSenderId(Long.parseLong( (String) session.getAttributes().get("userId")));
            customerServiceChat.setShopId(Long.parseLong(shopId));
            //ai处理问题
            String aiAnswer = aiCustomerServiceService.handleQuestion(customerServiceChat);
            session.sendMessage(new TextMessage(aiAnswer));
        }
        //发送客户的信息给客服
        else{
            //获取客服id
            String customerServiceId = sessionUser.split(":")[0];
            CustomerServiceSessionManager.sendToCustomerService(customerServiceId, customerServiceChatDto.getContent());
        }
    }

    /*
    * 关闭连接
     */
    //TODO 未完成
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            CustomerServiceSessionManager.removeUserSession(userId);
        }
    }
}
