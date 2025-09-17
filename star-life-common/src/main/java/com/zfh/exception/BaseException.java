package com.zfh.exception;

/**
 * 自定义异常
 */
public class BaseException extends RuntimeException{
    public BaseException(String message) {
        super(message);
    }
    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
