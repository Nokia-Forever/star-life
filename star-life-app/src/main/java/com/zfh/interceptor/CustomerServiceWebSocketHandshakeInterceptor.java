package com.zfh.interceptor;

import com.zfh.constant.CustomerServiceConstant;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.constant.UserConstant;
import com.zfh.property.JwtProperties;
import com.zfh.service.IShopService;
import com.zfh.utils.JwtUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket 客服握手拦截器
 */
@Component
public class CustomerServiceWebSocketHandshakeInterceptor implements HandshakeInterceptor {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IShopService shopService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws IOException {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String typeStr = servletRequest.getServletRequest().getParameter("type");

            //不能为false
            if(typeStr==null||typeStr.isBlank()){
                return false;
            }

            Integer type = Integer.parseInt(typeStr);
            //验证用户 token并获取用户id
            Long userId = verifyUserToken(servletRequest);
            if (userId==-1L) {
                return false;
            }
            //验证是否为客服连接
            if (!verifyCustomerService(userId,type)) {
                return false;
            }
            //重置有效时间
            stringRedisTemplate.expire(RedisKeyConstant.USER_TOKEN_KEY + userId, RedisKeyConstant.USER_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
            //放入属性
            attributes.put("userId", userId);
            attributes.put("type", type);
            return true;
        }
        return false;
    }


    //验证客服链接权限
    private boolean verifyCustomerService(Long userId,Integer type) {
        //用户无需验证
        if (type== CustomerServiceConstant.USER) {
            return true;
        }

        //查询他是否是商家
        String userType = (String) stringRedisTemplate.opsForHash().get(RedisKeyConstant.USER_TOKEN_KEY + userId, "userType");
        return userType!=null&&Long.parseLong(userType) == UserConstant.USER_TYPE_BUSINESS;
    }

    //验证用户token,并获取用户id
    private Long verifyUserToken(ServletServerHttpRequest servletRequest) {
        //获取token
        String token = servletRequest.getServletRequest().getHeader(jwtProperties.getUserTokenName());
        //无token
        if (token == null) {
            return -1L;
        }

        //解析并获取id
        String id;
        try {
            id = JwtUtils.parseToken(jwtProperties.getUserSecret(), token).getPayload().get("id").toString();
        } catch (Exception e) {
            return -1L;
        }

        //在redis获取token
        Object redisToken = stringRedisTemplate.opsForHash().get(RedisKeyConstant.USER_TOKEN_KEY + id, "token");
        if (redisToken == null) {
            return -1L;
        }
        String redisTokenStr = redisToken.toString();

        //先进行tokenHash的判断
        if (!DigestUtils.sha256Hex(token).equals(redisTokenStr)) {
            return -1L;
        }

        return Long.parseLong(id);
    }

    @Override
    public void afterHandshake(org.springframework.http.server.ServerHttpRequest request,
                               org.springframework.http.server.ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}

