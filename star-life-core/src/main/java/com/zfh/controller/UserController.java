package com.zfh.controller;

import com.zfh.dto.UserRegisterDto;
import com.zfh.result.R;
import com.zfh.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description 用户控制层
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private IUserService userService;

    /**
     * 用户注册
     * @param userRegisterDto
     * @return
     */
    @PostMapping("/register")
    public R register( @RequestBody @Validated UserRegisterDto userRegisterDto) {
        log.info("用户注册：{}", userRegisterDto);
        return R.OK(userService.register(userRegisterDto));
    }
}
