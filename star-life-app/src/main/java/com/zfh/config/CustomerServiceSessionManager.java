package com.zfh.config;


import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CustomerServiceSessionManager  客服管理器
 */
public class CustomerServiceSessionManager {
    // 客户会话Map
    private static final ConcurrentHashMap<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    //商家会话 Map
    private static final ConcurrentHashMap<String, WebSocketSession> customerServiceSessions = new ConcurrentHashMap<>();
    //商家客服空闲map Map<商家id, 商家客服id列表>
    private static final ConcurrentHashMap<String, List<String>> merchantServiceMap = new ConcurrentHashMap<>();

    //添加客户
    public static void addUserSession(String userId, WebSocketSession session) {
        userSessions.put(userId, session);
    }

    //添加客服
    public static void addCustomerServiceSession(String shopId,String merchantId, WebSocketSession session) {
        customerServiceSessions.put(merchantId, session);
        if(merchantServiceMap.containsKey(shopId)){
            merchantServiceMap.get(shopId).add(merchantId);
        }else {
            merchantServiceMap.put(shopId, List.of(merchantId));
        }
    }

    //获取一个客服id
    public static String getOneCustomerService(String shopId) {
        //没有客服返回 null
        if(merchantServiceMap.get(shopId) == null || merchantServiceMap.get(shopId).isEmpty()){
            return null;
        }
        String merchantId = merchantServiceMap.get(shopId).getFirst();
        merchantServiceMap.get(shopId).removeFirst();
        return merchantId;
    }

    //客服重回空闲
    public static void CustomerServiceToRelax(String shopId,String merchantId) {
        merchantServiceMap.get(shopId).add(merchantId);
    }

    // 移除用户连接
    public static void removeUserSession(String userId) {
        userSessions.remove(userId);
    }

    //移除客服连接
    public static void removeCustomerServiceSession(String merchantId) {
        customerServiceSessions.remove(merchantId);
        merchantServiceMap.get(merchantId).remove(merchantId);
    }

    //TODO 注意记忆持久化
    //TODO 未成功实现

    //客服发送给用户
    public static void sendToUser(String userId, String message) throws IOException {
        WebSocketSession session = customerServiceSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //用户发送给客服
    public static void sendToCustomerService(String merchantId, String message) throws IOException {
        WebSocketSession session = userSessions.get(merchantId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

