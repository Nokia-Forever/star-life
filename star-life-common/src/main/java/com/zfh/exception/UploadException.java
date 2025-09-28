package com.zfh.exception;

/**
 * 上传文件异常
 */
public class UploadException extends BaseException{
    public UploadException(String message) {
        super(message);
    }

    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
