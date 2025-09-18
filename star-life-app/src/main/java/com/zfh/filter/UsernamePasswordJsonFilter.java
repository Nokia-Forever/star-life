package com.zfh.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.constant.ExceptionConstant;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.property.CaptchaProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * 登录JSon格式过滤器
 */

public class UsernamePasswordJsonFilter extends UsernamePasswordAuthenticationFilter {

    private final StringRedisTemplate stringRedisTemplate;
    private final CaptchaProperties captchaProperties;
    public UsernamePasswordJsonFilter(StringRedisTemplate stringRedisTemplate, CaptchaProperties captchaProperties) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.captchaProperties = captchaProperties;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 检查是否为POST请求
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(ExceptionConstant.REQUEST_ERROR);
        }

        // 检查Content-Type是否为JSON
        String contentType = request.getContentType();
        if (contentType == null || !contentType.contains("application/json")) {
            throw new AuthenticationServiceException(ExceptionConstant.REQUEST_ERROR);
        }

        String username = "";
        String password = "";
        String captcha = "";//验证码

        try {
            // 读取请求体中的JSON数据
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            // 解析JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(sb.toString());

            // 获取用户名和密码
            username = jsonNode.get("username") != null ? jsonNode.get("username").asText() : "";
            password = jsonNode.get("password") != null ? jsonNode.get("password").asText() : "";
            captcha = jsonNode.get("captcha") != null ? jsonNode.get("captcha").asText() : "";

        } catch (IOException e) {
            throw new AuthenticationServiceException(ExceptionConstant.REQUEST_ERROR);
        }
        //用户名为空
        if (username == null || username.isEmpty()) {
            throw new AuthenticationServiceException(ExceptionConstant.USERNAME_EMPTY);
        }


        //验证验证码
        verifyCaptcha(request, captcha);


        // 清理用户名前后空格
        username = username != null ? username.trim() : "";

        // 创建认证令牌
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);

        // 设置认证详情
        setDetails(request, authRequest);

        // 执行认证
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    // 验证验证码
    private void verifyCaptcha(HttpServletRequest request, String captcha) {
        if(!captchaProperties.isUserEnable()){
            return;
        }
        //验证码为空
        if (captcha == null || captcha.isEmpty()) {
            throw new AuthenticationServiceException(ExceptionConstant.CAPTCHA_ERROR);
        }

        //验证码不正确
        //获取clientId
        String clientId = request.getHeader("clientId");
        if (clientId == null || clientId.isEmpty()) {
            throw new AuthenticationServiceException(ExceptionConstant.REQUEST_ERROR);
        }
        String redisCaptcha = stringRedisTemplate.opsForValue().get(RedisKeyConstant.USER_CAPTCHA_KEY + clientId);
        if(redisCaptcha== null){
            throw new AuthenticationServiceException(ExceptionConstant.CAPTCHA_TIMEOUT);
        }
        //验证码,不比较大小写
        if(!redisCaptcha.equalsIgnoreCase(captcha)){
            throw new AuthenticationServiceException(ExceptionConstant.CAPTCHA_ERROR);
        }
    }
}
