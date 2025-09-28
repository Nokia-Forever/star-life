package com.zfh.exception;

/**
 * 博客异常
 */
public class BlogException extends BaseException{
    public BlogException(String message) {
        super(message);
    }

    public BlogException(String message, Throwable cause) {
        super(message, cause);
    }
}
