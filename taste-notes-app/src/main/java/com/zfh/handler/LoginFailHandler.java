package com.zfh.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.result.R;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 登录失败处理
 */
@Component
public class LoginFailHandler implements AuthenticationFailureHandler {
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //写出异常,设置请求头为application/json,响应状态码为400,响应内容是异常信息
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        //转为 json
        response.getWriter().write(objectMapper.writeValueAsString(R.FAIL(exception.getMessage())));
    }
}
