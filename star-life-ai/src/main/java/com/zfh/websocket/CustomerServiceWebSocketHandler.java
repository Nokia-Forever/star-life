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
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CustomerServiceWebSocketHandler extends TextWebSocketHandler  {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IAiCustomerServiceService aiCustomerServiceService;
    @Autowired
    private ICustomerServiceChatService customerServiceChatService;
    @Autowired
    private ObjectMapper  objectMapper;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    /*
    * 建立连接
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从请求参数获取 userId
        Long userId = (Long) session.getAttributes().get("userId");
        Integer type =(Integer) session.getAttributes().get("type");
        if(type==null||userId== null){
            session.close();
            return;
        }

        //客服上线
        if(type== CustomerServiceConstant.CUSTOMER_SERVICE){
            customerServiceOnline(userId,session);
            return;
        }
        //用户上线
        userOnline(userId,session);
    }

    /*
    客服上线
     */
    private void customerServiceOnline( Long userId, WebSocketSession session) throws IOException {
        CustomerServiceSessionManager.addCustomerServiceSession(userId,session);
        log.info("添加空闲客服{}成功",userId);
        //发送提示信息
        CustomerServiceSessionManager.sendToCustomerService(userId, "欢迎加入客服");
    }
    /*
    用户上线
     */
    private void userOnline(Long userId, WebSocketSession session) throws IOException {
        CustomerServiceSessionManager.addUserSession(userId,session);
        log.info("添加用户{}成功",userId);
        CustomerServiceSessionManager.sendToUser(userId, "欢迎进行对话");
    }

    /*
    * 接收消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //1.解析发来的JSON信息
        CustomerServiceChatDto customerServiceChatDto
                = objectMapper.readValue(message.getPayload(), CustomerServiceChatDto.class);
        //获取信息类型
        Integer type = (Integer)session.getAttributes().get("type");
        //处理用户消息
        if(type== CustomerServiceConstant.USER) {
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

        //在session中获取会话信息
        Result result = getSessionInfoResult(session, customerServiceChatDto);
        if (result == null) return;

        //发信息给用户
        CustomerServiceSessionManager.sendToUser(result.userId(), customerServiceChatDto.getContent());

        //异步保存会话日志
        CustomerServiceChat customerServiceChat = new CustomerServiceChat();
        BeanUtils.copyProperties(customerServiceChatDto,customerServiceChat);
        customerServiceChat.setShopId(result.shopId());
        customerServiceChat.setSenderId(result.customerId());
        customerServiceChat.setReceiverId(result.userId());
        customerServiceChat.setCreateTime(new Date());
        saveSessionLog(customerServiceChat);
    }

    //处理用户消息
    private void handleUserMessage(WebSocketSession session,
                                   CustomerServiceChatDto customerServiceChatDto) throws IOException {
        //在session中获取会话信息
        Result result = getSessionInfoResult(session, customerServiceChatDto);
        if (result == null) return;

        //ai处理中,则继续处理
        if(result.customerId==CustomerServiceConstant.AI_CUSTOMER_SERVICE){
            AiCustomerServiceChatDto customerServiceChat = new AiCustomerServiceChatDto();
            BeanUtils.copyProperties(customerServiceChatDto, customerServiceChat);
            customerServiceChat.setSenderId( result.userId);
            customerServiceChat.setShopId(result.shopId);

            //存放用户对话内容
            stringRedisTemplate.opsForList()
                    .rightPush(RedisKeyConstant.USER_SESSION_LOG_KEY+customerServiceChatDto.getSessionId(),
                            "user:"+customerServiceChatDto.getContent());
            //ai处理问题
            String aiAnswer = aiCustomerServiceService.handleQuestion(customerServiceChat);
            session.sendMessage(new TextMessage(aiAnswer));
        }
        //发送客户的信息给客服
        else{
            //获取客服id
            Long customerServiceId = result.customerId;
            CustomerServiceSessionManager.sendToCustomerService(customerServiceId, customerServiceChatDto.getContent());

            //异步保存会话日志
            CustomerServiceChat customerServiceChat = new CustomerServiceChat();
            BeanUtils.copyProperties(customerServiceChatDto, customerServiceChat);
            customerServiceChat.setShopId(result.shopId);
            customerServiceChat.setSenderId(result.userId);
            customerServiceChat.setReceiverId(customerServiceId);
            customerServiceChat.setCreateTime(new Date());
            saveSessionLog(customerServiceChat);
        }
    }

    //切割会话信息
    private @Nullable Result getSessionInfoResult(WebSocketSession session, CustomerServiceChatDto customerServiceChatDto) throws IOException {
        String sessionInfo = getSessionInfo(session, customerServiceChatDto);
        if(sessionInfo == null){
            return null;
        }
        log.info("发送信息:{}", customerServiceChatDto);

        //获取对应的客户id
        String[]  split = sessionInfo.split(":");

        //获取用户id
        Long userId = Long.parseLong(split[2]);
        //获取客服id
        Long customerId = Long.parseLong(split[1]);
        //店铺id
        Long shopId = Long.parseLong(split[0]);
        return new Result(userId, customerId, shopId);
    }

    /*
    处理用户消息
     */
    private record Result(Long userId, Long customerId, Long shopId) {
    }


    //获取会话信息
    private String getSessionInfo(WebSocketSession session,
                                  CustomerServiceChatDto customerServiceChatDto) throws IOException {
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


    //异步保存会话日志
    @Async("threadPoolExecutor")
    public void saveSessionLog(CustomerServiceChat customerServiceChat){
      threadPoolExecutor.execute(() -> {
          customerServiceChatService.save(customerServiceChat);
      });
    }

    /*
    * 关闭连接
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
       //获取关闭人的类型
        Integer type = (Integer) session.getAttributes().get("type");
        Long userId = (Long)session.getAttributes().get("userId");
        //用户
        if(type == CustomerServiceConstant.USER){
            //获取用户所有会话信息
            Set<String> members = stringRedisTemplate.opsForSet().members(RedisKeyConstant.USER_SESSION_ID_LIST_KEY + userId);
            //删除所有会话信息
            if (members != null&& !members.isEmpty()) {
                for (String sessionId : members) {
                    stringRedisTemplate.delete(RedisKeyConstant.USER_SESSION_KEY + sessionId);
                }
            }
            stringRedisTemplate.delete(RedisKeyConstant.USER_SESSION_ID_LIST_KEY+ userId);
            CustomerServiceSessionManager.removeUserSession(userId);
            session.close();
        }
        //客服
        else{
            //获取用户所有会话信息
            Set<String> members = stringRedisTemplate.opsForSet().members(RedisKeyConstant.USER_SESSION_ID_LIST_KEY + userId);
            //删除所有会话信息
            if (members != null&& !members.isEmpty()) {
                for (String sessionId : members) {
                    //获取会话信息
                    String sessionInfo = stringRedisTemplate.opsForValue().get(RedisKeyConstant.USER_SESSION_KEY + sessionId);
                    if(sessionInfo != null){
                        String[] split = sessionInfo.split(":");
                        Long shopId = Long.parseLong(split[0]);
                        Long customerId = Long.parseLong(split[1]);
                        //删除客服
                        CustomerServiceSessionManager.removeCustomerService(shopId, customerId);
                        //redis删除会话
                        stringRedisTemplate.delete(RedisKeyConstant.USER_SESSION_KEY + sessionId);
                    }
                }
            }
            stringRedisTemplate.delete(RedisKeyConstant.USER_SESSION_ID_LIST_KEY+ userId);
            CustomerServiceSessionManager.removeCustomerServiceSession(userId);
            session.close();
        }
    }

}
