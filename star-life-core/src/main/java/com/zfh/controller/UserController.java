package com.zfh.controller;

import com.zfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description 用户控制层
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


}
