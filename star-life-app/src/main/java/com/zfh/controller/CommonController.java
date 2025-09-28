package com.zfh.controller;

import com.zfh.result.R;
import com.zfh.service.CommonService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用类接口
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private CommonService commonService;


    /**
     * 验证码
     *
     * @param request
     * @param response
     */
    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) {
       commonService.captcha(request, response);
    }


    /**
     * 图片上传
     * @param file
     * @return
     */
    @PostMapping("/upload/image")
    public R uploadImage(MultipartFile file) {
        return R.OK( commonService.uploadImage(file));
    }
}
