package com.weijin.partner.controller;

import com.weijin.partner.config.MsgConstant;
import com.weijin.partner.exception.AccountStartException;
import com.weijin.partner.exception.IvalidPermissionException;
import com.weijin.partner.shiro.ShiroDbRealm;
import com.weijin.partner.shiro.ShiroUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class LoginController {

    private static Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    /**
     * 登录过滤器放行后进入此接口
     *
     * @param request 携带成功或失败的request
     * @return 返回响应
     */
    @PostMapping(value = "/login")
    public ResponseEntity loginAjax(HttpServletRequest request, String username, String password) {
        if (request.getAttribute("success") != null && (boolean) request.getAttribute("success")) {
            ShiroDbRealm.ShiroUser currentUser = ShiroUtil.getCurrentUser();
            return ResponseEntity.ok(currentUser);
        }
        // 登录失败从request中获取shiro处理的异常信息。
        String message = MsgConstant.LOGIN_ERRROR;
        String error = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        LOGGER.info("登陆失败的原因:{}", error);
        if (error != null) {
            if (error.equals(IncorrectCredentialsException.class.getName())) {
                message = MsgConstant.CREDENTIAL_ERROR;
            } else if (error.equals(ExpiredCredentialsException.class.getName())) {
                message = MsgConstant.ACCOUNTEXPIRED;
            } else if (error.equals(AccountStartException.class.getName())) {
                message = MsgConstant.ACCOUNTSTART;
            } else if (error.equals(ExcessiveAttemptsException.class.getName())) {
                message = MsgConstant.LOCKING;
            } else if (error.equals(DisabledAccountException.class.getName())) {
                message = MsgConstant.UNENABLE;
            } else if (error.equals(IvalidPermissionException.class.getName())) {
                message = MsgConstant.INVALID_PERMISSION;
            }
        }
        return ResponseEntity.badRequest().body(message);
    }

    @GetMapping(value = "login")
    private ModelAndView login(ModelAndView modelAndView) {
        modelAndView.setViewName("login");
        return modelAndView;
    }

    /**
     * 退出系统
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/logout")
    @ResponseBody
    public ResponseEntity logout() throws Exception {
        Subject subject = SecurityUtils.getSubject();
       /* if (subject.isAuthenticated()) {
            subject.logout();
            return "logout->success";
        }*/
        try {
            subject.logout();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(e.getMessage());
        }
        return ResponseEntity.ok(MsgConstant.OPERATION_SUCCESS);
    }

    /**
     * 没有权限
     *
     * @param response
     */
    @GetMapping(value = "/unauthorized")
    public void unauthorized(HttpServletResponse response) {
        ShiroUtil.writeResponse(response, "对不起,您没有访问权限！");
    }
}
