package com.weijin.partner.shiro;

import com.weijin.partner.entity.User;
import com.weijin.partner.repository.UserRepository;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Objects;

/**
 * 身份校验核心类
 *
 * @author yindawei
 * @date 2017年12月13日
 */
@Slf4j
public class ShiroDbRealm extends AuthorizingRealm {

    @Resource
    private UserRepository userRepository;
    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("===============进行权限配置================");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        return authorizationInfo;
    }

    /**
     * 认证身份
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("===============进行登陆认证================");
        UsernamePasswordToken myToken = (UsernamePasswordToken) token;
        User checkUser = userRepository.findByUsername(myToken.getUsername());
        if (checkUser == null) {
            log.debug("user {} is not exist.", myToken.getUsername());
            throw new IncorrectCredentialsException();
        }
        ShiroUser currentUser = new ShiroUser();
        BeanUtils.copyProperties(checkUser,currentUser);
        if(!checkUser.getIfEnable()){
            log.debug("user {} is not enable.", myToken.getUsername());
            throw new DisabledAccountException();
        }
        return new SimpleAuthenticationInfo(currentUser, checkUser.getPassword(), getName());
    }

    @Override
    public void onLogout(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
        User user = (User) principals.getPrimaryPrincipal();
        removeUserCache(user);
    }

    /**
     * 清除用户缓存
     */
    public void removeUserCache(User user) {
        removeUserCache(user.getUsername());
    }

    /**
     * 清除用户缓存
     *
     * @param loginName
     */
    public void removeUserCache(String loginName) {
        SimplePrincipalCollection principals = new SimplePrincipalCollection();
        principals.add(loginName, super.getName());
        super.clearCachedAuthenticationInfo(principals);
    }

    /**
     * 自定义Authentication对象，使得Subject除了携带用户的登录名外还可以携带更多信息.
     */
    @Data
    @Accessors(chain = true)
    public static class ShiroUser implements Serializable {
        private static final long serialVersionUID = -5479583557463219088L;
        private Long id;
        private String username;
        private String name;

        /**
         * 本函数输出将作为默认的<shiro:principal/>输出.
         */
        @Override
        public String toString() {
            return username;
        }

        /**
         * 重载hashCode,只计算loginName;
         */
        @Override
        public int hashCode() {
            return Objects.hashCode(username);
        }

        /**
         * 重载equals,只计算loginName;
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ShiroUser other = (ShiroUser) obj;
            if (username == null) {
                if (other.username != null) {
                    return false;
                }
            } else if (!username.equals(other.username)) {
                return false;
            }
            return true;
        }
    }

}
