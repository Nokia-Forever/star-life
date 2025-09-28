package com.zfh.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ICaptcha;
import com.zfh.constant.ExceptionConstant;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.exception.BaseException;
import com.zfh.exception.UploadException;
import com.zfh.property.AliOssProperties;
import com.zfh.property.CaptchaProperties;
import com.zfh.service.CommonService;
import com.zfh.utils.AliOssUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 通用服务实现类
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CaptchaProperties captchaProperties;
    @Autowired
    private AliOssProperties aliOssProperties;
    /**
     * 生成验证码
     * @param request
     * @param response
     */
    @Override
    public void captcha(HttpServletRequest request, HttpServletResponse response) {
        if(!captchaProperties.isUserEnable()){
            return;
        }
        //四种验证码都可以进行指定生成验证码的规则
        //圆圈验证码,参数:长度,高度,字符个数,干扰圆圈数,字体高度(高度的倍数)
        //ICaptcha captcha = CaptchaUtil.createCircleCaptcha(90, 30, 4, 10, 1.0f);
        //GIF验证码,参数:长度,高度,字符个数,验证码干扰元素个数,字体高度(高度的倍数)
        //ICaptcha captcha = CaptchaUtil.createGifCaptcha(90, 30, 4, 10, 1.0f);
        //线验证码,参数:长度,高度,字符个数,干扰线数,字体高度(高度的倍数)
        //ICaptcha captcha = CaptchaUtil.createLineCaptcha(90, 30, 4, 10, 1.0f);
        //扭曲验证码,参数:长度,高度,字符个数,干扰线宽度,字体高度(高度的倍数)
        ICaptcha captcha = CaptchaUtil.createShearCaptcha(90, 30, captchaProperties.getUserLength(), 2, 1.0f);

        //获取前端唯一clientId
        String clientId = request.getHeader("clientId");
        if(clientId == null){
            throw new BaseException(ExceptionConstant.CAPTCHA_GET_FAILED);
        }

        //获取验证码,并存放到redis中
        stringRedisTemplate.opsForValue()
                .set(RedisKeyConstant.USER_CAPTCHA_KEY + clientId, captcha.getCode(),RedisKeyConstant.USER_CAPTCHA_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        //定义验证码返回的响应格式
        response.setContentType("image/png");

        //输出验证码
        try {
            captcha.write(response.getOutputStream());
        } catch (IOException e) {
            throw new BaseException(ExceptionConstant.CAPTCHA_GET_FAILED);
        }
    }

    /**
     * 上传图片
     * @param file
     * @return
     */
    @Override
    public String uploadImage(MultipartFile file) {
       //只用图片类可以上传
        String contentType = file.getContentType();
        if(contentType ==null||!contentType.startsWith("image/")){
            throw new UploadException(ExceptionConstant.IMAGE_FORMAT_ERROR);
        }
        String url = null;
        try {
            url = new AliOssUtil(aliOssProperties).upload(file.getBytes(),file.getOriginalFilename());
        } catch (Exception e) {
            throw new RuntimeException(ExceptionConstant.IMAGE_UPLOAD_FAILED);
        }
        return url;
    }
}
