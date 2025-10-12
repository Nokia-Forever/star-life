package com.zfh.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.config.CustomerServiceSessionManager;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.constant.UserConstant;
import com.zfh.entity.Staff;
import com.zfh.entity.User;
import com.zfh.property.JwtProperties;
import com.zfh.service.IStaffService;
import com.zfh.service.IUserService;
import com.zfh.utils.HttpUtils;
import com.zfh.utils.JwtUtils;
import com.zfh.vo.UserLoginVo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    @Autowired
    private IStaffService staffService;
    @Autowired
    @Lazy
    private IUserService userService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //获取当前实体对象id
        User user = ((User)authentication.getPrincipal());
        Map<String, Object> claims = Map.of("id", user.getId());

        UserLoginVo userLoginVo = new UserLoginVo();

        user.setLastLoginTime(new Date());
        //更新最后登录时间
        userService.update(new LambdaUpdateWrapper<User>()
                .set(User::getLastLoginTime, new Date())
                .eq(User::getId, user.getId()));
        BeanUtils.copyProperties(user, userLoginVo);


        //登录成功生成 token
        String token = JwtUtils.generateToken(jwtProperties.getUserSecret(),  claims );
        response.setHeader(jwtProperties.getUserTokenName(), token);
        userLoginVo.setToken(token);

        Map<String,String> map= new HashMap<>();
        objectMapper.convertValue(userLoginVo, Map.class).forEach((k, v)->{
            //生成hash函数
            if("token".equals(k)){
                v =DigestUtils.sha256Hex(v.toString());
            }
            map.put(k.toString(), v==null?null:v.toString());
        });

        //将用户信息保存在redis中
        stringRedisTemplate.opsForHash().putAll(RedisKeyConstant.USER_TOKEN_KEY + user.getId(), map);
        stringRedisTemplate.expire(RedisKeyConstant.USER_TOKEN_KEY + user.getId(), RedisKeyConstant.USER_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        //如果是商家,存储对应的职称
        if(user.getUserType().equals(UserConstant.USER_TYPE_BUSINESS)){
            //查询职称表,并存储在redis 中
            List<Staff> staffList = staffService.list(new QueryWrapper<Staff>().eq("user_id", user.getId()));
            if(!staffList.isEmpty()){
                Map<String, String> staffMap = new HashMap<>();
                staffList.forEach(staff -> {
                    staffMap.put(staff.getShopId().toString(), staff.getRoleId().toString());
                });
                stringRedisTemplate.opsForHash().putAll(RedisKeyConstant.USER_SHOP_ROLE_KEY + user.getId(), staffMap);
                stringRedisTemplate.expire(RedisKeyConstant.USER_SHOP_ROLE_KEY + user.getId(), RedisKeyConstant.USER_SHOP_ROLE_EXPIRE_TIME, TimeUnit.MILLISECONDS);
            }
        }

        //移除已建立的webSocket连接
        CustomerServiceSessionManager.removeUserSession(user.getId().toString());

        //响应数据
        HttpUtils.writeSuccessJson(response, userLoginVo, objectMapper);
    }
}
