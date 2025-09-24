package com.zfh.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.config.URLConfig;
import com.zfh.constant.ExceptionConstant;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.constant.UserConstant;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户token验证过滤器
 */
@Component
public class UserTokenVerifyFilter extends OncePerRequestFilter {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private URLConfig urlConfig;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //只校验用户请求,放行登录,注册
        String requestURI =request.getRequestURI();
        //不是客户端请求或在客户端请求白名单中则放行
        if (!requestURI.startsWith("/client")
                || urlConfig.CLIENT_WHITE_URL_LIST.stream().anyMatch((pattern -> antPathMatcher.match(pattern, requestURI)))
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

            List<GrantedAuthority>  authorities = new ArrayList<>();

            //商家查询职称
            if(user.getUserType().equals(UserConstant.USER_TYPE_BUSINESS)){
                Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(RedisKeyConstant.USER_SHOP_ROLE_KEY + user.getId());
                if(!map.isEmpty()){
                    //获取角色名称并拼接
                    map.forEach((key, value) -> {
                        String name = stringRedisTemplate.opsForHash().get(RedisKeyConstant.STAFF_ROLE_KEY + value.toString(), "name").toString();
                        authorities.add(new SimpleGrantedAuthority("ROLE_"+key.toString()+"_"+name));
                    });
                }
            }
            //把对象放入Security框架
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken( user, null, authorities));
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            filterChain.doFilter(request, response);
        } catch (RuntimeException | IOException | ServletException e) {
            HttpUtils.writeFailJson(response, e.getMessage(), objectMapper);
        }

    }
}
