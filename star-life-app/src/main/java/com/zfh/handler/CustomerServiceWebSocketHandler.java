package com.zfh.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.config.CustomerServiceSessionManager;
import com.zfh.constant.CustomerServiceConstant;
import com.zfh.dto.CustomerServiceChatDto;
import com.zfh.service.IAiCustomerServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

@Component
@Slf4j
public class CustomerServiceWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IAiCustomerServiceService aiCustomerServiceService;
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
            CustomerServiceSessionManager.sendToUser(userId, "欢迎加入客服");
            //发送提示信息

            return;
        }
        log.info("用户{}连接店铺{}成功",userId,shopId);
        //如果是用户就建立会话
        //1.生成唯一会话id,UUId
        String sessionId = UUID.randomUUID().toString();
        //TODO 2.查询数据库会话该用户和商家的会话记录
        //TODO 3.加载会话记录到redis
        //TODO 4.先跟ai客服对接
        //5.添加用户
        CustomerServiceSessionManager.addCustomerServiceSession(shopId,userId,session);
        CustomerServiceSessionManager.sendToCustomerService(userId, "欢迎进入对话");
        //TODO 发送会话id
    }

    /*
    * 接收消息
     */
    //TODO 这里只做了用户的处理,只做了用户的ai回答
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //1.解析发来的JSON信息
        CustomerServiceChatDto customerServiceChatDto = objectMapper.readValue(message.getPayload(), CustomerServiceChatDto.class);
        //在session中获取店铺id
        String shopId = (String) session.getAttributes().get("shopId");
        if(customerServiceChatDto==null){
            session.sendMessage(new TextMessage("请发送有效信息"));
            return;
        }
        log.info("用户发送信息:{}",customerServiceChatDto);
        //TODO 要在redis判断是否有该会话id,放入该信息

        //3.获取ai客服的回答
        String aiAnswer = aiCustomerServiceService.generateAnswer(Long.parseLong(shopId),customerServiceChatDto.getContent());
        session.sendMessage(new TextMessage(aiAnswer));
    }

    /*
    * 关闭连接
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            CustomerServiceSessionManager.removeUserSession(userId);
        }
    }
}
