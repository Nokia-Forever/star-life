package com.zfh.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfh.result.R;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * http工具类
 */
public class HttpUtils {
    /**
     * json格式写回错误信息
     */
    public static void writeFailJson(HttpServletResponse response,  String msg, ObjectMapper objectMapper) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(objectMapper.writeValueAsString(R.FAIL(msg)));
    }

    /**
     * json格式写回成功信息
     */
    public static void writeSuccessJson(HttpServletResponse response, Object data, ObjectMapper objectMapper) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(R.OK(data)));
    }
}
