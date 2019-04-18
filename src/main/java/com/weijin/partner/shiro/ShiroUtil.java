package com.weijin.partner.shiro;

import com.alibaba.fastjson.JSON;
import org.apache.shiro.SecurityUtils;
import org.springframework.http.ResponseEntity;
import com.weijin.partner.shiro.ShiroDbRealm.ShiroUser;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class ShiroUtil {

    /**
     * 统一返回前端json数据
     *
     * @param response
     */
    public static void writeResponse(HttpServletResponse response, String message) {
        try {
            response.setContentType("application/json");
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(JSON.toJSONString(ResponseEntity.status(-8).body(message)).getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static ShiroUser getCurrentUser(){
        return (ShiroUser) SecurityUtils.getSubject().getPrincipal();
    }
}