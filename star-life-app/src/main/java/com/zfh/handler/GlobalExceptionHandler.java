package com.zfh.handler;

import com.zfh.exception.BaseException;
import com.zfh.result.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseException.class)
    public R handleBaseException(BaseException e) {
        return R.FAIL(e.getMessage());
    }

}
