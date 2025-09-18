package com.zfh.handler;

import com.zfh.exception.BaseException;
import com.zfh.result.R;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 处理自定义异常
    @ExceptionHandler(BaseException.class)
    public R handleBaseException(BaseException e) {
        return R.FAIL(e.getMessage());
    }

    /**
     * 参数校验异常
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return R.FAIL(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    // 处理其他异常
    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        return R.FAIL(e.getMessage());
    }

}
