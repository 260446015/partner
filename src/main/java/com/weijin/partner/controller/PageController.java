package com.weijin.partner.controller;

import com.weijin.partner.service.IUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author yindwe@yonyu.com
 * @Date 2019/4/18 16:20
 * @Description
 */
@RestController
@RequestMapping("page")
public class PageController {

    @Resource
    private IUserService userService;
    @GetMapping("{page}")
    public ModelAndView toPage(@PathVariable String page){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(page);
        return modelAndView;
    }

    @GetMapping("index/{uid}")
    public ModelAndView index(HttpSession httpSession, @PathVariable(name = "uid") Long uid, ModelAndView modelAndView) {
        httpSession.setAttribute("uid", uid);
        String name = userService.getNameById(uid);
        httpSession.setAttribute("name", name);
        modelAndView.setViewName("/room");
        return modelAndView;
//        modelAndView.addObject("uid", uid);
//        String name = userService.getNameById(uid);
//        modelAndView.addObject("name", name);
//        modelAndView.setViewName("/room");
//        return modelAndView;
    }
}
