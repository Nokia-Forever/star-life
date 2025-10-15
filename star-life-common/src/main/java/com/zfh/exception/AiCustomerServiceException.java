package com.zfh.exception;

public class AiCustomerServiceException extends BaseException{
    public AiCustomerServiceException(String message) {
        super(message);
    }

    public AiCustomerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
