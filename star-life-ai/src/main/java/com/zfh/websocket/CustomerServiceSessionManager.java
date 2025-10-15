package com.zfh.websocket;


import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CustomerServiceSessionManager  客服管理器
 */
public class CustomerServiceSessionManager {
    // 客户会话Map
    private static final ConcurrentHashMap<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    //商家会话 Map
    private static final ConcurrentHashMap<Long, WebSocketSession> customerServiceSessions = new ConcurrentHashMap<>();
    //商家客服空闲map Map<商家id, 商家客服id列表>
    private static final ConcurrentHashMap<Long, List<Long>> merchantServiceMap = new ConcurrentHashMap<>();

    //客户上线
    public static void addUserSession(Long userId, WebSocketSession session) {
        userSessions.put(userId, session);
    }

    //客服上线
    public static void addCustomerServiceSession(Long merchantId, WebSocketSession session) {
        customerServiceSessions.put(merchantId, session);
    }

    //添加客服到店铺
    public static void addCustomerServiceSession(Long shopId,Long merchantId) {
        if(merchantServiceMap.containsKey(shopId)){
            merchantServiceMap.get(shopId).add(merchantId);
        }else {
            List<Long> serviceList = new LinkedList<>();
            serviceList.add(merchantId);
            merchantServiceMap.put(shopId, serviceList);
        }
    }

    //获取一个客服id
    public static Long getOneCustomerService(Long shopId) {
        //没有客服返回 null
        if(merchantServiceMap.get(shopId) == null || merchantServiceMap.get(shopId).isEmpty()){
            return -1L;
        }
        Long merchantId = merchantServiceMap.get(shopId).getFirst();
        merchantServiceMap.get(shopId).removeFirst();
        return merchantId;
    }

    // 移除用户连接
    public static void removeUserSession(Long userId) {
        userSessions.remove(userId);
    }

    //移除客服连接
    //TODo 要根据店铺id删除
    public static void removeCustomerServiceSession(Long merchantId) {
        customerServiceSessions.remove(merchantId);
    }

    //移除客服
    public static void removeCustomerService(Long shopId,Long merchantId) {
        merchantServiceMap.get(shopId).remove(merchantId);
    }

    //发信息给客服
    public static void sendToCustomerService(Long userId, String... messages) throws IOException {
        WebSocketSession session = customerServiceSessions.get(userId);
        sendMessageBath(messages, session);
    }

    //批量发送信息
    private static void sendMessageBath(String[] messages, WebSocketSession session) {
        if (session != null && session.isOpen()) {
           for (String message : messages){
               try {
                   session.sendMessage(new TextMessage(message));
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        }
    }

    //p

    //发信息给用户
    public static void sendToUser(Long merchantId, String... messages) throws IOException {
        WebSocketSession session = userSessions.get(merchantId);
        sendMessageBath(messages, session);
    }

    //查询用户是否建立连接
    public static boolean isUserConnected(Long userId) {
        return userSessions.containsKey(userId);
    }

    //查询客服是否建立连接
    public static boolean isCustomerServiceConnected(Long merchantId) {
        return customerServiceSessions.containsKey(merchantId);
    }
}

