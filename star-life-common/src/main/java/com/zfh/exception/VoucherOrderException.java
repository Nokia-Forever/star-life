package com.zfh.exception;

/**
 * 优惠券订单异常
 */
public class VoucherOrderException extends BaseException{
    public VoucherOrderException(String message) {
        super(message);
    }

    public VoucherOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
