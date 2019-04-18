package com.weijin.partner.repository;

import com.weijin.partner.entity.User;
import org.springframework.data.repository.CrudRepository;

/**
 * @author yindwe@yonyu.com
 * @Date 2019/4/18 14:51
 * @Description
 */
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

    User findByName(String name);
}
