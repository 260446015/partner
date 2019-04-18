package com.weijin.partner.service.impl;

import com.weijin.partner.entity.User;
import com.weijin.partner.repository.UserRepository;
import com.weijin.partner.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yindwe@yonyu.com
 * @Date 2019/4/18 14:50
 * @Description
 */
@Service
public class UserServiceImpl implements IUserService {
    @Resource
    private UserRepository userRepository;
    @Override
    public String getNameById(Long id) {
        return userRepository.findById(id).get().getName();
    }

    @Override
    public Long getIdByName(String name) {
        return userRepository.findByName(name).getId();
    }

    @Override
    public List<User> list() {
        return (List<User>) userRepository.findAll();
    }
}
