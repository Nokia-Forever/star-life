package com.zfh.utils;

import com.zfh.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 获取当前线程登录用户信息
 */
public class CurrentHolder {
    public static User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
