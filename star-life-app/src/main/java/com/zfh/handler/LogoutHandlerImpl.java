package com.zfh.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.constant.ExceptionConstant;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.entity.User;
import com.zfh.utils.HttpUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 登出成功处理
 */
@Component
public class LogoutHandlerImpl implements LogoutSuccessHandler {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication == null) {
            HttpUtils.writeFailJson(response, ExceptionConstant.USER_NOT_LOGIN, objectMapper);
            return;
        }
        //在redis移除当前用户的信息
        User user = (User) authentication.getPrincipal();
        stringRedisTemplate.delete(RedisKeyConstant.USER_TOKEN_KEY + user.getId());

        HttpUtils.writeSuccessJson(response, "退出成功" , objectMapper);
    }
}
