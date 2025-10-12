package com.zfh.exception;

/**
 * 优惠券异常
 */
public class VoucherException extends BaseException{
    public VoucherException(String message) {
        super(message);
    }

    public VoucherException(String message, Throwable cause) {
        super(message, cause);
    }
}
