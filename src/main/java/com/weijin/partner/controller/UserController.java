package com.weijin.partner.controller;

import com.weijin.partner.repository.UserRepository;
import com.weijin.partner.service.IUserService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ydw
 * Created on 2019/1/3
 */
@RestController
@RequestMapping("/apis/user")
@Api(description = "用户接口")
public class UserController {

    @Resource
    private IUserService userService;
    private UserRepository userRepository;






}
