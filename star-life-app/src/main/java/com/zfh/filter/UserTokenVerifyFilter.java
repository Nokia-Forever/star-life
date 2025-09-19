package com.zfh.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.constant.ExceptionConstant;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.constant.URLConstant;
import com.zfh.entity.User;
import com.zfh.exception.UserLoginException;
import com.zfh.property.JwtProperties;
import com.zfh.utils.HttpUtils;
import com.zfh.utils.JwtUtils;
import com.zfh.vo.UserLoginVo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户token验证过滤器
 */
@Component
public class UserTokenVerifyFilter extends OncePerRequestFilter {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //只校验用户请求,放行登录,注册
        String requestURI =request.getRequestURI();
        if (!requestURI.startsWith("/user")
                ||requestURI.equals(URLConstant.USER_LOGIN_URL)
                ||requestURI.equals(URLConstant.USER_REGISTER_URL)
        ) {
            filterChain.doFilter(request, response);
            return;
        }


        try {
            //获取token
            String token = request.getHeader(jwtProperties.getUserTokenName());
            if (token == null) {
                throw new RuntimeException("用户未登录");
            }

            String id;
            try {
                //解析并获取id
                id = JwtUtils.parseToken(jwtProperties.getUserSecret(), token).getPayload().get("id").toString();
            } catch (Exception e) {
                throw new UserLoginException(ExceptionConstant.USER_NOT_LOGIN);
            }

            //在redis获取所有信息
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(RedisKeyConstant.USER_TOKEN_KEY + id);
            if(entries.isEmpty()){
                throw new UserLoginException(ExceptionConstant.USER_NOT_LOGIN);
            }
            //先进行tokenHash的判断
            if(!DigestUtils.sha256Hex(token).equals(entries.get("token").toString())){
                throw new UserLoginException(ExceptionConstant.USER_LOGIN_ELSEWHERE);
            }


            //重置有效时间
            stringRedisTemplate.expire(RedisKeyConstant.USER_TOKEN_KEY + id, RedisKeyConstant.USER_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
            //将当前用户信息放入springSecurity框架中
            UserLoginVo userLoginVo = objectMapper.convertValue(entries, UserLoginVo.class);
            User user = new User();
            BeanUtils.copyProperties(userLoginVo, user);

            //把对象放入Security框架
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken( user, null, AuthorityUtils.NO_AUTHORITIES));
            filterChain.doFilter(request, response);
        } catch (RuntimeException | IOException | ServletException e) {
            HttpUtils.writeFailJson(response, e.getMessage(), objectMapper);
        }

    }
}
