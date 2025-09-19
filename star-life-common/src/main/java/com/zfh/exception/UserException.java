package com.zfh.exception;

/**
 * 用户异常
 */
public class UserException extends BaseException{
    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}
