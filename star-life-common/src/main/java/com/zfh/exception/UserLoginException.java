package com.zfh.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 用户登录异常
 */
public class UserLoginException extends AuthenticationException {
    public UserLoginException(String message) {
        super(message);
    }

}
