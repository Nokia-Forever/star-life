package com.zfh.config;

import com.zfh.handler.CustomerServiceWebSocketHandler;
import com.zfh.interceptor.CustomerServiceWebSocketHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置类
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private CustomerServiceWebSocketHandler customerServiceWebSocketHandler;
    @Autowired
    private CustomerServiceWebSocketHandshakeInterceptor customerServiceWebSocketHandshakeInterceptor;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customerServiceWebSocketHandler, "/customerService")//客服url
                //TODO 允许跨域, 生产环境请根据实际情况进行配置
                .setAllowedOrigins("*")
                .addInterceptors(customerServiceWebSocketHandshakeInterceptor);
    }
}
