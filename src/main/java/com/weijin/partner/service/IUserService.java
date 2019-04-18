package com.weijin.partner.service;

import com.weijin.partner.entity.User;

import java.util.List;

/**
 * @author yindwe@yonyu.com
 * @Date 2019/4/18 14:40
 * @Description
 */
public interface IUserService {
    String getNameById(Long id);

    Long getIdByName(String name);

    List<User> list();
}
