package com.weijin.partner.controller;

import com.weijin.partner.entity.User;
import com.weijin.partner.repository.UserRepository;
import com.weijin.partner.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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


    @ApiOperation(value = "")
    @GetMapping("list")
    public ResponseEntity list() {
        List<User> list;
        try {
            list = userService.list();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        return ResponseEntity.ok(list);
    }



}
