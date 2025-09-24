package com.zfh.utils;

import com.zfh.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 获取当前线程登录用户信息
 */
public class CurrentHolder {
    /*
     * 获取当前线程登录用户信息
     */
    public static User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    /**
     * 获取当前线程登录用户权限(店铺id,角色名)
     */
    public static Map<Long , String> getCurrentUserAuthorityStr() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return authorities.stream().collect(Collectors.toMap(
                //获取商铺id
                authority->  Long.parseLong(authority.getAuthority().split("_")[1])
                ,
                //获取角色名
                authority-> authority.getAuthority().split("_")[2]
        ));
    }

}
