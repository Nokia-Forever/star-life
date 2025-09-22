package com.zfh.controller;

import com.zfh.dto.UserInfoDto;
import com.zfh.dto.UserPasswordDto;
import com.zfh.dto.UserRegisterDto;
import com.zfh.result.R;
import com.zfh.service.IUserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @description 用户控制层
 */
@RestController
@RequestMapping("/client/user")
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

    /**
     *  查询当前用户信息
     * @return
     */
    @GetMapping("/me")
    public R getCurrentInfo() {
        log.info("查询当前用户信息");
        return R.OK(userService.getCurrentUserInfo());
    }


    /**
     * 更新当前用户信息
     * @param userInfoDto
     * @return
     */
    @PutMapping("/me")
    public R updateCurrent(@Valid  @RequestBody UserInfoDto userInfoDto) {
        log.info("更新用户信息：{}", userInfoDto);
        return R.OK(userService.updateCurrent(userInfoDto));
    }

    /**
     * 更新当前用户密码
     * @param userPasswordDto
     * @return
     */
    @PutMapping("/me/password")
    public R updateCurrentPassword(@Validated @RequestBody UserPasswordDto userPasswordDto) {
        log.info("更新用户密码：{}", userPasswordDto);
        return R.OK(userService.updateCurrentPassword(userPasswordDto));
    }


    /**
     * 查询用户信息
     * @param id
     * @return
     */
    @GetMapping("/white/info/{id}")
    public R getUserInfo(@PathVariable Long id) {
        log.info("查询用户信息：{}", id);
        return R.OK(userService.getInfoById(id));
    }

}
