package com.zfh.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.entity.User;
import com.zfh.property.JwtProperties;
import com.zfh.result.R;
import com.zfh.utils.JwtUtils;
import com.zfh.vo.UserVo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录成功处理器
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //获取当前实体对象id
        User user = ((User)authentication.getPrincipal());
        Map<String, Object> claims = Map.of("id", user.getId());

        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);

        Map<String,String> map= new HashMap<>();
        objectMapper.convertValue(userVo, Map.class).forEach((k, v)->{
            //不保存token
            if("token".equals(k)){
                return;
            }
            map.put(k.toString(), v==null?null:v.toString());
        });

        //将用户信息保存在redis中
        stringRedisTemplate.opsForHash().putAll(RedisKeyConstant.USER_TOKEN_KEY + user.getId(), map);
        stringRedisTemplate.expire(RedisKeyConstant.USER_TOKEN_KEY + user.getId(), RedisKeyConstant.USER_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        //登录成功生成 token
        String token = JwtUtils.generateToken(jwtProperties.getUserSecret(),  claims );
        response.setHeader(jwtProperties.getUserTokenName(), token);
        userVo.setToken(token);

        //设置响应格式
        response.setContentType("application/json;charset=UTF-8");
        //返回登录成功信息
        response.getWriter().write(objectMapper.writeValueAsString(R.OK(userVo)));
    }
}
