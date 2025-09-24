package com.zfh.exception;

/**
 * 商铺异常
 */
public class ShopException extends BaseException{

    public ShopException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShopException(String message) {
        super(message);
    }
}
