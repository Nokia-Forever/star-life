package com.zfh.exception;

/**
 * 职员异常类
 */
public class StaffException extends BaseException{
    public StaffException(String message) {
        super(message);
    }

    public StaffException(String message, Throwable cause) {
        super(message, cause);
    }
}
