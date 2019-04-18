package com.weijin.partner.controller;

import com.alibaba.fastjson.JSONObject;
import com.weijin.partner.config.SpringWebSocketHandler;
import com.weijin.partner.entity.Message;
import com.weijin.partner.entity.User;
import com.weijin.partner.service.IMessageService;
import com.weijin.partner.service.IUserService;
import com.weijin.partner.shiro.ShiroDbRealm;
import com.weijin.partner.shiro.ShiroUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ydw
 * Created on 2019/1/3
 */
@RestController
@RequestMapping(value = "websocket")
public class WebsocketController {

    @Resource
    private IUserService userService;
    @Resource
    private SpringWebSocketHandler springWebSocketHandler;
    @Resource
    private IMessageService messageService;

    @GetMapping("/onLineUsers")
    public Set<String> onLineUsers2() {
        ShiroDbRealm.ShiroUser currentUser = ShiroUtil.getCurrentUser();
        Map<Long, WebSocketSession> map = SpringWebSocketHandler.USER_SOCKET_SESSION_MAP;
        Set<Long> set = map.keySet();
        Set<String> names = new HashSet<>();
        set.forEach(id ->{
            String name = userService.getNameById(id);
            String user = currentUser.getName();
            if (!user.equals(name)) {
                names.add(name);
            }
        });
        return names;
    }


    /**
     * 发布系统广播（群发）
     */
    @ResponseBody
    @RequestMapping(value = "broadcast", method = RequestMethod.POST)
    public void broadcast(@RequestParam("text") String text) throws IOException {
        Message msg = new Message();
        msg.setDate(Calendar.getInstance().getTime());
        //-1表示系统广播
        msg.setFromId(-1L);
        msg.setFromName("系统广播");
        msg.setToId(0L);
        msg.setText(text);
        springWebSocketHandler.broadcast(new TextMessage(JSONObject.toJSONBytes(msg)));
    }

    @GetMapping("getUid")
    @ResponseBody
    public User getUid(@RequestParam("name") String name) {
        Long id = userService.getIdByName(name);
        User user = new User();
        user.setId(id);
        return user;
    }

    /*@GetMapping("getHistoryMessage")
    public ApiResult getHistoryMessage(Integer pageNum, Integer pageSize, Long uid, HttpSession httpSession) {
        UserOutputDTO currentUser = this.getCurrentUser(httpSession);
        Page<Message> page;
        try {
            page = PageHelper.startPage(pageNum + 1, pageSize, true);
            messageService.getHistoryMessage(currentUser.getId(), uid);
        } catch (Exception e) {
            return error("查询为空");
        }
        return successPages(page);
    }*/
}
