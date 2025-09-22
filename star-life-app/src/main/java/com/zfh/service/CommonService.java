package com.zfh.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 通用服务接口
 */

public interface CommonService {
    /**
     * 生成验证码
     * @param request
     * @param response
     */
    void captcha(HttpServletRequest request, HttpServletResponse response);
}
