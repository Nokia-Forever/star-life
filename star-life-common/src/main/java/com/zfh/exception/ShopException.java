package com.zfh.exception;

public class ShopException extends BaseException{

    public ShopException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShopException(String message) {
        super(message);
    }
}
