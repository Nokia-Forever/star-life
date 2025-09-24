package com.zfh.handler;

import com.zfh.enumeration.CodeEnum;
import com.zfh.exception.BaseException;
import com.zfh.exception.UserException;
import com.zfh.result.R;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
//TODO 全局异常没配置完毕
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

    /**
     * 用户异常
     * @param e
     * @return
     */
    @ExceptionHandler(UserException.class)
    public R handleUserException(UserException e) {
        return R.FAIL(e.getMessage());
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R handleAccessDeniedException(AccessDeniedException e) {
        return R.FAIL(CodeEnum.FORBIDDEN);
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R handleAuthenticationException(AuthenticationException e) {
        return R.FAIL(CodeEnum.UNAUTHORIZED);
    }



    /**
     * 全局异常
      */

    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        return R.FAIL(e.getMessage());
    }

}
