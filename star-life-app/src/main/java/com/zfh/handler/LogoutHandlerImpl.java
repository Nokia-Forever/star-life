package com.zfh.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.constant.ExceptionConstant;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.entity.User;
import com.zfh.result.R;
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
        //写回信息
        response.setContentType("application/json;charset=UTF-8");

        if (authentication == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(objectMapper.writeValueAsString(R.FAIL(ExceptionConstant.USER_NOT_LOGIN)));
            return;
        }
        //在redis移除当前用户的信息
        User user = (User) authentication.getPrincipal();
        stringRedisTemplate.delete(RedisKeyConstant.USER_TOKEN_KEY + user.getId());

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(R.OK()));
    }
}
